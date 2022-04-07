package com.qiqilm.server.admin.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.rsp.RspTestAccountProp;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoPropMapper;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.service.ILiveVideoPropService;
import org.springframework.util.CollectionUtils;

/**
 * 送礼物Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveVideoPropServiceImpl implements ILiveVideoPropService {
    @Autowired
    private LiveVideoPropMapper liveVideoPropMapper;
    @Autowired
    private LiveUserMapper liveUserMapper;
    @Value("${spring.profiles.active}")
    private String profile;


    /**
     * 查询送礼物列表
     *
     * @param liveVideoProp 送礼物
     * @return 送礼物
     */
    @Override
    public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp) {
        LiveVideoProp liveVideoProp1 = setTime(liveVideoProp);
        List<LiveVideoProp> liveVideoProps = liveVideoPropMapper.selectLiveVideoPropList(liveVideoProp1);
        Set<Long> liveUserId = liveVideoProps.stream().map(LiveVideoProp::getToUserId).filter(toUserId -> toUserId != -1).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(liveUserId)) {
            List<LiveUser> liveUsers = liveUserMapper.selectLiveUserInId(liveUserId);
            for (LiveVideoProp videoProp : liveVideoProps) {
                for (LiveUser liveUser : liveUsers) {
                    if (videoProp.getToUserId() == liveUser.getId()) {
                        videoProp.setNickName(liveUser.getNickName());
                    }
                }
            }
        }
        return liveVideoProps;
    }

    @Override
    public LiveVideoProp getCount(LiveVideoProp liveVideoProp) {
        LiveVideoProp liveVideoProp1 = setTime(liveVideoProp);
        return liveVideoPropMapper.getCount(liveVideoProp1);
    }

    @Override
    public List<RspTestAccountProp> testAccountPorpList(LiveVideoProp liveVideoProp) {
        return liveVideoPropMapper.testAccountPorpList(liveVideoProp);
    }

    @Override
    public RspTestAccountProp testAccountCount(LiveVideoProp liveVideoProp) {
        return liveVideoPropMapper.testAccountPorpCount(liveVideoProp);
    }


    private LiveVideoProp setTime(LiveVideoProp liveVideoProp) {
        if (liveVideoProp.getSelectDate() != null && liveVideoProp.getSelectDate().length > 0) {
            liveVideoProp.setStartTime(liveVideoProp.getSelectDate()[0] + " 00:00:00");
            liveVideoProp.setEndTime(liveVideoProp.getSelectDate()[1] + " 23:59:59");
        }
        return liveVideoProp;
    }

}
