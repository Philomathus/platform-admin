package com.qiqilm.server.admin.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.domain.ReportPlamCom;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportPlamComService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/report-plam-com" )
@Log4j2
public class ReportPlamComController extends BaseController {
	@Autowired
	private IReportPlamComService reportPlamComService;

	/**
	 * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:report-plam-com:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list(ReportPlamCom reportPlamCom) throws ParseException {
		startPage();
		Date d = new Date();
		log.info( "游戏投注报表统计" );
		String myString = reportPlamCom.getReporttime();
		if (!StringUtils.isEmpty(myString)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dd = simpleDateFormat.parse(myString);
			boolean flag = dd.before(d);
			if(!flag){
				reportPlamCom.setReporttime(null);
			}
		}else{
			reportPlamCom.setReporttime(getYestoday());
		}
		List<ReportPlamCom> list = reportPlamComService.selectReportPlamComList(reportPlamCom);
		return getDataTable( list );
	}
	//获取昨天数据
	private static String getYestoday(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
	}
}