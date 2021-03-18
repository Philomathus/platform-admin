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
import com.qiqilm.server.admin.domain.ConfigVip;
import com.qiqilm.server.admin.service.IConfigVipService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-03-17
 */
@RestController
@RequestMapping( "/admin/configVip" )
public class ConfigVipController extends BaseController {
    @Autowired
    private IConfigVipService configVipService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list(ConfigVip configVip) {
        startPage();
        List<ConfigVip> list = configVipService.selectConfigVipList(configVip);
        return getDataTable( list );
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:export')" )
    @Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public void export(ConfigVip configVip, HttpServletResponse response) {
        List<ConfigVip>      list = configVipService.selectConfigVipList(configVip);
        ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", ConfigVip.class, response );
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:edit')" )
    @GetMapping( value = "/{id}" )
    public AjaxResult getInfo( @PathVariable( "id" ) String id) {
        return AjaxResult.success( configVipService.selectConfigVipById(id) );
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:add')" )
    @Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
    @PostMapping
    public Object add( @RequestBody ConfigVip configVip) {
        AjaxResult ajaxResult=configVipService.insertConfigVip(configVip);
        return ajaxResult;
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:edit')" )
    @Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
    @PutMapping
    public Object edit( @RequestBody ConfigVip configVip) {
        AjaxResult ajaxResult=configVipService.updateConfigVip(configVip);
        return ajaxResult;
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize( "@ss.hasPermi('admin:configVip:remove')" )
    @Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
    @DeleteMapping( "/{ids}" )
    public AjaxResult remove( @PathVariable String[] ids ) {
        return toAjax( configVipService.deleteConfigVipByIds( ids ) );
    }
}