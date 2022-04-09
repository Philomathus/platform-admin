package com.qiqilm.server.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.MemberForbidUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.rsp.RspMemberInfo;
import com.qiqilm.server.admin.domain.vo.PageBO;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberInfoHistoryService;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.utils.NameUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.UuidUtil;
import com.qiqilm.server.admin.utils.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 会员信息 Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Slf4j
@Service
public class MemberInfoHistoryServiceImpl implements IMemberInfoHistoryService {
    @Autowired
    private MemberInfoHistoryMapper memberInfoHistoryMapper;
    @Autowired
    private MemberActionLogsMapper actionLogsMapper;
    @Autowired
    private LogMoneyMapper logMoneyMapper;
    @Autowired
    private MemberBcodeMapper codeFlowMapper;
    @Resource
    private MemberGameMoneyMapper gameMoneyMapper;
    @Resource
    private LogGameOrderMapper logGameOrderMapper;
    @Autowired
    private ILogService logService;
    @Autowired
    private MemberCardMapper memberCardMapper;
    @Autowired
    private MemberCacheManager memberCacheManager;
    @Autowired
    private MemberForbidUtil memberForbidUtil;
    @Autowired
    private MemberBcodeMapper memberBcodeMapper;
    @Autowired
    private NameUtil nameUtil;
    @Autowired
    private TokenService tokenService;

    /**
     * 查询会员信息
     *
     * @param id 会员信息 ID
     * @return 会员信息
     */
    @Override
    public MemberInfo selectMemberInfoById(String id) {
        return memberInfoHistoryMapper.selectMemberInfoById(id);
    }

