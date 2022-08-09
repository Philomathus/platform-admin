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
import java.util.Map;


/**
 * 【wheel_pool_history】Controller
 *
 * @author Rajesh
 * @date 2022-07-29
 */

@RestController
@RequestMapping("/admin/wheelPoolHistory")
public class WheelPoolHistoryController extends BaseController {

    @Autowired
    private WheelPoolHistoryService wheelPoolHistoryService;


    /** 获取所有轮池历史数据 get all wheel pool history data */
    @GetMapping("/list")
    @PreAuthorize( "@ss.hasPermi('admin:wheelPoolHistory:list')" )
    public TableDataInfo wheelPoolHistoryList(WheelPoolHistory wheelPoolHistory){
        startPage();
        List<WheelPoolHistory> wheelPoolHistoryList =  wheelPoolHistoryService.selectAllWheelPoolHistory(wheelPoolHistory);
        return getDataTable(wheelPoolHistoryList);
    }

    /**
     * 获取所有轮盘池历史彩票缓存
     * get all wheel pool history lottery caches
     */
    @GetMapping("/lotteryCacheList")
    public TableDataInfo getLotteryList(){
        return getDataTable(wheelPoolHistoryService.wheelPoolLotteryCacheList());
    }


    /**
     * 获取所有轮池历史计数
     * get all wheel pool history count
     */
    @PreAuthorize( "@ss.hasPermi('admin:wheelPoolHistory:list')" )
    @GetMapping("/listCount")
    public Map listCount(WheelPoolHistory wheelPoolHistory) {
        return wheelPoolHistoryService.listCount(wheelPoolHistory);
    }
}
