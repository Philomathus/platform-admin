package com.qiqilm.server.admin.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

	/**
	 * 查询全部彩种
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:list')" )
	@GetMapping( "/lotteryName" )
	public AjaxResult lotteryName() {
		List<LotteryHistory> list = lotteryHistoryService.selectLotteryHistoryList();
		return AjaxResult.success( list );
	}

	/**
	 * 重新派奖
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:list')" )
	@PostMapping( "/{id}" )
	public AjaxResult changeStatus(@PathVariable String id) {
		String ktime = lotteryHistoryService.selectKtimeById(id);
		LocalDateTime now       = LocalDateTime.now();
		now = now.minusMinutes(10);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String localTime = df.format(now);
		Integer i=ktime.compareTo(localTime);
		if(i<0) {
			return AjaxResult.error(0, "超过开奖时间10分钟不可再重新派奖");
		}
		lotteryHistoryService.changeStatus(id);
		return AjaxResult.success();
	}


	public static void main(String[] args) {
		String ktime = "2021-03-30 15:22:00";
		LocalDateTime now       = LocalDateTime.now();
		now = now.minusMinutes(10);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String localTime = df.format(now);
		Integer i=ktime.compareTo(localTime);
		System.out.println(i);
	}

}
