package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigMoneydes;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IConfigMoneydesService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/pay/configMoneydes" )
public class ConfigMoneydesController extends BaseController {
	@Autowired
	private IConfigMoneydesService configMoneydesService;

	/**
	 * 查询【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ConfigMoneydes configMoneydes ) {
		startPage();
		List<ConfigMoneydes> list = configMoneydesService.selectConfigMoneydesList( configMoneydes );
		return getDataTable( list );
	}

	/**
	 * 导出【请填写功能名称】列表
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( ConfigMoneydes configMoneydes, HttpServletResponse response ) {
		List<ConfigMoneydes> list = configMoneydesService.selectConfigMoneydesList( configMoneydes );
		ExportExcelUtil.exportExcel( list, "资金配置详细", "资金配置详细表", ConfigMoneydes.class, response );
	}

	/**
	 * 获取【请填写功能名称】详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:query')" )
	@GetMapping( value = "/{mdId}" )
	public AjaxResult getInfo( @PathVariable( "mdId" ) Long mdId ) {
		return AjaxResult.success( configMoneydesService.selectConfigMoneydesById( mdId ) );
	}

	/**
	 * 新增【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ConfigMoneydes configMoneydes ) {
		return toAjax( configMoneydesService.insertConfigMoneydes( configMoneydes ) );
	}

	/**
	 * 修改【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ConfigMoneydes configMoneydes ) {
		return toAjax( configMoneydesService.updateConfigMoneydes( configMoneydes ) );
	}

	/**
	 * 删除【请填写功能名称】
	 */
	@PreAuthorize( "@ss.hasPermi('pay:configMoneydes:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{mdIds}" )
	public AjaxResult remove( @PathVariable Long[] mdIds ) {
		return toAjax( configMoneydesService.deleteConfigMoneydesByIds( mdIds ) );
	}

	/**
	 * 入款备注选择列表
	 *
	 * @return
	 */
	@GetMapping( "/moneydes" )
	public AjaxResult findEffectPayType() {
		ConfigMoneydes       configMoneydes = new ConfigMoneydes();
		List<ConfigMoneydes> data           = configMoneydesService.selectConfigMoneydesList( configMoneydes );
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success( data );
	}
}
