package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveOfficer;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveOfficerService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * live officer Controller
 *
 * created date 2022-08-12
 */

@RestController
@RequestMapping( "/admin/liveOfficer" )
public class LiverOfficerController extends BaseController {

    @Autowired
    private ILiveOfficerService liveOfficerService;


    /**
     * 查询房管管理列表 Query live officer management list
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list(LiveOfficer liveOfficer) {
        startPage();
        List<LiveOfficer> list = liveOfficerService.selectLiveOfficerList(liveOfficer);
        return getDataTable( list );
    }

    /**
     * 导出房管管理列表 Export live officer management list
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:export')" )
    @Log( title = "房管管理", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public void export(LiveOfficer liveOfficer, HttpServletResponse response) {
        List<LiveOfficer>      list = liveOfficerService.selectLiveOfficerList(liveOfficer);
        ExportExcelUtil.exportExcel( list, "房管管理", "房管管理表", LiveOfficer.class, response );
    }

    /**
     * 获取房管管理详细信息 Get live officer management details
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:query')" )
    @GetMapping( value = "/{id}" )
    public AjaxResult getInfo( @PathVariable( "id" ) String id) {
        return AjaxResult.success( liveOfficerService.selectLiveOfficerById(id) );
    }

    /**
     * 新增房管管理 Add live officer management
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:add')" )
    @Log( title = "房管管理", businessType = BusinessType.INSERT )
    @PostMapping
    public AjaxResult add( @RequestBody LiveOfficer liveOfficer) {
        return toAjax( liveOfficerService.insertLiveOfficer(liveOfficer) );
    }

    /**
     * 修改房管管理 Modify live officer management
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:edit')" )
    @Log( title = "房管管理", businessType = BusinessType.UPDATE )
    @PutMapping
    public AjaxResult edit( @RequestBody LiveOfficer liveOfficer) {
        return toAjax( liveOfficerService.updateLiveOfficer(liveOfficer) );
    }

    /**
     * 删除房管管理
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveOfficer:remove')" )
    @Log( title = "房管管理", businessType = BusinessType.DELETE )
    @DeleteMapping( "/{ids}" )
    public AjaxResult remove( @PathVariable String[] ids ) {
        return toAjax( liveOfficerService.deleteLiveOfficerByIds( ids ) );
    }

}
