package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryHistory;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.service.ILotteryHistoryService;
import com.qiqilm.server.admin.service.ILotteryInfoService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	@Autowired
	private ILotteryInfoService lotteryInfoService;

	/**
	 * 查询开奖历史列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LotteryHistory lotteryHistory ) {
		startPage();
		List<LotteryHistory> list = lotteryHistoryService.selectLotteryHistoryList( lotteryHistory );
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
	public AjaxResult changeStatus( @PathVariable String id ) {
		String        ktime = lotteryHistoryService.selectKtimeById( id );
		LocalDateTime now   = LocalDateTime.now();
		now = now.minusMinutes( 2 );
		DateTimeFormatter df        = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );
		String            localTime = df.format( now );
		Integer           i         = ktime.compareTo( localTime );
		if ( i > 0 ) {
			return AjaxResult.error( 0, "超开奖时间2分钟后,方可人工派奖" );
		}
		lotteryHistoryService.changeStatus( id );
		return AjaxResult.success();
	}

	/**
	 * 补期数
	 */
	@PreAuthorize( "@ss.hasPermi('admin:lotteryHistory:addIssue')" )
	@PostMapping("/addIssue")
	public AjaxResult addIssue(@RequestBody LotteryHistory lotteryHistory) {
		try {
			if (lotteryHistory == null || lotteryHistory.getName() == null){
				return AjaxResult.error("彩种名称不能为空!");
			}
			if (StringUtils.isEmpty(lotteryHistory.getStartIssue()) || StringUtils.isEmpty(lotteryHistory.getEndIssue())){
				return AjaxResult.error("开始期数或者结束期数不能为空!");
			}
			if (lotteryHistory.getStartIssue().compareTo(lotteryHistory.getEndIssue()) > 0){
				return AjaxResult.error("开始期数不能大于结束期数!");
			}
			String lotName = lotteryHistory.getName();
			LotteryInfo info = new LotteryInfo();
			info.setName(lotName);
			List<LotteryInfo> list = lotteryInfoService.selectLotteryInfoList(info);
			if (list == null || list.get(0) == null){
				return AjaxResult.error("彩种名称:["+lotName+"]不存在!");
			}
			if (list.get(0).getStatus() == 0L){
				return AjaxResult.error("彩种名称["+lotName+"]已禁用!");
			}
			lotteryHistory.setIssue(lotteryHistory.getStartIssue());
			List<LotteryHistory> histories = lotteryHistoryService.selectLotteryHistoryList(lotteryHistory);
			if (histories == null || histories.size() == 0){
				return AjaxResult.error("彩种["+lotName+"]开始期数["+lotteryHistory.getStartIssue()+"]不存在!");
			}
			lotteryHistory.setIssue(lotteryHistory.getEndIssue());
			histories = lotteryHistoryService.selectLotteryHistoryList(lotteryHistory);
			if (histories == null || histories.size() == 0){
				return AjaxResult.error("彩种["+lotName+"]结束期数["+lotteryHistory.getStartIssue()+"]不存在!");
			}
			info = list.get(0);
			lotteryHistory.setLotteryId(info.getId());
			TreeMap<String,LotteryHistory> treeMap = handlyLotteryHistory(info,lotteryHistory);
			if (StringUtils.isNotEmpty(treeMap)){
				Date startTime = treeMap.get(treeMap.firstKey()).getKtime();
				Date endTime = treeMap.get(treeMap.lastKey()).getKtime();
				List<LotteryHistory> storeLists = lotteryHistoryService.selectBetweenByTime(startTime,endTime,info.getId());
				if (storeLists == null || storeLists.size() == 0){
					return AjaxResult.error("彩种名称["+lotName+"]开始期数或结束期数不存在,补开奖失败!");
				}
				for (LotteryHistory storeHistory : storeLists){
					treeMap.remove(storeHistory.getIssue());
				}
				if (StringUtils.isNotEmpty(treeMap)){
					histories = treeMap.values().stream().collect(Collectors.toList());
					for (LotteryHistory insertHistory: histories){
						insertHistory.setStatus(0L);
					}
					lotteryHistoryService.batchLotteryHistory(histories);
				}
			}
			return AjaxResult.success();
		}catch (Exception ex){
			return AjaxResult.error("补单开奖参数:["+ JsonUtil.object2Json(lotteryHistory) +"]出现异常{"+ex+"}!");
		}
	}

}
