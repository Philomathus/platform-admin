//package com.qiqilm.server.admin.mapper;
//
//import com.qiqilm.server.admin.domain.MemberInfoHistory;
//import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
//import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
//import com.qiqilm.server.admin.domain.rsp.RspMemberInfoHistory;
//import com.qiqilm.server.admin.domain.vo.WithdrawReport;
//import org.apache.ibatis.annotations.Param;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * 会员信息Mapper接口
// *
// * @author 77tv
// * @date 2021-01-25
// */
//public interface MemberInfoHistoryMapper {
//    /**
//     * 查询会员信息
//     *
//     * @param id 会员信息ID
//     * @return 会员信息
//     */
//    public MemberInfoHistory selectMemberInfoHistoryById(String id);
//
//    /**
//     * 查询会员信息列表
//     *
//     * @param MemberInfoHistory 会员信息
//     * @return 会员信息集合
//     */
//    public List<MemberInfoHistory> selectMemberInfoHistoryList(MemberInfoHistory MemberInfoHistory);
//
//    /**
//     * 新增会员信息
//     *
//     * @param MemberInfoHistory 会员信息
//     * @return 结果
//     */
//    public int insertMemberInfoHistory(MemberInfoHistory MemberInfoHistory);
//
//    /**
//     * 修改会员信息
//     *
//     * @param MemberInfoHistory 会员信息
//     * @return 结果
//     */
//    public int updateMemberInfoHistory(MemberInfoHistory MemberInfoHistory);
//
//    int selectMaxMemberCode();
//
//    int updateMoneySelect(@Param("userId") String userId,
//                          @Param("money") BigDecimal money,
//                          @Param("invite_money") BigDecimal invite_money,
//                          @Param("level_integral") BigDecimal level_integral,
//                          @Param("code_account") BigDecimal code_account,
//                          @Param("code_total") BigDecimal code_total);
//
//    int updateBeatCode(@Param("userId") String userId,
//                       @Param("code_account") BigDecimal code_account,
//                       @Param("code_total") BigDecimal code_total);
//
//    void call_pro_useranalysis(@Param("userid") String userId);
//
//    List<WithdrawReport> userWithdrawReportList();
//
//    List<String> selectMemberSpeak(String[] ids);
//
//    void updateSpeak(@Param("pUserId") String pUserId, @Param("speak") int speak);
//
//    BigDecimal selectTotalAccountById(String memberId);
//
//    MemberInfoHistory findRecommendByInviterCode(String inviterCode);
//
//    BigDecimal getMemberMoney(String userId);
//
//    //批量手机号更新密码
//    int updatePhones(ReqSmallFeatures reqSmallFeatures);
//
//    int countByUserName(String userName);
//
//    int countByPhone(String phone);
//
//    String findBanRemark(@Param("memberid") String memberid);
//
//    void updateInviterCode(@Param("memberId") String memberId,@Param("inviterCode") String inviterCode);
//
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIda(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdb(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdc(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdd(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIde(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdf(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdg(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdh(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdi(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdj(@Param("userid") String id, @Param("tableLast") String tableLast);
//    RspMemberInfoHistory selectMemberInfoHistoryWithdrawByIdk(@Param("userid") String id, @Param("tableLast") String tableLast);
//    List<RspMemberInfoHistory> selectMemberInfoHistoryWithdrawByIdl(@Param("userid") String id, @Param("tableLast") String tableLast);
//
//    void changeEmail(MemberInfoHistory MemberInfoHistory);
//
//    String selectMemberInfoHistoryAddressById(@Param("userid") String id);
//
//    String selectMemberInfoHistoryHistoryRechargeById(@Param("userid") String id);
//
//    List<RspMemberChannel> memberstatistics(MemberInfoHistory MemberInfoHistory);
//}
