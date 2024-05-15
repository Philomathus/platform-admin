package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.MessageOnSite;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.IMessageOnSiteService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 站内信息Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/admin/messageOnSite" )
public class MessageOnSiteController extends BaseController {
    @Autowired
    private IMessageOnSiteService messageOnSiteService;
    @Autowired
    private IMemberInfoService    memberInfoService;


    /**
     * 查询站内信息列表
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list( MessageOnSite messageOnSite ) {
        startPage();
        List<MessageOnSite> list = messageOnSiteService.selectMessageOnSiteList( messageOnSite );
        return getDataTable( list );
    }

    /**
     * 导出站内信息列表
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:export')" )
    @Log( title = "站内信息", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public void export( MessageOnSite messageOnSite, HttpServletResponse response ) {
        List<MessageOnSite> list = messageOnSiteService.selectMessageOnSiteList( messageOnSite );
        ExportExcelUtil.exportExcel( list, "站内信息", "站内信息表", MessageOnSite.class, response );
    }

    /**
     * 获取站内信息详细信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:query')" )
    @GetMapping( value = "/{id}" )
    public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( messageOnSiteService.selectMessageOnSiteById( id ) );
    }

    /**
     * 新增站内信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:add')" )
    @Log( title = "站内信息", businessType = BusinessType.INSERT )
    @PostMapping
    public AjaxResult add( @RequestBody MessageOnSite messageOnSite ) {
        messageOnSite.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        messageOnSite.setPubdatetime( new Date() );
        messageOnSite.setReceiverType( "ALL_MEMBER" );
        messageOnSite.setAction( "DIALOG" );
        return toAjax( messageOnSiteService.insertMessageOnSite( messageOnSite ) );
    }

    /**
     * 修改站内信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:edit')" )
    @Log( title = "站内信息", businessType = BusinessType.UPDATE )
    @PutMapping
    public AjaxResult edit( @RequestBody MessageOnSite messageOnSite ) {
        return toAjax( messageOnSiteService.updateMessageOnSite( messageOnSite ) );
    }

    /**
     * 删除站内信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:remove')" )
    @Log( title = "站内信息", businessType = BusinessType.DELETE )
    @DeleteMapping( "/{ids}" )
    public AjaxResult remove( @PathVariable String[] ids ) {
        return toAjax( messageOnSiteService.deleteMessageOnSiteByIds( ids ) );
    }

    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:add')" )
    @Log( title = "会员站内信息", businessType = BusinessType.INSERT )
    @PostMapping( "/addUserMessage" )
    public AjaxResult addUserMessage( @RequestBody MessageOnSite messageOnSite ) {
        MemberInfo memberInfo = memberInfoService.selectMemberInfoById( messageOnSite.getToUserId() );
        if ( Objects.isNull( memberInfo ) ) {
            return AjaxResult.error( "发送失败,会员id错误" );
        }
        messageOnSite.setId( UuidUtil.getRandomUuidWithoutSeparator() );
        messageOnSite.setPubdatetime( new Date() );
        messageOnSite.setReceiverType( "ALL_MEMBER" );
        messageOnSite.setAction( "DIALOG" );
        return toAjax( messageOnSiteService.insertMessageOnSite( messageOnSite ) );
    }

    @PreAuthorize( "@ss.hasPermi('admin:messageOnSite:add')" )
    @Log( title = "会员站内信息", businessType = BusinessType.INSERT )
    @PostMapping( "/addMultipleUserMessage" )
    public AjaxResult addMultipleUserMessage( @RequestBody MessageOnSite messageOnSite ) {
        if ( StringUtils.isBlank( messageOnSite.getToUserId() ) ) {
            return AjaxResult.error( "请输入会员ID" );
        }
        return AjaxResult.success( messageOnSiteService.insertMultipleMessageOnSite( messageOnSite ) );
    }
}
