package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.cache.VideoCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.im.GroupType;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.vo.api.ImInfo;
import com.qiqilm.server.admin.mapper.LiveFamilyMapper;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.mapper.LiveVideoMapper;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private VideoCacheUtil videoCacheUtil;

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
        return liveUserMapper.updateLiveUser(liveUser);
    }

    @Override
    public AjaxResult updateFamilyID(Long familyID, Long userId) {
        LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById(familyID);
        if (liveFamily != null || familyID == 0) {
            if (familyID == 0) {
                int oldFamilyId = liveUserMapper.getFamilyId(userId);
                int i = liveUserMapper.updateFamilyID(familyID, userId);
                int num = liveUserMapper.getNumFamily(oldFamilyId);
                liveFamilyMapper.updateFamilyID(num, oldFamilyId);
            } else {
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
        return AjaxResult.error();
    }

    @Override
    public List<RspLotteryBet> selectAnchorAward(ReqLotteryBat req) {
        return liveUserMapper.selectAnchorAward(req);
    }

    @Override
    public AjaxResult insertLiveUser(LiveUser liveUser) {
//	    查询手机号是否存在
        List<LiveUser> list = liveUserMapper.selectLiveUsersByMobile(liveUser.getMobile());
        if (list.isEmpty()) {
            if (ValidatorUtil.isNumber11(liveUser.getMobile())) {
                liveUser.setCreateTime(new Date());
                liveUser.setUpdateTime(new Date());
                liveUser.setRoboter(1);
                liveUserMapper.insertLiveUser(liveUser);
                return AjaxResult.success("添加成功");
            } else {
                return AjaxResult.error("手机号格式错误");
            }

        } else {
            return AjaxResult.error("手机号已存在");
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
                update.setExpiryAfter(1l);
                liveUserMapper.updateLiveUser(update);
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
            liveVideo.setPlayUrl( flv );
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
            liveVideo.setPaiId(new Long(-1));
            liveVideo.setLiveImage(liveImage);
            liveVideo.setHeadImage(hostInfo.getHeadImage());
            liveVideo.setLotteryId(1002);
            liveVideo.setNewPlayFlv(flv);
            liveVideo.setLotteryName("一分快三");
			liveVideo.setPlayUrl( flv );
            setIms(liveVideo, id, title);
            liveVideo.setNPlayFlv(AesUtil.aesEncrypt(flv, "qwertyui12345678"));
            liveVideoMapper.insertLiveVideo(liveVideo);
        }
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
            String groupId = imApi.createGroup(id.toString(), GroupType.AV_CHART_ROOM, title);
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
                String groupId = imApi.createGroup(id.toString(), GroupType.AV_CHART_ROOM, title);
                log.info("主播调用开播接口 - 开始创建群组 - userId:{};groupId:{}", id, groupId);
                liveVideo.setGroupId(groupId);
            }
        }

        if(liveVideo.getGroupId()!=null){
            videoCacheUtil.putHostGroupId(liveVideo.getUserId(),liveVideo.getGroupId());
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
        if (count==0) {
            liveUser.setMobile(newMobile);
            liveUserMapper.updateLiveUser(liveUser);
            return AjaxResult.success("手机号修改成功");
        }else {
            return AjaxResult.error("手机号已存在");
        }

    }

    @Override
    public List<LiveUser> selectLiveUserBankById(Integer userId) {
        return liveUserMapper.selectLiveUserBankById(userId);
    }

    @Override
    public LiveUser selectLiveUserBankOneById(Integer id) {
        return liveUserMapper.selectLiveUserBankOneById(id);
    }
    @Override
    public int delLiveUserBankById(String bankAccount) {
        return liveUserMapper.delLiveUserBankById(bankAccount);
    }
}
