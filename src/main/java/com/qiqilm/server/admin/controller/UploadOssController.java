package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.cache.ServerOssCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.service.IServerOssService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@RestController
@RequestMapping( "/upload/oss" )
public class UploadOssController {
    @Resource
    private IServerOssService  serverOssService;
    @Resource
    private ServerOssCacheUtil serverOssCacheUtil;

    @PostMapping( "{path}" )
    public AjaxResult upload( @RequestParam( "file" ) MultipartFile file, @PathVariable String path ) throws IOException {
        ServerOss   serverOss     = serverOssCacheUtil.getEffect();
        String      fileName      = file.getOriginalFilename();
        String      url           = null;
        String      extension     = FilenameUtils.getExtension( fileName );
        InputStream inputStream   = file.getInputStream();
        File        newFile       = new File( System.getProperty( "java.io.tmpdir" ) + fileName );
        Path        newFileToPath = newFile.toPath();
        IOUtils.copy( inputStream, Files.newOutputStream( newFileToPath ) );
        String rFileName = DigestUtils.md5Hex( Files.newInputStream( newFileToPath ) );
        String fileKey   = LiveCenterConfig.me.getProfile() + "/" + path + "/" + rFileName + "." + extension;

        log.warn( fileKey );
        if ( serverOss.getProvider() == 0 ) {//阿里云
            url = serverOssService.uploadInputStream( Files.newInputStream( newFileToPath ), fileKey );
            newFile.delete();
        }
        if ( serverOss.getProvider() == 1 ) {//亚马逊
            serverOssService.amazonawsUpload( fileKey, serverOss, newFile );
            url = "/" + fileKey;
            newFile.delete();
        }
        return AjaxResult.success( "上传成功", url );
    }
}
