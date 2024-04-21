package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ActivityWithdrawCashBack;
import com.qiqilm.server.admin.domain.ConfigBank;
import com.qiqilm.server.admin.service.IActivityWithdrawCashBackService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping( "/admin/activityWithdrawCashBack" )
public class ActivityWithdrawCashBackController extends BaseController {
    @Resource
    private IActivityWithdrawCashBackService service;

    @GetMapping( "/list" )
    public TableDataInfo list( ActivityWithdrawCashBack req ) {
        startPage();
        return getDataTable( service.list( req ) );
    }

    @GetMapping( value = "/{id}" )
    public AjaxResult getInfo( @PathVariable( "id" ) Integer id ) {
        return AjaxResult.success( service.selectById( id ) );
    }

    @PostMapping
    public AjaxResult add( @RequestBody ActivityWithdrawCashBack req ) {
        return toAjax( service.add( req ) );
    }

    @PutMapping
    public AjaxResult edit( @RequestBody ActivityWithdrawCashBack req ) {
        return toAjax( service.update( req ) );
    }

    @DeleteMapping( "/{bankCodes}" )
    public AjaxResult remove( @PathVariable String bankCodes ) {
        List<String> codes = Arrays.stream( bankCodes.split( "," ) ).map( String::trim ).collect( Collectors.toList() );
        return toAjax( service.deleteByBankCodes( codes ) );
    }

    @PutMapping( "/changeStatus" )
    public AjaxResult changeStatus( @RequestBody ActivityWithdrawCashBack req ) {
        return toAjax( service.updateStatus( req ) );
    }

    @GetMapping( "/getConfigBankList" )
    public AjaxResult getConfigBankList() {
        List<ConfigBank> list = service.getConfigBankList();
        return AjaxResult.success( list );
    }
}
