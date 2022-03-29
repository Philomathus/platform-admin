package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.cache.VideoCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.im.GroupType;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.vo.api.ImInfo;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 主播用户信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Slf4j
@Service
public class LiveUserServiceImpl implements ILiveUserService {
    @Autowired
    private LiveUserMapper liveUserMapper;
    @Autowired
    private LiveFamilyMapper liveFamilyMapper;
    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;
    @Autowired
    private LiveVideoMapper liveVideoMapper;
    @Autowired
    private ImApi imApi;
    @Autowired
    private LiveFamilyJoinMapper liveFamilyJoinMapper;
    @Autowired
    private VideoCacheUtil videoCacheUtil;
    @Resource
    private BankListMapper bankListMapper;
    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * 查询主播用户信息
     *
     * @param id 主播用户信息ID
     * @return 主播用户信息
     */
    @Override
    public LiveUser selectLiveUserById(Long id) {
        LiveUser liveUser = liveUserMapper.selectLiveUserById(id);
        if (liveUser != null) {
            if (StringUtils.isNotBlank(liveUser.getMobile())) {
                liveUser.setMobile(new StringBuilder(liveUser.getMobile()).replace(3, 7, "****").toString());
            }
            String domainValue = configDomainCacheUtil.getValue("domain.oss");
            if (StringUtils.isNotBlank(liveUser.getIdentifyHoldImage())
                    && !liveUser.getIdentifyHoldImage().startsWith("http")) {
                liveUser.setIdentifyHoldImage(domainValue + liveUser.getIdentifyHoldImage());
            }
            if (StringUtils.isNotBlank(liveUser.getIdentifyNagativeImage())
                    && !liveUser.getIdentifyNagativeImage().startsWith("http")) {
                liveUser.setIdentifyNagativeImage(domainValue + liveUser.getIdentifyNagativeImage());
            }
            if (StringUtils.isNotBlank(liveUser.getIdentifyPositiveImage())
                    && !liveUser.getIdentifyPositiveImage().startsWith("http")) {
                liveUser.setIdentifyPositiveImage(domainValue + liveUser.getIdentifyPositiveImage());
            }
        }
        return liveUser;
    }

    /**
     * 查询主播用户信息列表
     *
     * @param liveUser 主播用户信息
     * @return 主播用户信息
     */
    @Override
    public List<LiveUser> selectLiveUserList(LiveUser liveUser) {
        List<LiveUser> liveUsers = liveUserMapper.selectLiveUserList(liveUser);
        for (LiveUser user : liveUsers) {
            if (StringUtils.isNotBlank(user.getMobile())) {
                user.setMobile(new StringBuilder(user.getMobile()).replace(3, 7, "****").toString());
            }
        }
        return liveUsers;
    }

    /**
     * 修改主播用户信息
     *
     * @param liveUser 主播用户信息
     * @return 结果
     */
    @Override
    public int updateLiveUser(LiveUser liveUser) {
        liveUser.setUpdateTime(DateUtils.getNowDate());
        int i = liveUserMapper.updateLiveUser(liveUser);
        if (i > 0) {
            RedisCacheUtil.me.clear(liveUser.getId(), LiveUser.class);
        }
        return i;
    }

