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
import com.qiqilm.server.admin.domain.CommonProblem;
import com.qiqilm.server.admin.service.ICommonProblemService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

/**
 * 常见问题Controller
 *
 * @author 77tv
 * @date 2021-02-07
 */
@RestController
@RequestMapping( "/activity/commonProblem" )
public class CommonProblemController extends BaseController {
	@Autowired
	private ICommonProblemService commonProblemService;

	/**
	 * 查询常见问题列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(CommonProblem commonProblem) {
		startPage();
		List<CommonProblem> list = commonProblemService.selectCommonProblemList(commonProblem);
		return getDataTable( list );
	}
    
	/**
	 * 导出常见问题列表
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:export')" )
	@Log( title = "常见问题", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(CommonProblem commonProblem) {
		List<CommonProblem>      list = commonProblemService.selectCommonProblemList(commonProblem);
		ExcelUtil<CommonProblem> util = new ExcelUtil<>(CommonProblem.class);
		return util.exportExcel( list, "commonProblem" );
	}

	/**
	 * 获取常见问题详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( commonProblemService.selectCommonProblemById(id) );
	}

	/**
	 * 新增常见问题
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:add')" )
	@Log( title = "常见问题", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody CommonProblem commonProblem) {
		return toAjax( commonProblemService.insertCommonProblem(commonProblem) );
	}

	/**
	 * 修改常见问题
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:edit')" )
	@Log( title = "常见问题", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody CommonProblem commonProblem) {
		return toAjax( commonProblemService.updateCommonProblem(commonProblem) );
	}

	/**
	 * 删除常见问题
	 */
	@PreAuthorize( "@ss.hasPermi('activity:commonProblem:remove')" )
	@Log( title = "常见问题", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( commonProblemService.deleteCommonProblemByIds( ids ) );
	}
}
