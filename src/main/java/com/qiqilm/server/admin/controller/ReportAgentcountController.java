package com.qiqilm.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import com.qiqilm.server.admin.utils.Constants;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 代理统计，主要用于代理渠道的统计Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/reportAgentcount" )
public class ReportAgentcountController extends BaseController {
	@Autowired
	private IReportAgentcountService reportAgentcountService;

    /**
     * 首页注册人数
     *
     * @return
     */
    @ApiOperation( value = "查询统计注册人数", notes = "查询统计注册人数" )
    @PostMapping( value = "/record" )
    public RspBase record(HttpServletRequest request, Map map) {
        String time= DateFormatUtils.formate(new Date(),"yyyy-MM-dd");
        if(map.containsKey("time")){
            time=map.get("time").toString();
            DateFormatUtils.parse(time,"yyyy-MM-dd");
        }
        RspBase rspBase = new RspBase();

        //查询出款人数classTwo=106
        List<ReportPlamHome> classTwo106= reportAgentcountService.findChartsOne("106",time);

        List<ReportPlamHome> classTwo105= reportAgentcountService.findChartsOne("105",time);

        List<ReportPlamHome> classTwo104= reportAgentcountService.findChartsOne("104",time);

        List<ReportPlamHome> classTwo103= reportAgentcountService.findChartsOne("103",time);

        List<ReportPlamHome> classTwox212= reportAgentcountService.findChartsOne("212",time);

        List<ReportPlamHome> classTwox102= reportAgentcountService.findChartsOne("102",time);

        List<ReportPlamHome> classTwox211= reportAgentcountService.findChartsOne("211",time);

        List<ReportPlamHome> classTwox101= reportAgentcountService.findChartsOne("101",time);

        List<ReportPlamHome> classTwox210= reportAgentcountService.findChartsOne("210",time);

        List<ReportPlamHome> classTwox110= reportAgentcountService.findChartsOne("110",time);

        List<ReportPlamHome> classTwox209= reportAgentcountService.findChartsOne("209",time);

        List<ReportPlamHome> classTwox208= reportAgentcountService.findChartsOne("208",time);

        List<ReportPlamHome> classTwox109= reportAgentcountService.findChartsOne("109",time);

        List<ReportPlamHome> classTwox108= reportAgentcountService.findChartsOne("108",time);

        List<ReportPlamHome> classTwox107= reportAgentcountService.findChartsOne("107",time);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("x106",classTwo106);
        jsonObject.put("x105",classTwo105);
        jsonObject.put("x104",classTwo104);
        jsonObject.put("x103",classTwo103);
        jsonObject.put("x212",classTwox212);
        jsonObject.put("x102",classTwox102);
        jsonObject.put("x211",classTwox211);
        jsonObject.put("x101",classTwox101);
        jsonObject.put("x210",classTwox210);
        jsonObject.put("x110",classTwox110);
        jsonObject.put("x209",classTwox209);
        jsonObject.put("x208",classTwox208);
        jsonObject.put("x109",classTwox109);
        jsonObject.put("x108",classTwox108);
        jsonObject.put("x107",classTwox107);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setMsg("成功");
        rspBase.setData(jsonObject);
        return rspBase;
    }
	/**
	 * 查询代理统计，主要用于代理渠道的统计列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ReportAgentcount reportAgentcount) throws ParseException {
		startPage();
		List<ReportAgentcount> list = reportAgentcountService.selectReportAgentcountList(reportAgentcount);
		return getDataTable( list );
	}
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:list')" )
	@GetMapping( "/storage" )
	public AjaxResult storage(ReportAgentcount reportAgentcount) throws ParseException {
		return AjaxResult.success( reportAgentcountService.storage(reportAgentcount));
	}
	@PreAuthorize( "@ss.hasPermi('admin:reportAgentcount:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ReportAgentcount reportAgentcount) throws ParseException {
		List<ReportAgentcount> list = reportAgentcountService.selectReportAgentcountList(reportAgentcount);
		ExcelUtil<ReportAgentcount> util = new ExcelUtil<>(ReportAgentcount.class);
		return util.exportExcel( list, "reportAgentcount" );
	}

}
