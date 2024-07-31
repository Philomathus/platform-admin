package com.qiqilm.server.admin.core.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiqilm.server.admin.constant.HttpStatus;
import com.qiqilm.server.admin.core.page.PageDomain;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.page.TableSupport;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LotteryHistory;
import com.qiqilm.server.admin.domain.LotteryInfo;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.exception.ControllerExceptionHandler;
import com.qiqilm.server.admin.service.IMemberGameDataMinService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * web层通用数据处理
 *
 * @author 77tv
 */
@Log4j2
public class BaseController extends ControllerExceptionHandler {

	/**
	 * 将前台传递过来的日期格式的字符串，自动转化为Date类型
	 */
	@InitBinder
	public void initBinder( WebDataBinder binder ) {
		// Date 类型转换
		binder.registerCustomEditor( Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText( String text ) {
				if(text.indexOf( " " ) > 0){
					setValue( DateFormatUtils.parse( text ) );
				}else{
					setValue( DateFormatUtils.parse( text,DateFormatUtils.SPLIT_PATTERN_DATE ) );
				}

			}
		} );
	}

	/**
	 * 设置请求分页数据
	 */
	protected void startPage() {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer    pageNum    = pageDomain.getPageNum();
		Integer    pageSize   = pageDomain.getPageSize();
		if ( StringUtils.isNotNull( pageNum ) && StringUtils.isNotNull( pageSize ) ) {
			String orderBy = SqlUtil.escapeOrderBySql( pageDomain.getOrderBy() );
			PageHelper.startPage( pageNum, pageSize, orderBy );
		}
	}

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    protected TableDataInfo getDataTable( List<?> list ) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode( HttpStatus.SUCCESS );
        rspData.setMsg( "查询成功" );
        rspData.setRows( list );
        rspData.setTotal( new PageInfo( list ).getTotal() );
        return rspData;
    }

	/**
	 * 响应请求分页数据
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected TableDataInfo getDataTable2( List<?> list ) {
		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode( HttpStatus.SUCCESS );
		rspData.setMsg( "查询成功" );
        String pageNum = ServletUtil.getParameter("pageNum");
        String pageSize = ServletUtil.getParameter("pageSize");
		rspData.setRows( PageUtil.pageBySubList(list, Integer.parseInt(pageSize), Integer.parseInt(pageNum)) );
		rspData.setTotal( new PageInfo( list ).getTotal() );
		return rspData;
	}

	/**
	 * 响应返回结果
	 *
	 * @param rows 影响行数
	 * @return 操作结果
	 */
	protected AjaxResult toAjax( int rows ) {
		return rows > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	/**
	 * 页面跳转
	 */
	public String redirect( String url ) {
		return StringUtils.format( "redirect:{}", url );
	}

	public Map defaultOrderBetState(){
		Map<String, String> cacheOrderBetState = new TreeMap<>();
		cacheOrderBetState.put("ALL","全部");
		cacheOrderBetState.put("X","未结算");
		cacheOrderBetState.put("N","已取消");
		cacheOrderBetState.put("W","赢");
		cacheOrderBetState.put("L","输");
		cacheOrderBetState.put("LW","赢一半");
		cacheOrderBetState.put("LL","输一半");
		cacheOrderBetState.put("O","平手");
		cacheOrderBetState.put("S","等待中");
		cacheOrderBetState.put("D","未接受");
		cacheOrderBetState.put("C","注销");
		cacheOrderBetState.put("F","非法下注");
		cacheOrderBetState.put("SC","系统注销");
		cacheOrderBetState.put("DC","危险球注销");
		return cacheOrderBetState;
	}

	public TreeMap defaultSbBetState(){
		TreeMap<String, String> betState = new TreeMap<>();
		betState.put("ALL","全部");
		betState.put("waiting","等待中");
		betState.put("running","进行中");
		betState.put("void","作废");
		betState.put("refund","退款");
		betState.put("reject","已取消");
		betState.put("lose","输");
		betState.put("won","赢");
		betState.put("draw","和局");
		betState.put("half won","半赢");
		betState.put("half lose","半输");
		return betState;

	}

	public TreeMap defaultTransferType(){
		TreeMap<String, String> transferType = new TreeMap<>();
		transferType.put("ALL","全部");
		transferType.put("Deposit","额度转出");
		transferType.put("Withdraw","额度转入");
		return transferType;
	}

	public TreeMap defaultTransferState(){
		TreeMap<String, String> transferState = new TreeMap<>();
		transferState.put("ALL","全部");
		transferState.put("Succeeded","成功");
		transferState.put("Inprogress","待处理");
		transferState.put("Unconfirmed","未确认");
		transferState.put("Failed","失败");
		return transferState;

	}

	public List<Map> handlyGameData(IMemberGameDataMinService memberGameDataMinService, MemberGameData req) throws Exception {
		return memberGameDataMinService.selectMemberGameDataMinList(req);
	}


	public TreeMap<String, LotteryHistory> handlyLotteryHistory(LotteryInfo lotteryInfo, LotteryHistory lotteryHistory) {
		String startIssue = lotteryHistory.getStartIssue();
		String endIssue = lotteryHistory.getEndIssue();
		String s = startIssue.split("-")[0];
		String e = endIssue.split("-")[0];
		Date preStart = DateUtils.dateTime(DateUtils.YYYY_MM_DD,s.substring(0,4) + "-" + s.substring(4,6) + "-"+s.substring(6,8));
		Long sufStart = Long.valueOf(startIssue.split("-")[1]);
		Date preEnd = DateUtils.dateTime(DateUtils.YYYY_MM_DD,e.substring(0,4) + "-" + e.substring(4,6) + "-"+e.substring(6,8));
		Long sufEnd = Long.valueOf(endIssue.split("-")[1]);
		Long day = (preEnd.getTime() - preStart.getTime()) / (1000 * 60 * 60 * 24);
		//校验前缀
		if (day <= 0 && sufStart > sufEnd) {
			throw new BusinessException("彩种名称[" + lotteryHistory.getName() + "],开始期数:{" + startIssue + "}不能大于结束期数:{" + endIssue + "},请排查!");
		}
		TreeMap<String,LotteryHistory> historyMap = new TreeMap<>();
		//当天
		if (day == 0) {
			for (Long i = sufStart; i <= sufEnd; i++) {
				LotteryHistory history = initLottery(preStart,i,lotteryInfo,lotteryHistory);
				historyMap.put(history.getIssue(),history);
			}
			return historyMap;
		}
		Long min = 1L;//默认第一期
		Long max = 24 * 60 / lotteryInfo.getCycle();
		for (int i = 0; i <= day; i++) {
			if (i == 0){
				for (Long j = sufStart; j <= max; j++) {
					LotteryHistory history = initLottery(preStart,j,lotteryInfo,lotteryHistory);
					historyMap.put(history.getIssue(),history);
				}
			}
			if (i >0 && i < day){
				preStart = DateUtils.addDays(preStart,1);
				for (Long j = min; j <= max; j++) {
					LotteryHistory history = initLottery(preStart,j,lotteryInfo,lotteryHistory);
					historyMap.put(history.getIssue(),history);
				}
			}
			if (i == day){
				preStart = DateUtils.addDays(preStart,1);
				for (Long k = min; k <= sufEnd; k++) {
					LotteryHistory history = initLottery(preStart,k,lotteryInfo,lotteryHistory);
					historyMap.put(history.getIssue(),history);
				}
			}
		}
		return historyMap;
	}

	public LotteryHistory initLottery(Date preStart, Long i,LotteryInfo lotteryInfo, LotteryHistory lotteryHistory){
		LotteryHistory lottery = (LotteryHistory) lotteryHistory.clone();
		String dateTime = DateFormatUtils.formate(preStart,DateFormatUtils.TIGHT_PATTERN_DATE);
		if (i < 10){
			lottery.setIssue(dateTime + "-" + "000"+i);
		}else if (i < 100){
			lottery.setIssue(dateTime + "-" + "00"+i);
		}else if (i < 1000){
			lottery.setIssue(dateTime + "-" + "0"+i);
		}else {
			lottery.setIssue(dateTime + "-" +i);
		}
		String issue = lottery.getIssue();
		String ymd = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8);
		Long h = Long.valueOf(issue.split("-")[1]) * lotteryInfo.getCycle() / 60;
		Long m = Long.valueOf(issue.split("-")[1]) * lotteryInfo.getCycle() - (h * 60);
		String hms = "";
		if (h >= 10 && m >= 10){
			hms = h + ":" +m + ":00";
		}
		if (h >= 10 && m < 10){
			hms = h + ":0"+m + ":00";
		}
		if (h < 10 && m >= 10){
			hms = "0"+h + ":" +m + ":00";
		}
		if (h < 10 && m < 10){
			hms = "0"+h + ":0" +m + ":00";
		}
		lottery.setId(issue+"-" + lotteryInfo.getId());
		lottery.setKtime(DateFormatUtils.parse(ymd+" " +hms,DateFormatUtils.SPLIT_PATTERN_DATETIME));
		lottery.setStatus(1L);
		lottery.setName(lotteryInfo.getName());
		return lottery;
	}

}
