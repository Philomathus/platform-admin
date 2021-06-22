package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveVideoClassified;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveVideoClassifiedService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分类Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/liveVideoClassified" )
public class LiveVideoClassifiedController extends BaseController {
	@Autowired
	private ILiveVideoClassifiedService liveVideoClassifiedService;

	/**
	 * 查询分类列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveVideoClassified liveVideoClassified ) {
		startPage();
		List<LiveVideoClassified> list = liveVideoClassifiedService.selectLiveVideoClassifiedList( liveVideoClassified );
		return getDataTable( list );
	}

	/**
	 * 导出分类列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:export')" )
	@Log( title = "导出分类列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveVideoClassified liveVideoClassified, HttpServletResponse response ) {
		List<LiveVideoClassified> list = liveVideoClassifiedService.selectLiveVideoClassifiedList( liveVideoClassified );
		ExportExcelUtil.exportExcel( list, "导出分类列表", "分类列表", LiveVideoClassified.class, response );
	}

	/**
	 * 获取分类详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveVideoClassifiedService.selectLiveVideoClassifiedById( id ) );
	}

	/**
	 * 新增分类
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:add')" )
	@Log( title = "新增分类", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveVideoClassified liveVideoClassified ) {
		return toAjax( liveVideoClassifiedService.insertLiveVideoClassified( liveVideoClassified ) );
	}

	/**
	 * 修改分类
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:edit')" )
	@Log( title = "修改分类", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveVideoClassified liveVideoClassified ) {
		return toAjax( liveVideoClassifiedService.updateLiveVideoClassified( liveVideoClassified ) );
	}

	/**
	 * 删除分类
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveVideoClassified:remove')" )
	@Log( title = "删除分类", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( liveVideoClassifiedService.deleteLiveVideoClassifiedByIds( ids ) );
	}
}