    @Override
    public AjaxResult updateFamilyID(Long familyID, Long userId) {
        if (familyID == 0) {
            int oldFamilyId = liveUserMapper.getFamilyId(userId);
            int i = liveUserMapper.updateFamilyID(familyID, userId);
            int num = liveUserMapper.getNumFamily(oldFamilyId);
            liveFamilyMapper.updateFamilyID(num, oldFamilyId);
        } else {
            LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById(familyID);
            if (liveFamily == null) {
                return AjaxResult.error("家族不存在，请检查家族ID");
            }
            LiveUser liveUser = liveUserMapper.selectLiveUserById(userId);
            if (liveUser.getFamilyId() != 0) {
                return AjaxResult.error("该主播已有家族,无法加入家族");
            }
            int oldFamilyId = liveUserMapper.getFamilyId(userId);
            int i = liveUserMapper.updateFamilyID(familyID, userId);
            int num = liveUserMapper.getNumFamily(oldFamilyId);
            liveFamilyMapper.updateFamilyID(num, oldFamilyId);
            int newnum = liveUserMapper.getNumFamily(familyID.intValue());
            liveFamilyMapper.updateFamilyID(newnum, familyID.intValue());
        }
        RedisCacheUtil.me.clear(userId, LiveUser.class);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult updateTicket(BigDecimal ticket, Long userId) {
        liveUserMapper.updateTicket(ticket, userId);
        return AjaxResult.success();
    }

    @Override
    public List<RspLotteryBet> selectAnchorAward(ReqLotteryBat req) {
        return liveUserMapper.selectAnchorAward(req);
    }

    @Override
    public AjaxResult insertLiveUser(LiveUser liveUser) {
        if (ValidatorUtil.isNumber11(liveUser.getMobile())) {
            //	    查询手机号是否存在
            List<LiveUser> list = liveUserMapper.selectLiveUsersByMobile(liveUser.getMobile());
            if (list.isEmpty()) {
                if (profile.equals("7706") || profile.equals("7705") || profile.equals("7710") || profile.equals("7711") || profile.equals("7712")) {
                    Long firstId = liveUserMapper.selectFirstId();
                    if (firstId > 0) {
                        firstId = 0L;
                    } else {
                        firstId += -1;
                    }
                    liveUser.setId(firstId);
                }
                liveUser.setCreateTime(new Date());
                liveUser.setUpdateTime(new Date());
                liveUser.setRoboter(1);
                liveUserMapper.insertLiveUser(liveUser);
                return AjaxResult.success("添加成功");
            } else {
                return AjaxResult.error("手机号已存在");
            }
        } else {
            return AjaxResult.error("手机号格式错误");
        }
    }

    public void imReg(LiveUser hostInfo) {
        boolean regOk = false;
        if (hostInfo.getExpiryAfter() == null || hostInfo.getExpiryAfter() < 0) {
            regOk = imApi.register(ImInfo.of(String.valueOf(hostInfo.getId())));
            if (!regOk) {
                log.error("主播第一次注册IM失败hostId:{}", hostInfo.getId());
                regOk = imApi.register(ImInfo.of(String.valueOf(hostInfo.getId())));
            }
            if (!regOk) {
                log.error("主播第二次注册IM失败hostId:{}", hostInfo.getId());
            }
            if (regOk) {//更新注册IM标识
                LiveUser update = new LiveUser();
                update.setId(hostInfo.getId());
                update.setExpiryAfter(1L);
                liveUserMapper.updateLiveUser(update);

                RedisCacheUtil.me.clear(hostInfo.getId(), LiveUser.class);
            }
        }
    }

    /**
     * 开放的生活
     *
     * @param map 地图
     * @return {@link AjaxResult}
     * @throws Exception 异常
     */
    @Override
    public AjaxResult openLive(Map map) throws Exception {
        Integer id = (Integer) map.get("id");
        String title = (String) map.get("title");
        String flv = (String) map.get("flv");
        String liveImage = "";
        Object liveImage1 = map.get("liveImage");
        if (liveImage1 != null) {
            liveImage = (String) map.get("liveImage");
        }
        LiveVideo liveVideo = liveVideoMapper.selectLiveVideoById(new Long(id));
        log.error("虚拟主播开播map:{}", JsonUtil.object2Json(map));
        LiveUser hostInfo = liveUserMapper.selectLiveUserById(new Long(id));
        imReg(hostInfo);

        if (liveVideo != null) {
            //修改
            liveVideo.setLiveIn(1);
            liveVideo.setBeginTime(new Date());
            liveVideo.setEndTime(null);
            liveVideo.setEndDate(null);
            liveVideo.setTitle(title);
            liveVideo.setNPlayFlv(AesUtil.aesEncrypt(flv, "qwertyui12345678"));
            setIms(liveVideo, id, title);
            liveVideo.setCreateType(true);
            liveVideo.setLiveImage(liveImage);
            liveVideo.setHeadImage(hostInfo.getHeadImage());
            liveVideo.setHostName(hostInfo.getNickName());
            liveVideo.setNewPlayFlv(flv);
            liveVideo.setPlayUrl(flv);
            liveVideoMapper.updateLiveVideo2(liveVideo);
        } else {
            //新增
            liveVideo = new LiveVideo();
            liveVideo.setId(new Long(id));
            liveVideo.setLiveIn(1);
            liveVideo.setUserId(id);
            liveVideo.setBeginTime(new Date());
            liveVideo.setEndTime(null);
            liveVideo.setHostName(hostInfo.getNickName());
            liveVideo.setCateId(2);
            liveVideo.setEndDate(null);
            liveVideo.setCreateType(true);
            liveVideo.setTitle(title);
            liveVideo.setPaiId(-1L);
            liveVideo.setLiveImage(liveImage);
            liveVideo.setHeadImage(hostInfo.getHeadImage());
            liveVideo.setLotteryId(1002);
            liveVideo.setNewPlayFlv(flv);
            liveVideo.setLotteryName("一分快三");
            liveVideo.setPlayUrl(flv);
            setIms(liveVideo, id, title);
            liveVideo.setNPlayFlv(AesUtil.aesEncrypt(flv, "qwertyui12345678"));
            if (profile.equals("7701")) {
                liveVideoMapper.insertLiveVideo7706(liveVideo);
                liveVideoMapper.insertLiveVideo7711(liveVideo);
            }
            if (profile.equals("7704")) {
                liveVideoMapper.insertLiveVideo7705(liveVideo);
            }
            if (profile.equals("7708")) {
                liveVideoMapper.insertLiveVideo7710(liveVideo);
                liveVideoMapper.insertLiveVideo7712(liveVideo);
            }
            liveVideoMapper.insertLiveVideo(liveVideo);
        }
        RedisCacheUtil.me.clear(id, LiveVideo.class);
        return null;
    }

    /**
     * 设置ims
     *
     * @param liveVideo 视频直播
     * @param id        id
     * @param title     标题
     */
    private void setIms(LiveVideo liveVideo, Object id, String title) {
        if (!org.springframework.util.StringUtils.hasText(liveVideo.getGroupId())) {
            //创建 im 聊天群
            String groupId = imApi.createGroup(id.toString(), GroupType.AV_CHART_ROOM,
                    String.valueOf(liveVideo.getUserId()));
            if (groupId == null) {
                throw new BusinessException("创建直播失败,请联系客服");
            }
            log.info("主播调用开播接口 - 开始创建群组 - userId:{};groupId:{}", id, groupId);
            liveVideo.setGroupId(groupId);
        } else {
            //im 连接测试
            try {
                imApi.getGroupUser(liveVideo.getGroupId(), PageVO.ofPage(1, 1));
            } catch (Exception e) {
                log.error("主播调用开播接口 - 测试群组失败 - userId:{};groupId:{}", id, liveVideo.getGroupId(), e);
                //创建 im 聊天群
                String groupId = imApi.createGroup(id.toString(), GroupType.AV_CHART_ROOM,
                        String.valueOf(liveVideo.getUserId()));
                log.info("主播调用开播接口 - 开始创建群组 - userId:{};groupId:{}", id, groupId);
                liveVideo.setGroupId(groupId);
            }
        }

        if (liveVideo.getGroupId() != null) {
            videoCacheUtil.putHostGroupId(liveVideo.getUserId(), liveVideo.getGroupId());
        }
    }


    /**
     * 接近生活
     *
     * @param map 地图
     * @return {@link AjaxResult}
     */
    @Override
    public AjaxResult closeLive(Map map) {
        LiveVideo liveVideo = new LiveVideo();
        liveVideo.setUserId((Integer) map.get("id"));
        List<LiveVideo> liveVideos = liveVideoMapper.selectLiveVideoList2(liveVideo);
        if (!liveVideos.isEmpty()) {
            liveVideo = liveVideos.get(0);
            liveVideo.setEndDate(new Date());
            liveVideo.setEndTime(new Date());
            liveVideo.setLiveIn(0);
            liveVideoMapper.updateLiveVideo(liveVideo);

            RedisCacheUtil.me.clear(liveVideo.getId(), LiveVideo.class);
            return AjaxResult.success("关播成功");
        } else {
            return AjaxResult.error("直播不存在");
        }
    }

    @Override
    public AjaxResult updateMobile(String newMobile, String oldMobile, String id) {
        //校验旧手机号
        LiveUser liveUser = liveUserMapper.selectLiveUserById(Long.parseLong(id));
        //判断手机号是否存在
        Integer count = liveUserMapper.checkMobile(newMobile);
        if (count == 0) {
            liveUser.setMobile(newMobile);
            liveUserMapper.updateLiveUser(liveUser);

            RedisCacheUtil.me.clear(id, LiveUser.class);
            return AjaxResult.success("手机号修改成功");
        } else {
            return AjaxResult.error("手机号已存在");
        }

    }

    @Override
    public List<LiveUser> selectLiveUserBankById(Integer userId) {
        return liveUserMapper.selectLiveUserBankById(userId);
    }

    @Override
    public AjaxResult updateLiveUserBank(LiveUser liveUser) {
        BankList bankList = bankListMapper.selectBankListByName(liveUser.getBankName());
        if (bankList == null) {
            return AjaxResult.error(100, "银行卡名称错误！");
        }
        liveUser.setBankTypeId(bankList.getId());
        liveUserMapper.updateLiveUserBank(liveUser);
        RedisCacheUtil.me.clear(liveUser.getId(), LiveUser.class);
        return AjaxResult.success();
    }

    @Override
    public int delLiveUserBankById(String bankAccount) {
        return liveUserMapper.delLiveUserBankById(bankAccount);
    }

    /**
     * 踢出主播
     *
     * @param id
     * @return success
     * @deprecated 1：要判断主播是否在直播 live_video live_in字段
     * 2：要更新live_user 家族id清空 。。。
     * 3：家族成员要减少
     * 4: 家族成员配置表要删除改主播的信息
     * 5：要判断是否是家族长，家族长不能被踢出家族
     */
    @Override
    @Transactional
    public AjaxResult kickOutLiveById(Long id) {
        LiveVideo liveVideo = liveVideoMapper.selectLiveVideoById(id);
        if (liveVideo != null && liveVideo.getLiveIn() == 1) {
            return AjaxResult.error(100, "该主播在直播中,踢出家族主播失败！");
        }
        LiveUser liveUser = liveUserMapper.selectLiveUserById(id);
        if (liveUser.getFamilyChieftain() != null && liveUser.getFamilyChieftain() == 1) {
            return AjaxResult.error(100, "家族长不能被踢出家族,踢出家族主播失败！");
        }
        LiveFamily family = liveFamilyMapper.selectLiveFamilyById(liveUser.getFamilyId());
        if (family == null) {
            return AjaxResult.error(100, "主播未加入家族,踢出家族主播失败！");
        }
        int count = Integer.parseInt(family.getUserCount() + "");
        int familyId = Integer.parseInt(liveUser.getFamilyId() + "");
        LiveFamilyJoin liveFamilyJoin = new LiveFamilyJoin();
        liveFamilyJoin.setFamilyId(liveUser.getFamilyId());
        liveFamilyJoin.setUserId(id);
        liveFamilyJoin.setStatus(3L);
        //修改申请表状态
        liveFamilyJoinMapper.updateLiveFamilyJoin(liveFamilyJoin);
        //修改家族成员
        count--;
        liveFamilyMapper.updateFamilyID(count, familyId);
        liveUser.setFamilyId(0L);
        liveUser.setFamilyChieftain(0);
        //修改主播状态
        liveUserMapper.updateLiveUser(liveUser);
        return AjaxResult.success();
    }

}
