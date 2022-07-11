package com.qiqilm.server.admin.dao;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface MemberInfoDao {

    public MemberInfo selectMemberInfoById(String id);

    List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

    int countByPhone(String phone);

    int insertMemberInfo(MemberInfo memberInfo);

    int updateMemberInfo(MemberInfo memberInfo);

    List<WithdrawReport> userWithdrawReportList();

    String findBanRemark(@Param("memberid") String memberid);

    int updatePhones(ReqSmallFeatures reqSmallFeatures);

    List<ReqSmallFeatures> queryPhones(ReqSmallFeatures reqSmallFeatures);

    Integer clear();

    Integer insertPaiSong(@Param("userIds") String userIds);

    void updateInviterCode(@Param("memberId") String memberId,@Param("inviterCode") String inviterCode);

    void changeEmail(MemberInfo memberInfo);

    String selectMemberInfoAddressById(@Param("userid") String id);

    String selectMemberInfoHistoryRechargeById(@Param("userid") String id);

    List<RspMemberChannel> memberstatistics(MemberInfo memberInfo);

    Map listCount(MemberInfo req );

    List<MemberInfo> selectMemberInfoByIp(String id);

    int banStatus(@Param( "array" ) List<String> memberIds);


}
