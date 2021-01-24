package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.SysDictData;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ISysDictDataService;
import com.qiqilm.server.admin.service.ISysDictTypeService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.SecurityUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author 77tv
 */
@RestController
@RequestMapping( "/system/dict/data" )
public class SysDictDataController extends BaseController {
	@Autowired
	private ISysDictDataService dictDataService;
	@Autowired
	private ISysDictTypeService dictTypeService;

	@PreAuthorize( "@ss.hasPermi('system:dict:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( SysDictData dictData ) {
		startPage();
		List<SysDictData> list = dictDataService.selectDictDataList( dictData );
		return getDataTable( list );
	}

	@Log( title = "字典数据", businessType = BusinessType.EXPORT )
	@PreAuthorize( "@ss.hasPermi('system:dict:export')" )
	@GetMapping( "/export" )
	public AjaxResult export( SysDictData dictData ) {
		List<SysDictData>      list = dictDataService.selectDictDataList( dictData );
		ExcelUtil<SysDictData> util = new ExcelUtil<>( SysDictData.class );
		return util.exportExcel( list, "字典数据" );
	}

	/**
	 * 查询字典数据详细
	 */
	@PreAuthorize( "@ss.hasPermi('system:dict:query')" )
	@GetMapping( value = "/{dictCode}" )
	public AjaxResult getInfo( @PathVariable Long dictCode ) {
		return AjaxResult.success( dictDataService.selectDictDataById( dictCode ) );
	}

	/**
	 * 根据字典类型查询字典数据信息
	 */
	@GetMapping( value = "/type/{dictType}" )
	public AjaxResult dictType( @PathVariable String dictType ) {
		List<SysDictData> data = dictTypeService.selectDictDataByType( dictType );
		if ( StringUtils.isNull( data ) ) {
			data = new ArrayList<>();
		}
		return AjaxResult.success( data );
	}

	/**
	 * 新增字典类型
	 */
	@PreAuthorize( "@ss.hasPermi('system:dict:add')" )
	@Log( title = "字典数据", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @Validated @RequestBody SysDictData dict ) {
		dict.setCreateBy( SecurityUtils.getUsername() );
		return toAjax( dictDataService.insertDictData( dict ) );
	}

	/**
	 * 修改保存字典类型
	 */
	@PreAuthorize( "@ss.hasPermi('system:dict:edit')" )
	@Log( title = "字典数据", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @Validated @RequestBody SysDictData dict ) {
		dict.setUpdateBy( SecurityUtils.getUsername() );
		return toAjax( dictDataService.updateDictData( dict ) );
	}

	/**
	 * 删除字典类型
	 */
	@PreAuthorize( "@ss.hasPermi('system:dict:remove')" )
	@Log( title = "字典类型", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{dictCodes}" )
	public AjaxResult remove( @PathVariable Long[] dictCodes ) {
		return toAjax( dictDataService.deleteDictDataByIds( dictCodes ) );
	}
}
