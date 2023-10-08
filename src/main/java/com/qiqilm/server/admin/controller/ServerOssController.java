package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IServerOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * oss文件存储服务配置Controller
 *
 * @author 77tv
 * @date 2021-01-27
 */
@RestController
@RequestMapping( "/server/oss" )
public class ServerOssController extends BaseController {
	@Autowired
	private IServerOssService serverOssService;

	/**
	 * 查询oss文件存储服务配置列表
	 */
	@PreAuthorize( "@ss.hasPermi('server:oss:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( ServerOss serverOss ) {
		startPage();
		List<ServerOss> list = serverOssService.selectServerOssList( serverOss);
		return getDataTable( list );
	}

	/**
	 * 获取oss文件存储服务配置详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('server:oss:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( serverOssService.selectServerOssById( id ) );
	}

	/**
	 * 新增oss文件存储服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:oss:add')" )
	@Log( title = "oss文件存储服务配置", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ServerOss serverOss ) {
		return toAjax( serverOssService.insertServerOss( serverOss ) );
	}

	/**
	 * 修改oss文件存储服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:oss:edit')" )
	@Log( title = "oss文件存储服务配置", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ServerOss serverOss ) {
		return toAjax( serverOssService.updateServerOss( serverOss ) );
	}

	/**
	 * 删除oss文件存储服务配置
	 */
	@PreAuthorize( "@ss.hasPermi('server:oss:remove')" )
	@Log( title = "oss文件存储服务配置", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( serverOssService.deleteServerOssByIds( ids ) );
	}

	@PreAuthorize( "@ss.hasPermi('server:oss:effect')" )
	@PatchMapping( "/effect/{id}" )
	@Log( title = "oss文件存储服务配置", businessType = BusinessType.EFFECT )
	public AjaxResult effect( @PathVariable long id ) {
		return toAjax( serverOssService.effect( id ) );
	}

	@PreAuthorize( "@ss.hasPermi('server:oss:ossTest')" )
	@PostMapping( "/ossTest/{path}/{id}" )
	@Log( title = "oss文件存储服务配置", businessType = BusinessType.UPLOAD )
	public AjaxResult uploadOssTest( @RequestParam( "file" ) MultipartFile file,
									 @PathVariable String path,
									 @PathVariable long id ) throws IOException {
		return AjaxResult.success( "上传成功", serverOssService.uploadOssTest( file, path, id ) );
	}
}
