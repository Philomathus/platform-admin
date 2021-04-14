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
import com.qiqilm.server.admin.domain.LiveHostPre;
import com.qiqilm.server.admin.service.ILiveHostPreService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 主播开播时间预约Controller
 *
 * @author 77tv
 * @date 2021-04-13
 */
@RestController
@RequestMapping( "/admin/liveHostPre" )
public class LiveHostPreController extends BaseController {
	@Autowired
	private ILiveHostPreService liveHostPreService;

	/**
	 * 查询主播开播时间预约列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(LiveHostPre liveHostPre) {
		startPage();
		List<LiveHostPre> list = liveHostPreService.selectLiveHostPreList(liveHostPre);
		return getDataTable( list );
	}
    
	/**
	 * 导出主播开播时间预约列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:export')" )
	@Log( title = "主播开播时间预约", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(LiveHostPre liveHostPre, HttpServletResponse response) {
		List<LiveHostPre>      list = liveHostPreService.selectLiveHostPreList(liveHostPre);
		ExportExcelUtil.exportExcel( list, "主播开播时间预约", "主播开播时间预约表", LiveHostPre.class, response );
	}

	/**
	 * 获取主播开播时间预约详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( liveHostPreService.selectLiveHostPreById(id) );
	}

	/**
	 * 新增主播开播时间预约
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:add')" )
	@Log( title = "主播开播时间预约", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveHostPre liveHostPre) {
		return toAjax( liveHostPreService.insertLiveHostPre(liveHostPre) );
	}

	/**
	 * 修改主播开播时间预约
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:edit')" )
	@Log( title = "主播开播时间预约", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveHostPre liveHostPre) {
		return toAjax( liveHostPreService.updateLiveHostPre(liveHostPre) );
	}

	/**
	 * 删除主播开播时间预约
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveHostPre:remove')" )
	@Log( title = "主播开播时间预约", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( liveHostPreService.deleteLiveHostPreByIds( ids ) );
	}

	public static void main(String[] args) {
		for(int i = 0;i<24;i++) {
			System.out.println("<el-table-column label=\""+i+"点\" align=\"center\" prop=\"live"+i+"\">\n" +
					"        <template slot-scope=\"scope\">\n" +
					"          <span :style=\"{color: (live"+i+" = liveOptions[parseInt(scope.row.live"+i+")]).color}\">\n" +
					"            {{ live"+i+".dictLabel }}\n" +
					"          </span>\n" +
					"        </template>\n" +
					"      </el-table-column>");
		}
	}
}
