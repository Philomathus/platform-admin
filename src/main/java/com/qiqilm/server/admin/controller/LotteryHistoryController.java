package com.qiqilm.server.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.LotteryHistory;
import com.qiqilm.server.admin.service.ILotteryHistoryService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 开奖历史Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryHistory" )
public class LotteryHistoryController extends BaseController {
	@Autowired
	private ILotteryHistoryService lotteryHistoryService;

	/**
	 * 查询开奖历史列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryHistory lotteryHistory) {
		startPage();
		List<LotteryHistory> list = lotteryHistoryService.selectLotteryHistoryList(lotteryHistory);
		return getDataTable( list );
	}
}
