package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveBlack;
import com.qiqilm.server.admin.service.ILiveBlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 拉黑Controller
 *
 * @author 77tv
 * @date 2021-08-25
 */
@RestController
@RequestMapping("/admin/liveBlack")
public class LiveBlackController extends BaseController {
    @Autowired
    private ILiveBlackService liveBlackService;

    @PreAuthorize("@ss.hasPermi('admin:liveBlack:list')")
    @GetMapping("/list")
    public TableDataInfo liveBlackList(LiveBlack liveBlack) {
        startPage();
        List<LiveBlack> list = liveBlackService.selectLiveBlackList(liveBlack);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:liveBlack:remove')")
    @PutMapping("/deleteLiveBlack")
    public AjaxResult deleteLiveBlack(@RequestBody LiveBlack liveBlack) {
        return liveBlackService.deleteLiveBlackById(liveBlack);
    }
}