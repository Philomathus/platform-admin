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
import com.qiqilm.server.admin.domain.LotteryTemp;
import com.qiqilm.server.admin.service.ILotteryTempService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 彩票即时信息Controller
 *
 * @author 77tv
 * @date 2021-02-23
 */
@RestController
@RequestMapping( "/admin/lotteryTemp" )
public class LotteryTempController extends BaseController {
	@Autowired
	private ILotteryTempService lotteryTempService;

	/**
	 * 查询彩票即时信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryTemp:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LotteryTemp lotteryTemp) {
		startPage();
		List<LotteryTemp> list = lotteryTempService.selectLotteryTempList(lotteryTemp);
		return getDataTable( list );
	}
}
