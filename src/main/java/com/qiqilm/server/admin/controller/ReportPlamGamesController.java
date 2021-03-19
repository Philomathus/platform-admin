package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportPlamGames;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamGamesService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 游戏投注报表Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/report-plam-games" )
public class ReportPlamGamesController extends BaseController {
	@Autowired
	private IReportPlamGamesService reportPlamGamesService;

	//获取昨天数据
	private static String getYestoday() {
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DATE, -1 );
		Date time = cal.getTime();
		return new SimpleDateFormat( "yyyy-MM-dd" ).format( time );
	}

	/**
	 * 查询游戏投注报表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-games:list')" )
	@GetMapping( "/list" )
	public Object list( ReportPlamGames reportPlamGames ) throws ParseException {
		return reportPlamGamesService.selectReportPlamGamesList( reportPlamGames );

	}

	@GetMapping( value = "/count" )
	public AjaxResult countBetData( ReportPlamGames reportPlamGames ) {
		String myString = reportPlamGames.getBegindate();
		if ( StringUtils.isEmpty( myString ) ) {
			reportPlamGames.setBegindate( getYestoday() );
		}
		ReportPlamGames reportPlamGames1 = reportPlamGamesService.countBetData( reportPlamGames );
		return AjaxResult.success( reportPlamGames1 );
	}

	@PreAuthorize( "@ss.hasPermi('admin:reportPlamGames:export')" )
	@Log( title = "游戏投注报表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ReportPlamGames reportPlamGames, HttpServletResponse response ) {
		List<ReportPlamGames> list = reportPlamGamesService.exportPlamGamesList( reportPlamGames );
		ExportExcelUtil.exportExcel( list, "游戏投注报表", "游戏投注报表", ReportPlamGames.class, response );
	}
}
