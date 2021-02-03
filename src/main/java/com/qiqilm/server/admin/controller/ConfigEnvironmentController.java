package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 环境参数配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/admin/configEnvironment" )
public class ConfigEnvironmentController extends BaseController {
	@Autowired
	private IConfigEnvironmentService configEnvironmentService;
	@Autowired
    private HttpServletRequest request;

	/**
	 * 查询环境参数配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(ConfigEnvironment configEnvironment) {
		startPage();
		List<ConfigEnvironment> list = configEnvironmentService.selectConfigEnvironmentList(configEnvironment);
		return getDataTable( list );
	}

	/**
	 * 导出环境参数配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:export')" )
	@Log( title = "导出环境参数配置列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public AjaxResult export(ConfigEnvironment configEnvironment) {
		List<ConfigEnvironment>      list = configEnvironmentService.selectConfigEnvironmentList(configEnvironment);
		ExcelUtil<ConfigEnvironment> util = new ExcelUtil<ConfigEnvironment>(ConfigEnvironment. class);
		return util.exportExcel( list, "configEnvironment" );
	}

	/**
	 * 获取环境参数配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:query')" )
	@GetMapping( value = "/{envCode}" )
	public AjaxResult getInfo( @PathVariable( "envCode" ) String envCode) {
		return AjaxResult.success( configEnvironmentService.selectConfigEnvironmentById(envCode) );
	}

	/**
	 * 新增环境参数配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:add')" )
	@Log( title = "新增环境参数配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigEnvironment configEnvironment) {
		return configEnvironmentService.insertConfigEnvironment(configEnvironment);
	}

	/**
	 * 修改环境参数配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:edit')" )
	@Log( title = "修改环境参数配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigEnvironment configEnvironment) {
		return toAjax( configEnvironmentService.updateConfigEnvironment(configEnvironment) );
	}

	/**
	 * 修改环境参数配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:edit')" )
	@Log( title = "批量修改环境参数配置", businessType = BusinessType.UPDATE )
	@PostMapping("/editList")
	public AjaxResult edit(@RequestBody ArrayList<ConfigEnvironment> configEnvironments) {
        try {
            for (ConfigEnvironment configEnvironment : configEnvironments) {
                configEnvironmentService.updateConfigEnvironment(configEnvironment);
            }
            return AjaxResult.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error();
        }

	}

	/**
	 * 获取环境参数头所对应的index
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:edit')" )
	@GetMapping("/getTitleIndex")
	public AjaxResult getTitleIndex(String title, String code) {
        return  configEnvironmentService.getTitleIndex(title,code);
	}

	/**
	 * 删除环境参数配置
	 */
	@PreAuthorize( "@ss.hasPermi('admin:configEnvironment:remove')" )
	@Log( title = "环境参数配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{envCodes}" )
	public AjaxResult remove( @PathVariable String[] envCodes ) {
		return toAjax( configEnvironmentService.deleteConfigEnvironmentByIds( envCodes ) );
	}
}
