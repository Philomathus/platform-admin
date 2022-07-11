package com.qiqilm.server.admin.dao.impl;

import com.qiqilm.server.admin.dao.MemberInfoDao;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class MemberInfoDaoImpl implements MemberInfoDao {

    @Resource
    private MemberInfoMapper memberInfoMapper;

    @Override
    public MemberInfo selectMemberInfoById(String id) {
        return memberInfoMapper.selectMemberInfoById(id);
    }

    @Override
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo) {
        return memberInfoMapper.selectMemberInfoList(memberInfo);
    }

    @Override
    public int countByPhone(String phone) {
        return memberInfoMapper.countByPhone(phone);
    }

    @Override
    public int insertMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.insertMemberInfo(memberInfo);
    }

    @Override
    public int updateMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.updateMemberInfo(memberInfo);
    }

    @Override
    public List<WithdrawReport> userWithdrawReportList() {
        return memberInfoMapper.userWithdrawReportList();
    }

    @Override
    public String findBanRemark(String memberId) {
        return memberInfoMapper.findBanRemark(memberId);
    }

    @Override
    public int updatePhones(ReqSmallFeatures reqSmallFeatures) {
        return memberInfoMapper.updatePhones(reqSmallFeatures);
    }

    @Override
    public List<ReqSmallFeatures> queryPhones(ReqSmallFeatures reqSmallFeatures) {
        return memberInfoMapper.queryPhones(reqSmallFeatures);
    }

    @Override
    public Integer clear() {
        return memberInfoMapper.clear();
    }

    @Override
    public Integer insertPaiSong(String userIds) {
        return memberInfoMapper.insertPaiSong(userIds);
    }

    @Override
    public void updateInviterCode(String memberId, String inviterCode) {
        memberInfoMapper.updateInviterCode(memberId,inviterCode);
    }

    @Override
    public void changeEmail(MemberInfo memberInfo) {
        memberInfoMapper.changeEmail(memberInfo);
    }

    @Override
    public String selectMemberInfoAddressById(String id) {
        return memberInfoMapper.selectMemberInfoAddressById(id);
    }

    @Override
    public String selectMemberInfoHistoryRechargeById(String id) {
        return memberInfoMapper.selectMemberInfoHistoryRechargeById(id);
    }

    @Override
    public List<RspMemberChannel> memberstatistics(MemberInfo memberInfo) {
        return memberInfoMapper.memberstatistics(memberInfo);
    }

    @Override
    public Map listCount(MemberInfo req) {
        return memberInfoMapper.listCount(req);
    }

    @Override
    public List<MemberInfo> selectMemberInfoByIp(String ip) {
        return memberInfoMapper.selectMemberInfoByIp(ip);
    }

    @Override
    public int banStatus(List<String> memberIds) {
        return memberInfoMapper.banStatus(memberIds);
    }


}
