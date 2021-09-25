package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.cache.ServerOssCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.service.IServerOssService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Log4j2
@RestController
@RequestMapping( "/upload/oss" )
public class UploadOssController {
	@Autowired
	private IServerOssService  serverOssService;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;
	@Autowired
	private ServerOssCacheUtil serverOssCacheUtil;
	@PostMapping( "{path}" )
	public AjaxResult upload( @RequestParam( "file" )
										  MultipartFile file, @PathVariable String path )
			throws IOException {
		ServerOss serverOss = serverOssCacheUtil.getEffect();
		String      fileName    = file.getOriginalFilename();
		String url = null;
		String      extension   = FilenameUtils.getExtension( fileName );
		InputStream inputStream = file.getInputStream();
		File        newFile     = new File( System.getProperty( "java.io.tmpdir" ) + fileName );
		IOUtils.copy( inputStream, new FileOutputStream( newFile ) );
		String rFileName = DigestUtils.md5Hex( new FileInputStream( newFile ) );
		String fileKey = sysConfigCacheUtil.getConf( "agent_id" ) + "/" + path + "/" + rFileName
				+ FilenameUtils.EXTENSION_SEPARATOR + extension;
		if (serverOss.getProvider()==0){//阿里云
			url = serverOssService.uploadInputStream( new FileInputStream( newFile ), fileKey );
			newFile.delete();
		}
		if (serverOss.getProvider()==1){//亚马逊
			serverOssService.amazonawsUpload(file,fileKey,serverOss,newFile);
			url=serverOss.getEndpoint()+ fileKey;
		}
		if(serverOss.getProvider()==2){
			serverOssService.kuaiKuaiYun(file,fileKey,serverOss,newFile);
			url="https://"+serverOss.getBucket()+FilenameUtils.EXTENSION_SEPARATOR+"oss-cn-quanzhou.kz.cc/"+fileKey;
		}
		return AjaxResult.success( "上传成功",url);
	}
}
