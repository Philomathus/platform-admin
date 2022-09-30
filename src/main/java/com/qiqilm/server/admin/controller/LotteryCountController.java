package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.LotteryCount;
import com.qiqilm.server.admin.service.LotteryCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/lotteryCount")
public class LotteryCountController extends BaseController {


    @Autowired
    private LotteryCountService lotteryCountService;

    @PreAuthorize( "@ss.hasPermi('admin:lotteryCount:list')")
    @GetMapping("/list")
    public TableDataInfo lotteryCountList(LotteryCount lotteryCount){
        startPage();
        List<LotteryCount> lotteryCountList =  lotteryCountService.selectAllLotteryCount(lotteryCount);
        System.out.println("=============================testing here ============================================");
        for (LotteryCount lotteryC : lotteryCountList){
            System.out.println(lotteryC);
        }
        return getDataTable(lotteryCountList);
    }

}
