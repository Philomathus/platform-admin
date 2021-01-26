package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MemberInfoServiceImpl implements IMemberInfoService {
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberInfo selectMemberInfoById(String id) {
        return memberInfoMapper.selectMemberInfoById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberInfo 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo) {
        return memberInfoMapper.selectMemberInfoList(memberInfo);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.insertMemberInfo(memberInfo);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.updateMemberInfo(memberInfo);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoByIds(String[] ids) {
        return memberInfoMapper.deleteMemberInfoByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoById(String id) {
        return memberInfoMapper.deleteMemberInfoById(id);
    }

    @Override
    @Transactional
    public RspBase addMemberMoneyOnly(String ip, String userId, BigDecimal money, BigDecimal beatNum, String Mk, String ordermk, String admin_name) {
        RspBase rspBase = new RspBase();
        MemberInfo oldmemberInfo = this.selectMemberInfoById(userId);
        BigDecimal total = oldmemberInfo.getTotalAccount();

        if(money.compareTo(BigDecimal.ZERO)>0){
            if(money.compareTo(new BigDecimal(1000000))>0){
                rspBase.setMsg("最大金额为1000000");
                rspBase.setCode(2);
                return rspBase;
            }
        }else if(money.compareTo(BigDecimal.ZERO)<0){
            BigDecimal lat = total.add(money);
            if(lat.compareTo(BigDecimal.ZERO)<0){
                rspBase.setMsg("余额"+money+"不足扣除");
                rspBase.setCode(2);
                return rspBase;
            }
            beatNum = new BigDecimal(0);
        }

        if(!"0".equals(markorder)){
            List<RspLog_money> markList = null;
            if(money.compareTo(BigDecimal.ZERO)>0){
                markList = logMoneyMapper.findMark(userId, markorder, money,null);
            }else{
                BigDecimal negate = money.negate();
                markList = logMoneyMapper.findMark(userId, markorder,null,negate);
            }
            if(markList.size()>0){
                rspBase.setMsg("请查看此笔金额是否已经入款过，如否请输入其他订单备注");
                rspBase.setCode(2);
                return rspBase;
            }
        }

        if(money!=null && total!=null){
            BigDecimal now = total.add(money);
            if(beatNum!=null&&beatNum.compareTo(BigDecimal.ZERO)>0){
                MemberBcode codeFlow = new MemberBcode();
                codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
                codeFlow.setIncome( money .multiply(beatNum).setScale(2));
                codeFlow.setCreate_time( new Date() );
                codeFlow.setStatus( 0 );
                codeFlow.setCur( BigDecimal.ZERO );
                codeFlow.setUser_id( userId );
                codeFlow.setDes("人工入款");
                codeFlowMapper.insert( codeFlow );
            }else{
                beatNum = new BigDecimal(0);
            }
            memberInfoMapper.updateMoneySelect(userId,money,null, money.multiply(beatNum).setScale(2),null,null);
            MemberActionLogs log = new MemberActionLogs();
            log.setId(UuidUtil.getRandomUuidWithoutSeparator());
            log.setUser_id(userId);
            log.setUser_name(oldmemberInfo.getUser_name());
            log.setC_time(new Date());
            log.setType(EnumAction.gm.getType());
            log.setDes(EnumAction.gm.getDes());
            log.setParam1("人工入款："+money);
            log.setParam2("剩余资金："+now);
            log.setParam3("操作人："+admin_name);
            log.setParam4("备注："+Mk);
            log.setParam_ip(ip);
            memberActionLosService.insert(log);
            logService.logmarkMoney(userId,oldmemberInfo.getUserName(), EnumMoney.gm,now,total,Mk,markorder);
        }else{
            rspBase.setMsg("该成员redis未初始化金额，或者您输入的金额有误");
            rspBase.setCode(2);
            return rspBase;
        }
        return rspBase;
    }
}
