package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.config.Tv77Config;
import com.qiqilm.server.admin.utils.FileUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用请求处理
 *
 * @author 77tv
 */
@Log4j2
@RestController
public class SysCommonController {

	/**
	 * 通用下载请求
	 *
	 * @param fileName 文件名称
	 * @param delete   是否删除
	 */
	@GetMapping( "common/download" )
	public void fileDownload( String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request ) {
		try {
			if ( !FileUtils.checkAllowDownload( fileName ) ) {
				throw new Exception( StringUtils.format( "文件名称({})非法，不允许下载。 ", fileName ) );
			}
			String realFileName = System.currentTimeMillis() + fileName.substring( fileName.indexOf( "_" ) + 1 );
			String filePath     = Tv77Config.getDownloadPath() + fileName;

			response.setContentType( MediaType.APPLICATION_OCTET_STREAM_VALUE );
			FileUtils.setAttachmentResponseHeader( response, realFileName );
			FileUtils.writeBytes( filePath, response.getOutputStream() );
			if ( delete ) {
				FileUtils.deleteFile( filePath );
			}
		} catch ( Exception e ) {
			log.error( "下载文件失败", e );
		}
	}
}
