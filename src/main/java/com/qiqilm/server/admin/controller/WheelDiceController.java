package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.service.ILiveMountService;
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
import com.qiqilm.server.admin.domain.WheelDice;
import com.qiqilm.server.admin.service.IWheelDiceService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 中秋博饼Controller
 *
 * @author 77tv
 * @date 2021-09-02
 */
@RestController
@RequestMapping( "/admin/wheelDice" )
public class WheelDiceController extends BaseController {
	@Autowired
	private IWheelDiceService wheelDiceService;
	@Autowired
	private ILiveMountService liveMountService;

	/**
	 * 查询中秋博饼列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(WheelDice wheelDice) {
		startPage();
		List<WheelDice> list = wheelDiceService.selectWheelDiceList(wheelDice);
		return getDataTable( list );
	}
    
	/**
	 * 导出中秋博饼列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:export')" )
	@Log( title = "中秋博饼", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(WheelDice wheelDice, HttpServletResponse response) {
		List<WheelDice>      list = wheelDiceService.selectWheelDiceList(wheelDice);
		ExportExcelUtil.exportExcel( list, "中秋博饼", "中秋博饼表", WheelDice.class, response );
	}

	/**
	 * 获取中秋博饼详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( wheelDiceService.selectWheelDiceById(id) );
	}

	/**
	 * 新增中秋博饼
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:add')" )
	@Log( title = "中秋博饼", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody WheelDice wheelDice) {
		if (wheelDice.getType().equals(Long.valueOf("2"))) {
			LiveMount liveMount = liveMountService.selectLiveMountById(Long.valueOf(wheelDice.getPrize()));
			if (liveMount==null){
				return AjaxResult.error("请填写正确的坐骑id");
			}
		}
		return toAjax( wheelDiceService.insertWheelDice(wheelDice) );
	}

	/**
	 * 修改中秋博饼
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:edit')" )
	@Log( title = "中秋博饼", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody WheelDice wheelDice) {
		if (wheelDice.getType().equals(Long.valueOf("2"))) {
			LiveMount liveMount = liveMountService.selectLiveMountById(Long.valueOf(wheelDice.getPrize()));
			if (liveMount==null){
				return AjaxResult.error("请填写正确的坐骑id");
			}
		}
		return toAjax( wheelDiceService.updateWheelDice(wheelDice) );
	}

	/**
	 * 删除中秋博饼
	 */
	@PreAuthorize( "@ss.hasPermi('admin:wheelDice:remove')" )
	@Log( title = "中秋博饼", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( wheelDiceService.deleteWheelDiceByIds( ids ) );
	}
}
