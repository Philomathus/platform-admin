package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.service.IServerOssService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping( "/upload/oss" )
public class UploadOssController {
	@Autowired
	private IServerOssService serverOssService;

	@PostMapping("{path}")
	public AjaxResult upload( @RequestParam( "file" ) MultipartFile file, @PathVariable String path ) throws IOException {
		String fileName = file.getOriginalFilename();
		String fileKey  = path + "/" + fileName;
		return AjaxResult.success("上传成功", serverOssService.uploadInputStream( file.getInputStream(), fileKey ) );
	}
}
