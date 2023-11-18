package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveGuardUser;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.rsp.RspMemberChannel;
import com.qiqilm.server.admin.domain.rsp.RspMemberInfo;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会员信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface MemberInfoMapper {
    /**
     * 查询会员信息
     *
     * @param id 会员信息ID
     * @return 会员信息
     */
    public MemberInfo selectMemberInfoById(String id);
    public MemberInfo selectMemberInfoHistoryById(String id);

    /**
     * 查询会员信息列表
     *
     * @param memberInfo 会员信息
     * @return 会员信息集合
     */
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

    /**
     * 新增会员信息
     *
     * @param memberInfo 会员信息
     * @return 结果
     */
    public int insertMemberInfo(MemberInfo memberInfo);

    /**
     * 修改会员信息
     *
     * @param memberInfo 会员信息
     * @return 结果
     */
    public int updateMemberInfo(MemberInfo memberInfo);
    public int updateMemberInfoHistory(MemberInfo memberInfo);

    int selectMaxMemberCode();

    int updateMoneySelect(@Param("userId") String userId,
                          @Param("money") BigDecimal money,
                          @Param("invite_money") BigDecimal invite_money,
                          @Param("level_integral") BigDecimal level_integral,
                          @Param("code_account") BigDecimal code_account,
                          @Param("code_total") BigDecimal code_total);

    int updateBeatCode(@Param("userId") String userId,
                          @Param("code_account") BigDecimal code_account,
                          @Param("code_total") BigDecimal code_total);

    void call_pro_useranalysis(@Param("userid") String userId);

    List<WithdrawReport> userWithdrawReportList();

    List<String> selectMemberSpeak(String[] ids);

    void updateSpeak(@Param("pUserId") String pUserId, @Param("speak") int speak);

    BigDecimal selectTotalAccountById(String memberId);

    MemberInfo findRecommendByInviterCode(String inviterCode);

    BigDecimal getMemberMoney(String userId);

    //批量手机号更新密码
    int updatePhones(ReqSmallFeatures reqSmallFeatures);
    //批量会员ID查询手机号
    List<ReqSmallFeatures> queryPhones(ReqSmallFeatures reqSmallFeatures);

    int countByUserName(String userName);

    int countByPhone(String phone);
    int countHistoryByPhone(String phone);

    String findBanRemark(@Param("memberid") String memberid);

    void updateInviterCode(@Param("memberId") String memberId,@Param("inviterCode") String inviterCode);

    RspMemberInfo selectMemberInfoWithdrawByIda(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdb(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdc(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdd(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIde(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdf(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdg(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdh(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdi(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdj(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdk(@Param("userid") String id, @Param("tableLast") String tableLast);
    List<RspMemberInfo> selectMemberInfoWithdrawByIdl(@Param("userid") String id, @Param("tableLast") String tableLast);
    RspMemberInfo selectMemberInfoWithdrawByIdz(@Param("userid") String id, @Param("tableLast") String tableLast);

    void changeEmail(MemberInfo memberInfo);

    String selectMemberInfoAddressById(@Param("userid") String id);

    String selectMemberInfoHistoryRechargeById(@Param("userid") String id);

    List<RspMemberChannel> memberstatistics(MemberInfo memberInfo);

    public Map listCount(MemberInfo req );

    List<MemberInfo> selectRegisterByMemberIds(String memberIds);

    Integer clear();
    Integer insertPaiSong(@Param("userIds") String userIds);

    List<MemberInfo> selectNikeNameById( @Param( "array" ) Collection<String> memberIds );


    int banStatus(@Param( "array" ) List<String> memberIdsr,@Param("realName") String realName);

    List<MemberInfo> selectMemberInfoByIp(String id);

    List<MemberInfo> selectStatusByIds(@Param( "array" ) Set<String> memberIds);

    List<MemberInfo> selectAllDBNikeName(@Param( "array" ) Set<String> puserIds,@Param( "arrayDb" ) Set<String> liveSubAgentSet);


    BigDecimal personalRecharge(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId);

    BigDecimal personalOnlineRecharge(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId);

    BigDecimal personalAgentRecharge(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId);

    BigDecimal personalUsdtRecharge(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId);

    BigDecimal personalWithdrawRecharge(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                        @Param("memberId") String memberId);

    BigDecimal personalLiverVideoProp(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId);

    HashMap totalAccount(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("memberId") String memberId);

    List<Map> personalGameData(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("memberId") String memberId,@Param( "dbNodes" ) String dbNodes );

    int boxDish( @Param( "memberId" ) String memberId );

    int unBlockStatus(@Param( "array" )  List<String> loginIp,@Param("realName") String realName );

    List<LiveGuardUser> selectLiveGuard(LiveGuardUser liveGuardUser);

    int withdrawStatus( MemberInfo memberInfo );

    int updateCodeTotalVipLevel( MemberInfo memberInfo );

    String selectUserNameById( @Param( "memberId" ) String memberId );
}