    /**
     * 查询会员信息 列表
     *
     * @param memberInfo 会员信息
     * @return 会员信息
     */
    @Override
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo) {
        if (!StringUtils.isEmpty(memberInfo.getSearchValue())){
            memberInfo.setParams(null);
        }
        List<MemberInfo> memberInfos = memberInfoHistoryMapper.selectMemberInfoList(memberInfo);
        if (memberInfos.size() > 0 && !CollectionUtils.isEmpty(memberInfos)) {
            for (MemberInfo me : memberInfos) {
                if (!StringUtils.isEmpty(me.getPhone())) {
                    me.setPhone(me.getPhone().substring(0, 3) + "****" + me.getPhone().substring(7, 11));
                }
            }
        }
        return memberInfos;
    }

    /**
     * 新增会员信息
     *
     * @param memberInfo 会员信息
     * @return 结果
     */
    @Override
    public AjaxResult insertMemberInfo(MemberInfo memberInfo) {
        //校验是不是手机号
        if (!ValidatorUtil.isNumber11(memberInfo.getPhone())) {
            return AjaxResult.error("手机号必须是11位数字");
        }
        if (memberInfoHistoryMapper.countByPhone(memberInfo.getPhone()) > 0) {
            return AjaxResult.error("此手机号已经存在");
        }
        MemberInfo member = memberCacheManager.createMember();
        if (StringUtils.isEmpty(member.getId())) {
            return AjaxResult.error("注册redis存在问题，请联系管理员");
        }

        member.setIsOnline(0);
        member.setVip(1);//默认vip1
        member.setStatus(2);
        member.setTotalAccount(BigDecimal.ZERO);
        member.setPassword(memberInfo.getPassword());
        member.setUserName(member.getMemberCode());
        member.setPhone(memberInfo.getPhone());
        member.setRegTime(new Date());
        member.setLevelIntegral(BigDecimal.ZERO);
        member.setBoxAccount(BigDecimal.ZERO);
        member.setCodeAccount(BigDecimal.ZERO);
        member.setCodeTotal(BigDecimal.ZERO);
        member.setInviteMoney(memberInfo.getInviteMoney());
        member.setInviterCode(memberInfo.getInviterCode());
        member.setNickName(nameUtil.nickNameRandom());
        member.setLoginNum(0);
        if (memberInfoHistoryMapper.insertMemberInfo(member) > 0) {
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.success("添加失败");
        }
    }

    /**
     * 修改会员信息
     *
     * @param memberInfo 会员信息
     * @return 结果
     */
    @Override
    public int updateMemberInfo(MemberInfo memberInfo) {
        return memberInfoHistoryMapper.updateMemberInfo(memberInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RspBase addMemberMoneyOnly(String ip, String userId, BigDecimal money, BigDecimal beatNum, String Mk,
                                      String markorder, String admin_name) {
        RspBase rspBase = new RspBase();
        MemberInfo oldmemberInfo = this.selectMemberInfoById(userId);
        BigDecimal total = oldmemberInfo.getTotalAccount();

        if (money.compareTo(BigDecimal.ZERO) > 0) {
            if (money.compareTo(new BigDecimal(1000000)) > 0) {
                rspBase.setMsg("最大金额为1000000");
                rspBase.setCode(2);
                return rspBase;
            }
        } else if (money.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal lat = total.add(money);
            if (lat.compareTo(BigDecimal.ZERO) < 0) {
                rspBase.setMsg("余额" + money + "不足扣除");
                rspBase.setCode(2);
                return rspBase;
            }
            beatNum = new BigDecimal(0);
        }

        if (!"0".equals(markorder)) {
            List<LogMoney> markList = null;
            if (money.compareTo(BigDecimal.ZERO) > 0) {
                markList = logMoneyMapper.findMark(userId, markorder, money, null, userId.substring(userId.length() - 1));
            } else {
                BigDecimal negate = money.negate();
                markList = logMoneyMapper.findMark(userId, markorder, null, negate, userId.substring(userId.length() - 1));
            }
            if (markList.size() > 0) {
                rspBase.setMsg("请查看此笔金额是否已经入款过，如否请输入其他订单备注");
                rspBase.setCode(2);
                return rspBase;
            }
        }

        if (total != null) {
            BigDecimal now = total.add(money);
            if (beatNum != null && beatNum.compareTo(BigDecimal.ZERO) > 0) {
                MemberBcode codeFlow = new MemberBcode();
                codeFlow.setId(UuidUtil.getRandomUuidWithoutSeparator());
                codeFlow.setIncome(money.multiply(beatNum).setScale(2));
                codeFlow.setCreateTime(new Date());
                codeFlow.setStatus(0);
                codeFlow.setCur(BigDecimal.ZERO);
                codeFlow.setUserId(userId);
                codeFlow.setDes("人工入款");
                codeFlowMapper.insertMemberBcode(codeFlow);
            } else {
                beatNum = new BigDecimal(0);
            }
            memberInfoHistoryMapper.updateMoneySelect(userId, money, null, money.multiply(beatNum).setScale(2), null, null);
            MemberActionLogs log = new MemberActionLogs();
            log.setId(UuidUtil.getRandomUuidWithoutSeparator());
            log.setUserId(userId);
            log.setUserName(oldmemberInfo.getUserName());
            log.setcTime(new Date());
            log.setType(EnumAction.gm.getType());
            log.setDes(EnumAction.gm.getDes());
            log.setParam1("人工入款：" + money);
            log.setParam2("剩余资金：" + now);
            log.setParam3("操作人：" + admin_name);
            log.setParam4("备注：" + Mk);
            log.setParamIp(ip);
            actionLogsMapper.insertMemberActionLogs(log);
            logService.logmarkMoney(userId, oldmemberInfo.getUserName(), EnumMoney.gm, now, total, Mk, markorder);
        } else {
            rspBase.setMsg("该成员redis未初始化金额，或者您输入的金额有误");
            rspBase.setCode(2);
            return rspBase;
        }
        return rspBase;
    }

    @Override
    public PageBO<WithdrawReport> withdrawReport(String memberid, Integer pageNum, Integer pageSize) {
        memberInfoHistoryMapper.call_pro_useranalysis(memberid);
        PageBO<WithdrawReport> pageBO = new PageBO<>();
        pageNum = 1;
        pageSize = 100;
        Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<WithdrawReport> withdrawReports = memberInfoHistoryMapper.userWithdrawReportList();
        String remark = memberInfoHistoryMapper.findBanRemark(memberid);
        WithdrawReport withdrawReport = new WithdrawReport();
        withdrawReport.setClass_twoname("禁言禁用备注");
        withdrawReport.setT_value(remark);
        withdrawReports.add(withdrawReport);
        pageBO.setData(withdrawReports);
        pageBO.setCount(page.getTotal());
        return pageBO;
    }


    @Override
    public PageBO<MemberCard> findMemberCardPage(String memberid, Integer pageNum, Integer pageSize, String orderBy) {
        PageBO<MemberCard> pageBO = new PageBO<>();
        Page page = PageHelper.startPage(pageNum, pageSize, orderBy);
        pageBO.setData(memberCardMapper.findList(memberid));
        pageBO.setCount(page.getTotal());
        return pageBO;
    }

    @Override
    public void outGameFail(String orderId, String userId, Integer platformId) {
        MemberGameMoney myGameMoney = new MemberGameMoney();
        myGameMoney.setId(userId + "_" + platformId);
        myGameMoney.setStatus(2);
        myGameMoney.setOderSn("");
        gameMoneyMapper.updateMemberGameMoney(myGameMoney);

        LogGameOrder logOrder = new LogGameOrder();
        logOrder.setId(orderId);
        logOrder.setStatus(1);
        logOrder.setETime(new Date());
        logGameOrderMapper.updateLogGameOrder(logOrder);
    }

    @Override
    public void outGMGameSucess(String orderId, String userId, Integer platformId, BigDecimal money, String account) {
        MemberGameMoney myGameMoney = new MemberGameMoney();
        myGameMoney.setId(userId + "_" + platformId);
        myGameMoney.setStatus(0);
        myGameMoney.setOderSn("");
        myGameMoney.setMoney(BigDecimal.ZERO);
        int i = gameMoneyMapper.updateMemberGameMoney(myGameMoney);

        Date date = new Date();
        LogGameOrder logOrder = new LogGameOrder();
        logOrder.setId(orderId);
        logOrder.setBTime(date);
        logOrder.setETime(date);
        logOrder.setMemberId(userId);
        logOrder.setMoney(money);
        logOrder.setStatus(2);
        logOrder.setType(2);
        logOrder.setUserName(account);
        logOrder.setPlatformId(platformId);
        int i1 = logGameOrderMapper.insertLogGameOrder(logOrder);

        if (money.compareTo(BigDecimal.ZERO) > 0 && i > 0 && i1 > 0) {
            memberInfoHistoryMapper.updateMoneySelect(userId, money, null, null, null, null);
        }
    }

    @Override
    public int changeSpeak(MemberInfo memberInfo) {
        if ("0".equals(memberInfo.getSpeak())) {
            memberInfo.setSpeak("0");
            memberInfoHistoryMapper.updateMemberInfo(memberInfo);
            memberForbidUtil.setPlatformUserSpeak(memberInfo.getId(), false);
        } else {
            memberInfo.setSpeak("1");
            memberInfoHistoryMapper.updateMemberInfo(memberInfo);
            memberForbidUtil.setPlatformUserSpeak(memberInfo.getId(), true);
        }
        return 1;
    }

    @Override
    public AjaxResult updatePhones(ReqSmallFeatures req) {
        if (!StringUtils.isEmpty(req.getPhones()) && !StringUtils.isEmpty(req.getPassword())) {
            if(req.getPhones().contains("\n")){
                try {
                    String[] phones = req.getPhones().split("\n");
                    StringBuilder phone = new StringBuilder();
                    for(int i=0;i<phones.length;i++) {
                        phone.append("\"").append(phones[i]).append("\"").append(",");
                    }
                    phone = new StringBuilder(phone.substring(0, phone.length() - 1));
                    req.setPhones(phone.toString());
                } catch (Exception e) {
                    return AjaxResult.error(0, "分割手机号出错,请检查格式");
                }
            }
            memberInfoHistoryMapper.updatePhones(req);
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    @Override
    public AjaxResult unbindCard(MemberCard member) {
        String id = member.getId();
        String memberId = member.getMemberId();
        List<MemberCard> memberCardList = memberCardMapper.memberCardList(memberId);
        MemberCard memberCard = memberCardMapper.selectMemberCardById(id);
        if (Objects.isNull(memberCard)) {
            return AjaxResult.success("卡号不存在");
        }
        if (memberCardList.size() > 1 && memberCard.getDv() == 1) {
            return AjaxResult.success("请先解绑副卡");
        }
        memberCardMapper.deleteMemberCardById(id);
        return AjaxResult.success("解绑成功");
    }

    @Override
    public AjaxResult changeBank(MemberCard member) {
        String id = member.getId();
        //判断用户是否已经绑定该银行卡
        MemberCard memberCard1 = new MemberCard();
        memberCard1.setBankAccount(member.getBankAccount());
        memberCard1.setMemberId(member.getMemberId());
        List<MemberCard> memberCards = memberCardMapper.selectMemberCardList(memberCard1);
        if (!memberCards.isEmpty()) {
            MemberCard memberCard2 = memberCards.get(0);
            //判断绑定的与修改成的是不是同一个,如果不是就不能修改
            if (!memberCard2.getId().equals(member.getId())) {
                log.error("修改的id: {},上传的id: {}", memberCard2.getId(), member.getId());
                return AjaxResult.error("用户已绑定该银行卡");
            }
        }
        MemberCard memberCard = memberCardMapper.selectMemberCardById(id);
        memberCard.setRealName(member.getRealName());
        memberCard.setBankName(member.getBankName());
        memberCard.setBankAddress(member.getBankAddress());
        memberCard.setBankAccount(member.getBankAccount());
        memberCardMapper.updateMemberCard(memberCard);
        return AjaxResult.success("修改银行卡信息成功");
    }

    @Override
    public void repairMemberBcode(String memberId) {
//        int count=memberBcodeMapper.countMemberBcodeStatus(memberId);
//        if (count>0){
//            return;
//        }
        memberBcodeMapper.updateMemberBcodeStatus(memberId);
        memberBcodeMapper.repairMemberInfo(memberId);
    }

    @Override
    public void updateVip(String memberId, Integer vip, String nickName) {
        memberBcodeMapper.updateVip(memberId, vip, nickName);
    }

    @Override
    public AjaxResult updateInviterCode(String inviterCode, String memberId) {
        memberInfoHistoryMapper.updateInviterCode(memberId, inviterCode);
        return AjaxResult.success("修改成功");
    }

    @Override
    public AjaxResult changeEmail(MemberInfo memberInfo) {
        memberInfoHistoryMapper.changeEmail(memberInfo);
        return AjaxResult.success("修改成功");
    }

    @Override
    public String getMemberLoginAddress(String id) {
        return memberInfoHistoryMapper.selectMemberInfoAddressById(id);
    }

    @Override
    public String getHistoryRecharge(String id) {
        return memberInfoHistoryMapper.selectMemberInfoRechargeById(id);
    }

    @Override
    public List<RspMemberChannel> memberstatistics(MemberInfo memberInfo) {
        return memberInfoHistoryMapper.memberstatistics(memberInfo);
    }

    @Override
    public void updataStatus(MemberInfo memberInfo) {
        if (memberInfo.getBanSpeakTime() == 0) {
            memberForbidUtil.setPlatformUserSpeak(memberInfo.getId(), false);
            memberInfo.setSpeak("0");
            memberInfoHistoryMapper.updateMemberInfo(memberInfo);
        }
        if (memberInfo.getBanSpeakTime() > 0) {
            memberForbidUtil.setPlatformUserSpeak(memberInfo.getId(), true);
            memberInfo.setSpeak("1");
            memberInfoHistoryMapper.updateMemberInfo(memberInfo);
        }
    }

    /**
     * 取报告
     *
     * @param id id
     * @return {@link AjaxResult}
     */
    @Override
    public AjaxResult withdrawReport( String id ) {

        //        memberInfoMapper.call_pro_useranalysis(id);
        //        List<WithdrawReport> withdrawReports = memberInfoMapper.userWithdrawReportList();

        //取会员id最后一个字符
        String tableLast = id.substring( id.length() - 1 );

        RspMemberInfo rspMemberInfo1  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIda( id, tableLast );
        RspMemberInfo rspMemberInfo2  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdb( id, tableLast );
        RspMemberInfo rspMemberInfo3  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdc( id, tableLast );
        RspMemberInfo rspMemberInfo4  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdd( id, tableLast );
        RspMemberInfo rspMemberInfo5  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIde( id, tableLast );
        RspMemberInfo rspMemberInfo6  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdf( id, tableLast );
        RspMemberInfo rspMemberInfo7  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdg( id, tableLast );
        RspMemberInfo rspMemberInfo8  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdh( id, tableLast );
        RspMemberInfo rspMemberInfo9  = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdi( id, tableLast );
        RspMemberInfo rspMemberInfo10 = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdj( id, tableLast );
        RspMemberInfo rspMemberInfo11 = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdk( id, tableLast );
        //游戏投注详细
        List<RspMemberInfo> rspMemberInfo12 = memberInfoHistoryMapper.selectMemberInfoWithdrawByIdl( id, tableLast );

        List<WithdrawReport> withdrawReports = new LinkedList<>();
        WithdrawReport       withdrawReporta = new WithdrawReport();
        withdrawReporta.setClass_twoname( "禁言原因" );
        withdrawReporta.setT_value( rspMemberInfo1.getEmail() );
        withdrawReports.add( withdrawReporta );

        WithdrawReport withdrawEmail = new WithdrawReport();
        withdrawEmail.setClass_twoname( "会员备注" );
        withdrawEmail.setT_value( rspMemberInfo1.getEmail() );
        withdrawReports.add( withdrawEmail );

        WithdrawReport withdrawReportb = new WithdrawReport();
        withdrawReportb.setClass_twoname( "会员编号" );
        withdrawReportb.setT_value( rspMemberInfo1.getId() );
        withdrawReports.add( withdrawReportb );

//		WithdrawReport withdrawReportc = new WithdrawReport();
//		withdrawReportc.setClass_twoname( "会员名称" );
//		String phone = rspMemberInfo1.getPhone();
//		if ( !StringUtils.isEmpty( phone ) ) {
//			withdrawReportc.setT_value( PhoneUtil.getEncPhone( phone ) );
//			withdrawReports.add( withdrawReportc );
//		}

        WithdrawReport withdrawReportc = new WithdrawReport();
        withdrawReportc.setClass_twoname( "用户类型" );
        if(!StringUtils.isEmpty(rspMemberInfo1.getChannelCode())){
            if("0".equals(rspMemberInfo1.getChannelCode())){
                withdrawReportc.setT_value( "游客" );
            } else {
                withdrawReportc.setT_value( "会员" );
            }
        }
        withdrawReports.add( withdrawReportc );

        WithdrawReport withdrawReportd = new WithdrawReport();
        withdrawReportd.setClass_twoname( "会员VIP" );
        withdrawReportd.setT_value( rspMemberInfo1.getVip() );
        withdrawReports.add( withdrawReportd );

        WithdrawReport withdrawReportv = new WithdrawReport();
        withdrawReportv.setClass_twoname( "登录时间" );
        withdrawReportv.setT_value( rspMemberInfo1.getLogin_time() );
        withdrawReports.add( withdrawReportv );

        WithdrawReport withdrawReporte = new WithdrawReport();
        withdrawReporte.setClass_twoname( "会员注册时间" );
        withdrawReporte.setT_value( rspMemberInfo1.getReg_time() );
        withdrawReports.add( withdrawReporte );

        WithdrawReport withdrawReportf = new WithdrawReport();
        withdrawReportf.setClass_twoname( "会员积分" );
        withdrawReportf.setT_value( rspMemberInfo1.getTotal_account() );
        withdrawReports.add( withdrawReportf );

        WithdrawReport withdrawReportg = new WithdrawReport();
        withdrawReportg.setClass_twoname( "会员注单" );
        withdrawReportg.setT_value( rspMemberInfo1.getCode_total() );
        withdrawReports.add( withdrawReportg );

        WithdrawReport withdrawReporth = new WithdrawReport();
        withdrawReporth.setClass_twoname( "会员打码" );
        withdrawReporth.setT_value( rspMemberInfo1.getCode_account() );
        withdrawReports.add( withdrawReporth );

        WithdrawReport withdrawReporti = new WithdrawReport();
        withdrawReporti.setClass_twoname( "登陆IP" );
        withdrawReporti.setT_value( rspMemberInfo1.getLogin_ip() );
        withdrawReports.add( withdrawReporti );

        //        WithdrawReport withdrawReportj = new WithdrawReport();
        //        withdrawReportj.setClass_twoname("登陆地址");
        //        withdrawReportj.setT_value(rspMemberInfo1.getIpaddress());
        //        withdrawReports.add(withdrawReportj);

        WithdrawReport withdrawReportk = new WithdrawReport();
        withdrawReportk.setClass_twoname( "线下充值金额" );
        withdrawReportk.setT_value( rspMemberInfo2.getRechargemoney() );
        withdrawReports.add( withdrawReportk );

        WithdrawReport withdrawReportl = new WithdrawReport();
        withdrawReportl.setClass_twoname( "线上金额" );
        withdrawReportl.setT_value( rspMemberInfo3.getSubmoney() );
        withdrawReports.add( withdrawReportl );

        WithdrawReport withdrawReportm = new WithdrawReport();
        withdrawReportm.setClass_twoname( "人工代充金额" );
        withdrawReportm.setT_value( rspMemberInfo4.getP_money() );
        withdrawReports.add( withdrawReportm );

        WithdrawReport withdrawReportn = new WithdrawReport();
        withdrawReportn.setClass_twoname( "手动增加金额" );
        withdrawReportn.setT_value( rspMemberInfo5.getRg_income() );
        withdrawReports.add( withdrawReportn );

        WithdrawReport withdrawReporto = new WithdrawReport();
        withdrawReporto.setClass_twoname( "平台赠送金额" );
        withdrawReporto.setT_value( rspMemberInfo6.getZs_income() );
        withdrawReports.add( withdrawReporto );

        WithdrawReport withdrawReportp = new WithdrawReport();
        withdrawReportp.setClass_twoname( "充值总的金额" );
        withdrawReportp.setT_value( rspMemberInfo7.getTotalincom() );
        withdrawReports.add( withdrawReportp );

        WithdrawReport withdrawReportq = new WithdrawReport();
        withdrawReportq.setClass_twoname( "会员提现次数" );
        withdrawReportq.setT_value( rspMemberInfo8.getW_count() );
        withdrawReports.add( withdrawReportq );

        WithdrawReport withdrawReportr = new WithdrawReport();
        withdrawReportr.setClass_twoname( "会员提现金额" );
        withdrawReportr.setT_value( rspMemberInfo9.getW_sum() );
        withdrawReports.add( withdrawReportr );

        WithdrawReport withdrawReportu = new WithdrawReport();
        withdrawReportu.setClass_twoname( "彩票异常投注次数" );
        withdrawReportu.setT_value( rspMemberInfo10.getGcount() );
        withdrawReports.add( withdrawReportu );

        WithdrawReport withdrawReportt = new WithdrawReport();
        withdrawReportt.setClass_twoname( "彩票总投注笔数" );
        withdrawReportt.setT_value( rspMemberInfo11.getGtcount() );
        withdrawReports.add( withdrawReportt );

        //游戏
        if ( rspMemberInfo12 != null && rspMemberInfo12.size() != 0 ) {
            for ( RspMemberInfo rs : rspMemberInfo12 ) {
                WithdrawReport withdrawReportTwo = new WithdrawReport();
                withdrawReportTwo.setClass_twoname( rs.getClass_twoname() );
                withdrawReportTwo.setT_value( "投注:" + rs.getTouZhu() + "盈利:" + rs.getYingLi() );
                withdrawReports.add( withdrawReportTwo );
            }
        }

        return AjaxResult.success( withdrawReports );
    }
}
