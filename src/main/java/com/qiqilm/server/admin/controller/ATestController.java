package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.domain.LiveHostWageDay;
import com.qiqilm.server.admin.domain.vo.HostPropDayVo;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.mapper.LiveVideoPropMapper;
import com.qiqilm.server.admin.service.ILiveHostWageDayService;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.task.WheelLotteryTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/test")
public class ATestController {
    @Resource
    private WheelLotteryTask wheelLotteryTask;

    @GetMapping
    public String task(){
        wheelLotteryTask.cashBackTask();
        return "执行成功";
    }
}
