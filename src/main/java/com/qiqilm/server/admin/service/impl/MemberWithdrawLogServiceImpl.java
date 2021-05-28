package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.BankCardAddress;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.MemberWithdrawLogShunWei;
import com.qiqilm.server.admin.domain.req.ReqMemberWithdrawLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberInfo;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.service.IBankCardAddressService;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UserDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员提现信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-30
 */
@Service
public class MemberWithdrawLogServiceImpl implements IMemberWithdrawLogService {
    @Autowired
    private MemberWithdrawLogMapper memberWithdrawLogMapper;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ILogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IBankCardAddressService bankCardAddressService;

    /**
     * 查询会员提现信息
     *
     * @param id 会员提现信息ID
     * @return 会员提现信息
     */
    @Override
    public MemberWithdrawLog selectMemberWithdrawLogById(String id) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogById(id);
    }

    /**
     * 查询会员提现信息列表
     *
     * @param memberWithdrawLog 会员提现信息
     * @return 会员提现信息
     */
    @Override
    public List<MemberWithdrawLog> selectMemberWithdrawLogList(MemberWithdrawLog memberWithdrawLog) {
        List<MemberWithdrawLog> memberWithdrawLogList = memberWithdrawLogMapper.selectMemberWithdrawLogList(memberWithdrawLog);

        //查出会员状态是否为套利号
        if (!CollectionUtils.isEmpty(memberWithdrawLogList)) {
           List<String> memberIds  = new ArrayList<>();
            for (MemberWithdrawLog me : memberWithdrawLogList) {
                memberIds.add(me.getMemberId());
            }
            List<MemberWithdrawLog> Statuss = memberWithdrawLogMapper.selectMemberIdStatus(memberIds);
            for(MemberWithdrawLog me : memberWithdrawLogList){
                for(MemberWithdrawLog st:Statuss){
                    if(me.getMemberId().equals(st.getMemberId())){
                        me.setMemberStatus(st.getMemberStatus());
                    }
                }
            }
        }

        BankCardAddress bankCardAddress = new BankCardAddress();
        bankCardAddress.setStatus("1");
        List<BankCardAddress> bankCardAddresses = bankCardAddressService.selectBankCardAddressList(bankCardAddress);
        if (!CollectionUtils.isEmpty(memberWithdrawLogList) && !CollectionUtils.isEmpty(bankCardAddresses)) {
            for (MemberWithdrawLog me : memberWithdrawLogList) {
                if (!StringUtils.isEmpty(me.getRealBankAddress())) {
                    String[] arr = me.getRealBankAddress().split("/");
                    if (arr.length > 1) {
                        me.setProvince(arr[0]);
                        me.setCity(arr[1]);
                        for (BankCardAddress ba : bankCardAddresses) {
                            if (ba.getProvince().contains(me.getProvince())) {
                                if (ba.getCity().contains(me.getCity())) {
                                    //来到这里,是在黑名单中
                                    me.setCardBlack("1");
                                } else {
                                    me.setCardBlack("0");
                                }
                            }
                        }
                    } else {
                        me.setCardBlack("1");
                    }
                }
            }
        }
        //银行卡黑名单搜索
        if (!StringUtils.isEmpty(memberWithdrawLog.getSearchCardBlack())) {
            if (!CollectionUtils.isEmpty(memberWithdrawLogList)) {
                Iterator<MemberWithdrawLog> it = memberWithdrawLogList.iterator();
                if ("1".equals(memberWithdrawLog.getSearchCardBlack())) {
                    while (it.hasNext()) {
                        if ("0".equals(it.next().getCardBlack())) {
                            it.remove();
                        }
                    }
                } else {
                    while (it.hasNext()) {
                        if ("1".equals(it.next().getCardBlack())) {
                            it.remove();
                        }
                    }
                }
            }
        }
        return memberWithdrawLogList;
    }

    @Override
    public List<MemberWithdrawLogShunWei> selectMemberWithdrawLogShunWeiList(ReqMemberWithdrawLog req) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogShunWeiList(req.getIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult refused(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() == 2) {
            return AjaxResult.error("订单重复处理");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();

        String ip = UserDataUtil.getIp(ServletUtil.getHttpServletRequest());

        if (!StringUtils.isEmpty(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单只能由" + memberWithdrawLog.getOpName() + "处理");
        }
        if (!redisUtil.lock(EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }
        if (memberWithdrawLog.getStatus()<2 || memberWithdrawLog.getStatus()==5 || memberWithdrawLog.getStatus()==7 || memberWithdrawLog.getStatus()==8 ){
            memberWithdrawLog.setRemark(req.getRemark());
            memberWithdrawLog.setStatus(2);//审核不通过
            memberWithdrawLog.setOpName(userName);
            memberWithdrawLog.setUpdateTime(new Date());
            this.refusedUpdateProcess(memberWithdrawLog, userName, ip);
        }else {
            return AjaxResult.error("会员账号"+memberWithdrawLog.getAccount()+"该笔订单状态"+memberWithdrawLog.getStatus()+"该状态下订单不能拒绝");
        }


        redisUtil.unLock(EnumLock.member, memberWithdrawLog.getMemberId());
        return AjaxResult.success();
    }

    @Transactional(rollbackFor = Exception.class)
    void refusedUpdateProcess(MemberWithdrawLog memberWithdrawLog, String userName, String ip) {
        memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        BigDecimal old = memberInfoMapper.selectTotalAccountById(memberWithdrawLog.getMemberId());
        //回退提现金额
        memberInfoMapper.updateMoneySelect(memberWithdrawLog.getMemberId(), memberWithdrawLog.getWithdrawMoney(), null, null
                , null, null);
        BigDecimal now = memberInfoMapper.selectTotalAccountById(memberWithdrawLog.getMemberId());
        logService.logmarkMoney(memberWithdrawLog.getMemberId(), memberWithdrawLog.getAccount(), EnumMoney.bohui, now, old,
                "驳回人：" + userName + "-" + ip, memberWithdrawLog.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult refuseds(ReqMemberWithdrawLog req) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();

        String ip = UserDataUtil.getIp(ServletUtil.getHttpServletRequest());

        if (!redisUtil.lock(EnumLock.adminUser, userName, "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }
        List<MemberWithdrawLog> withdrawLogList = memberWithdrawLogMapper.selectByIds(req.getIds());
        if (withdrawLogList==null || withdrawLogList.size()==0){
            return AjaxResult.error("该订单已被处理,请刷新界面");
        }
        for (MemberWithdrawLog memberWithdrawLog : withdrawLogList) {
            if (memberWithdrawLog == null) {
                return AjaxResult.error("订单不存在");
            }
            if (!StringUtils.isEmpty(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
                return AjaxResult.error("会员账号"+memberWithdrawLog.getAccount()+"该笔订单只能由" + memberWithdrawLog.getOpName() + "处理");
            }
            if (memberWithdrawLog.getStatus() == 2) {
                return AjaxResult.error("会员账号"+memberWithdrawLog.getAccount()+"该笔订单重复处理");
            }
            if (memberWithdrawLog.getStatus()<2 || memberWithdrawLog.getStatus()==5 || memberWithdrawLog.getStatus()==7 || memberWithdrawLog.getStatus()==8 ){
                memberWithdrawLog.setRemark(req.getRemark());
                memberWithdrawLog.setStatus(2);//审核不通过
                memberWithdrawLog.setOpName(userName);
                memberWithdrawLog.setUpdateTime(new Date());
                this.refusedUpdateProcess(memberWithdrawLog, userName, ip);
            }else {
                return AjaxResult.error("会员账号"+memberWithdrawLog.getAccount()+"该笔订单状态"+memberWithdrawLog.getStatus()+"该状态下订单不能拒绝");
            }
        }

        redisUtil.unLock(EnumLock.adminUser, userName);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult lock(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() == 1) {
            return AjaxResult.error("该订单已被锁定,请刷新界面");
        }
        if (memberWithdrawLog.getStatus() == 2) {
            return AjaxResult.error("该订单已被拒绝");
        }
        if (memberWithdrawLog.getStatus() != 5 && 1 < memberWithdrawLog.getStatus()) {
            return AjaxResult.error("审核流程非法");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();

        if (!StringUtils.isEmpty(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单只能由" + memberWithdrawLog.getOpName() + "处理");
        }

        memberWithdrawLog.setRemark(req.getRemark());
        memberWithdrawLog.setStatus(1);
        memberWithdrawLog.setOpName(userName);
        memberWithdrawLog.setUpdateTime(new Date());
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        if (i > 0) {
            return AjaxResult.success();
        }

        return AjaxResult.error("更新订单状态失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult locks(ReqMemberWithdrawLog req) {
        List<MemberWithdrawLog> memberWithdrawLogList = memberWithdrawLogMapper.selectLocksByIds(req.getIds());
        for (MemberWithdrawLog memberWithdrawLog : memberWithdrawLogList) {
            if (memberWithdrawLog == null) {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "订单不存在");
            }
            if (memberWithdrawLog.getStatus() == 1) {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "订单已被锁定,请刷新界面");
            }
            if (memberWithdrawLog.getStatus() == 2) {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "订单已被拒绝");
            }
            if (memberWithdrawLog.getStatus() != 5 && 1 < memberWithdrawLog.getStatus()) {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "审核流程非法");
            }
            LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
            String userName = loginUser.getUser().getUserName();

            if (!StringUtils.isEmpty(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "订单只能由" + memberWithdrawLog.getOpName() + "处理");
            }

            memberWithdrawLog.setRemark(req.getRemark());
            memberWithdrawLog.setStatus(1);
            memberWithdrawLog.setOpName(userName);
            memberWithdrawLog.setUpdateTime(new Date());
            int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
            if (i > 0) {
                continue;
            } else {
                return AjaxResult.error(memberWithdrawLog.getOrderNo() + "更新订单状态失败");
            }
        }
        return AjaxResult.success("批量锁定成功");
    }

    @Override
    public AjaxResult unlock(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() != 1 && memberWithdrawLog.getStatus() != 5) {
            return AjaxResult.error("该订单已被处理,请刷新界面");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();
        if (!StringUtils.isEmpty(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单只能由" + memberWithdrawLog.getOpName() + "处理");
        }
        if (!redisUtil.lock(EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }

        memberWithdrawLog.setRemark("取消锁定人：" + userName);
        memberWithdrawLog.setStatus(0);
        memberWithdrawLog.setOpName("");
        memberWithdrawLog.setUpdateTime(new Date());
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        if (i > 0) {
            return AjaxResult.success();
        }

        return AjaxResult.error("更新订单状态失败");
    }

    @Override
    public AjaxResult artificial(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() == 2) {
            return AjaxResult.error("该订单已被拒绝");
        }
        if (memberWithdrawLog.getStatus() == 3) {
            return AjaxResult.error("该订单已被终审,请刷新界面");
        }
        if (memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus()) {
            return AjaxResult.error("审核流程非法");
        }
        if (!redisUtil.lock(EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }

        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();
        if (StringUtils.hasText(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单已被" + memberWithdrawLog.getOpName() + "锁定");
        }

        memberWithdrawLog.setRemark(req.getRemark());
        memberWithdrawLog.setStatus(3);
        memberWithdrawLog.setOpName(userName);
        memberWithdrawLog.setUpdateTime(new Date());
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        if (i > 0) {
            redisUtil.unLock(EnumLock.member, memberWithdrawLog.getMemberId());
            return AjaxResult.success();
        }

        return AjaxResult.error("更新订单状态失败");
    }

    @Override
    public AjaxResult abnormalWithdrawal(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() == 2) {
            return AjaxResult.error("该订单已被拒绝");
        }
        if (memberWithdrawLog.getStatus() == 3) {
            return AjaxResult.error("该订单已被终审,请刷新界面");
        }
        if (memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus()) {
            return AjaxResult.error("审核流程非法");
        }
        if (!redisUtil.lock(EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }

        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();
        if (StringUtils.hasText(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单已被" + memberWithdrawLog.getOpName() + "锁定");
        }

        memberWithdrawLog.setRemark(req.getRemark());
        memberWithdrawLog.setStatus(7);
        memberWithdrawLog.setOpName(userName);
        memberWithdrawLog.setUpdateTime(new Date());
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        if (i > 0) {
            redisUtil.unLock(EnumLock.member, memberWithdrawLog.getMemberId());
            return AjaxResult.success();
        }

        return AjaxResult.error("更新订单状态失败");
    }

    @Override
    public AjaxResult manualWithdrawal(ReqMemberWithdrawLog req) {
        MemberWithdrawLog memberWithdrawLog = this.selectMemberWithdrawLogById(req.getId());
        if (memberWithdrawLog == null) {
            return AjaxResult.error("订单不存在");
        }
        if (memberWithdrawLog.getStatus() == 2) {
            return AjaxResult.error("该订单已被拒绝");
        }
        if (memberWithdrawLog.getStatus() == 3) {
            return AjaxResult.error("该订单已被终审,请刷新界面");
        }
        if (memberWithdrawLog.getStatus() != 5 && 3 < memberWithdrawLog.getStatus()) {
            return AjaxResult.error("审核流程非法");
        }
        if (!redisUtil.lock(EnumLock.member, memberWithdrawLog.getMemberId(), "1", 5)) {
            return AjaxResult.error("请勿重复提交");
        }

        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String userName = loginUser.getUser().getUserName();
        if (StringUtils.hasText(memberWithdrawLog.getOpName()) && !userName.equals(memberWithdrawLog.getOpName())) {
            return AjaxResult.error("该订单已被" + memberWithdrawLog.getOpName() + "锁定");
        }

        // memberWithdrawLog.setRemark( req.getRemark() );
        memberWithdrawLog.setStatus(8);
        memberWithdrawLog.setOpName(userName);
        memberWithdrawLog.setUpdateTime(new Date());
        int i = memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
        if (i > 0) {
            redisUtil.unLock(EnumLock.member, memberWithdrawLog.getMemberId());
            return AjaxResult.success();
        }

        return AjaxResult.error("更新订单状态失败");
    }

    /**
     * 取报告
     *
     * @param id id
     * @return {@link AjaxResult}
     */
    @Override
    public RspMemberInfo withdrawReport(String id) {
//        if (!redisUtil.lock(EnumLock.member, id, "1", 10)) {
//            return AjaxResult.error("请勿连续点击");
//        }
//        memberInfoMapper.call_pro_useranalysis(id);
//        List<WithdrawReport> withdrawReports = memberInfoMapper.userWithdrawReportList();
//        redisUtil.unLock( EnumLock.member, id );

        //取会员id最后一个字符
        String tableLast = id.substring(id.length() - 1);

        RspMemberInfo rspMemberInfo1 = memberInfoMapper.selectMemberInfoWithdrawByIda(id,tableLast);
        RspMemberInfo rspMemberInfo2 = memberInfoMapper.selectMemberInfoWithdrawByIdb(id,tableLast);
        RspMemberInfo rspMemberInfo3 = memberInfoMapper.selectMemberInfoWithdrawByIdc(id,tableLast);
        RspMemberInfo rspMemberInfo4 = memberInfoMapper.selectMemberInfoWithdrawByIdd(id,tableLast);
        RspMemberInfo rspMemberInfo5 = memberInfoMapper.selectMemberInfoWithdrawByIde(id,tableLast);
        RspMemberInfo rspMemberInfo6 = memberInfoMapper.selectMemberInfoWithdrawByIdf(id,tableLast);
        RspMemberInfo rspMemberInfo7 = memberInfoMapper.selectMemberInfoWithdrawByIdg(id,tableLast);
        RspMemberInfo rspMemberInfo8 = memberInfoMapper.selectMemberInfoWithdrawByIdh(id,tableLast);
        RspMemberInfo rspMemberInfo9 = memberInfoMapper.selectMemberInfoWithdrawByIdi(id,tableLast);
        RspMemberInfo rspMemberInfo10 = memberInfoMapper.selectMemberInfoWithdrawByIdj(id,tableLast);
        RspMemberInfo rspMemberInfo11 = memberInfoMapper.selectMemberInfoWithdrawByIdk(id,tableLast);
        rspMemberInfo1.setRechargemoney(rspMemberInfo2.getRechargemoney());
        rspMemberInfo1.setSubmoney(rspMemberInfo3.getSubmoney());
        rspMemberInfo1.setP_money(rspMemberInfo4.getP_money());
        rspMemberInfo1.setRg_income(rspMemberInfo5.getRg_income());
        rspMemberInfo1.setZs_income(rspMemberInfo6.getZs_income());
        rspMemberInfo1.setTotalincom(rspMemberInfo7.getTotalincom());
        rspMemberInfo1.setW_count(rspMemberInfo8.getW_count());
        rspMemberInfo1.setW_sum(rspMemberInfo9.getW_sum());
        rspMemberInfo1.setGcount(rspMemberInfo10.getGcount());
        rspMemberInfo1.setGtcount(rspMemberInfo11.getGtcount());

        return rspMemberInfo1;
    }

    @Override
    public AjaxResult getTotal(MemberWithdrawLog memberWithdrawLog) {
        return AjaxResult.success(memberWithdrawLogMapper.getTotal(memberWithdrawLog));
    }

    @Override
    public List<MemberWithdrawLog> getWithdrawLogList() {
        String date = getTime();
        String beginTime = date.split(" ")[0] + " 00:00:00";
        return memberWithdrawLogMapper.getWithdrawLogList(date, beginTime);
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, -10);
        return sdf.format(nowTime.getTime());
    }
}
