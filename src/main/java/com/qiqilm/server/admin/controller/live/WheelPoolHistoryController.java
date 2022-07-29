package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.WheelPoolHistory;
import com.qiqilm.server.admin.service.WheelPoolHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/wheelPoolHistory")
public class WheelPoolHistoryController extends BaseController {

    @Autowired
    private WheelPoolHistoryService wheelPoolHistoryService;

    @GetMapping("/list")
    @PreAuthorize( "@ss.hasPermi('admin:wheelPoolHistory:list')" )
    public TableDataInfo list(WheelPoolHistory wheelPoolHistory){
        startPage();
        List<WheelPoolHistory> wheelPoolHistoryList =  wheelPoolHistoryService.selectAllWheelPoolHistory(wheelPoolHistory);
        return getDataTable(wheelPoolHistoryList);
    }
}
