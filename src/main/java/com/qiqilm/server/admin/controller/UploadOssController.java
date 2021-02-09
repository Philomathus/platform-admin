package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.vo.AjaxResult;
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
	private IServerOssService serverOssService;

	@PostMapping( "{path}" )
	public AjaxResult upload( @RequestParam( "file" ) MultipartFile file, @PathVariable String path ) throws IOException {
		String      fileName    = file.getOriginalFilename();
		String      extension   = FilenameUtils.getExtension( fileName );
		InputStream inputStream = file.getInputStream();
		File        newFile     = new File( System.getProperty( "java.io.tmpdir" ) + fileName );
		IOUtils.copy( inputStream, new FileOutputStream( newFile ) );
		String rFileName = DigestUtils.md5Hex( new FileInputStream( newFile ) );
		String fileKey   = path + "/" + rFileName + FilenameUtils.EXTENSION_SEPARATOR + extension;
		String url       = serverOssService.uploadInputStream( new FileInputStream( newFile ), fileKey );
		newFile.delete();
		return AjaxResult.success( "上传成功", url );
	}
}
