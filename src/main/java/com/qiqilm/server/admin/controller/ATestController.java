package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.vo.HostPropDayVo;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.mapper.LiveVideoPropMapper;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;
import com.qiqilm.server.admin.service.ILiveVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/test")
public class ATestController {
    @Autowired
    private ILiveVideoService liveVideoService;

    @Resource
    private LiveVideoPropMapper liveVideoPropMapper;
    @Resource
    private LiveHostWageDayMapper liveHostWageDayMapper;

    @GetMapping()
    public void list() {
        liveVideoService.countHostGift();
    }

    @PutMapping
    public void put(String dayTime) {
        if (LiveCenterConfig.me.isLiveCenter() && !Objects.isNull(LiveCenterConfig.me.getLiveSubAgents())) {
            return;
        }

        List<LiveHostWageDay> liveHostWageDayList = liveHostWageDayMapper.selectLikeId(dayTime);
        List<HostPropDayVo> propDayVos = liveVideoPropMapper.sumHostPropDayList(dayTime,
                LiveCenterConfig.me.getProfileDbMain(), LiveCenterConfig.me.getProfileDbLive());

        String begin = dayTime.concat(" 00:00:00");
        String end = dayTime.concat(" 23:59:59");

        List<HostPropDayVo> lotteryDayVos = liveVideoPropMapper.sumHostLotteryDayList(begin, end,
                LiveCenterConfig.me.getProfileDbMain(), LiveCenterConfig.me.getProfileDbLottery());

        for (LiveHostWageDay liveHostWageDay : liveHostWageDayList) {
            for (HostPropDayVo propDayVo : propDayVos) {
                if (propDayVo.getHostId() == liveHostWageDay.getHostId().intValue()) {
                    liveHostWageDay.setTicket(propDayVo.getSumHostProp());
                }
            }
            for (HostPropDayVo lotteryDayVo : lotteryDayVos) {
                if (lotteryDayVo.getHostId() == liveHostWageDay.getHostId().intValue()) {
                    liveHostWageDay.setLotteryCost(lotteryDayVo.getSumHostProp());
                }
            }

        }
        for (LiveHostWageDay liveHostWageDay : liveHostWageDayList) {
            liveHostWageDayMapper.updateLiveHostWageDay(liveHostWageDay, LiveCenterConfig.me.getProfileDbLive());
        }
    }
}
