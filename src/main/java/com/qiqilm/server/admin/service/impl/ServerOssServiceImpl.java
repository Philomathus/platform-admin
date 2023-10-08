package com.qiqilm.server.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.qiqilm.server.admin.cache.ServerOssCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import com.qiqilm.server.admin.domain.ServerOss;
import com.qiqilm.server.admin.mapper.ServerOssMapper;
import com.qiqilm.server.admin.service.IServerOssService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * oss文件存储服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
@Log4j2
public class ServerOssServiceImpl implements IServerOssService {
    @Autowired
    private ServerOssMapper    serverOssMapper;
    @Autowired
    private ServerOssCacheUtil serverOssCacheUtil;

    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;

    /**
     * 查询oss文件存储服务配置
     *
     * @param id oss文件存储服务配置ID
     *
     * @return oss文件存储服务配置
     */
    @Override
    public ServerOss selectServerOssById( Long id ) {
        ServerOss serverOss = serverOssMapper.selectServerOssById( id );
        serverOss.setAccessKey( new StringBuilder( serverOss.getAccessKey() ).replace( 5,15,"*******" ).toString() ) ;
        serverOss.setAccessSecret( serverOss.getAccessSecret().replace( serverOss.getAccessSecret().substring( 7,30 ),
                "********" ));
        return serverOss;
    }

    /**
     * 查询oss文件存储服务配置列表
     *
     * @param serverOss oss文件存储服务配置
     *
     * @return oss文件存储服务配置
     */
    @Override
    public List<ServerOss> selectServerOssList( ServerOss serverOss ) {
        List<ServerOss> ossList = serverOssMapper.selectServerOssList( serverOss );
        String hideAccess = sysConfigCacheUtil.getConf( "ossKeyHide" );
        if ( "0".equals( hideAccess )  ) {
            ossList.forEach(( r)-> {
                r.setAccessKey("*****");
                r.setAccessSecret("*****");
            });
        }
        return ossList;
    }

    /**
     * 新增oss文件存储服务配置
     *
     * @param serverOss oss文件存储服务配置
     *
     * @return 结果
     */
    @Override
    public int insertServerOss( ServerOss serverOss ) {
        serverOss.setCreateTime( DateUtils.getNowDate() );
        return serverOssMapper.insertServerOss( serverOss );
    }

    /**
     * 修改oss文件存储服务配置
     *
     * @param serverOss oss文件存储服务配置
     *
     * @return 结果
     */
    @Override
    public int updateServerOss( ServerOss serverOss ) {
        serverOss.setUpdateTime( DateUtils.getNowDate() );
        int i = serverOssMapper.updateServerOss( serverOss );
        if ( i > 0 ) {
            serverOssCacheUtil.clear();
        }
        return i;
    }

    /**
     * 批量删除oss文件存储服务配置
     *
     * @param ids 需要删除的oss文件存储服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerOssByIds( Long[] ids ) {
        return serverOssMapper.deleteServerOssByIds( ids );
    }

    /**
     * 删除oss文件存储服务配置信息
     *
     * @param id oss文件存储服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerOssById( Long id ) {
        return serverOssMapper.deleteServerOssById( id );
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public int effect( long id ) {
        //只允许激活一个
        List<ServerOss> serverOssList = serverOssMapper.selectServerOssList( null );
        for ( ServerOss serverOss : serverOssList ) {
            ServerOss update = new ServerOss();
            update.setId( serverOss.getId() );
            update.setIsEffect( 0 );
            serverOssMapper.updateServerOss( update );
        }
        ServerOss update = new ServerOss();
        update.setId( id );
        update.setIsEffect( 1 );
        int i = serverOssMapper.updateServerOss( update );
        if ( i > 0 ) {
            serverOssCacheUtil.clear();
        }
        return i;
    }

    @Override
    public String uploadInputStream( InputStream inputStream, String fileKey ) {
        ServerOss serverOss = serverOssCacheUtil.getEffect();
        return this.uploadOss( inputStream, fileKey, serverOss );
    }

    @Override
    public String uploadOssTest( MultipartFile file, String path, long id ) throws IOException {
        String      fileName      = file.getOriginalFilename();
        String      extension     = FilenameUtils.getExtension( fileName );
        InputStream inputStream   = file.getInputStream();
        File        newFile       = new File( System.getProperty( "java.io.tmpdir" ) + fileName );
        Path        newFileToPath = newFile.toPath();
        IOUtils.copy( inputStream, Files.newOutputStream( newFileToPath ) );
        String rFileName = DigestUtils.md5Hex( Files.newInputStream( newFileToPath ) );
        String fileKey   = LiveCenterConfig.me.getProfile() + "/" + path + "/" + rFileName + "." + extension;

        ServerOss serverOss = serverOssMapper.selectServerOssById( id );
        switch ( serverOss.getProvider() ) {
        case 0:
            String url = this.uploadInputStream( Files.newInputStream( newFileToPath ), fileKey );
            newFile.delete();
            String s = serverOss.getEndpoint() + url;
            log.warn( s );
            return s;
        case 1:
            this.amazonawsUpload( fileKey, serverOss, newFile );
            newFile.delete();
            String s1 = serverOss.getEndpoint() + "/" + fileKey;
            log.warn( s1 );
            return s1;
        default:
            return null;
        }
    }

    private String uploadOss( InputStream inputStream, String fileKey, ServerOss serverOss ) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build( serverOss.getEndpoint(), serverOss.getAccessKey(),
                serverOss.getAccessSecret() );
        // 上传文件流
        ossClient.putObject( serverOss.getBucket(), fileKey, inputStream );
        // 关闭client
        ossClient.shutdown();
        return "/" + fileKey;
    }

    @Override
    public void amazonawsUpload( String path, ServerOss serverOss, File newFile ) {
        Regions clientRegion = null;//地区
        if ( StringUtils.isNotBlank( serverOss.getVhost() ) ) {
            try {
                clientRegion = Regions.fromName( serverOss.getVhost() );
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
        }
        if ( clientRegion == null ) {
            log.error( "亚马逊上传,地区未配置或配置错误,默认配置香港地区 - ossId:{};Region:{}", serverOss.getId(), serverOss.getVhost() );
            clientRegion = Regions.AP_EAST_1;
        }
        String bucketName = serverOss.getBucket();//桶的名称
        try {
            BasicAWSCredentials creds = new BasicAWSCredentials( serverOss.getAccessKey(), serverOss.getAccessSecret() );
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion( clientRegion )
                                                     .withCredentials( new AWSStaticCredentialsProvider( creds ) )
                                                     .build();//创建证书及注册地址
            s3Client.putObject( bucketName, path, newFile );
            s3Client.shutdown();
        } catch ( AmazonServiceException e ) {
            e.printStackTrace();
        } catch ( SdkClientException e ) {
            e.printStackTrace();
        }
    }

    public void kuaiKuaiYun( MultipartFile file, String path, ServerOss serverOss, File newFile ) {

        try {
            byte[] bytes = new byte[ 0 ];
            try {
                bytes = FileUtils.readFileToByteArray( newFile );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength( bytes.length );
            //设置加密  加密算法为  AES256
            objectMetadata.setSSEAlgorithm( ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION );
            PutObjectRequest putObjectRequest = new PutObjectRequest( serverOss.getBucket(), path,
                    new ByteArrayInputStream( bytes ), objectMetadata ).withCannedAcl( CannedAccessControlList.PublicRead );
            //设置文件图片上传读写权限。访问继承桶的权限，不设置则单个图片无法显示。权限默认私有。
            BasicAWSCredentials creds = new BasicAWSCredentials( serverOss.getAccessKey(), serverOss.getAccessSecret() );
            //创建安全证书注册
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider( creds ) )
                                                     .withEndpointConfiguration( new AwsClientBuilder.EndpointConfiguration( serverOss.getEndpoint(), "oss-cn-quanzhou.kz.cc" ) )//上传地址和区域
                                                     .build();
            //通过访问第三方，将文件上传到亚马逊
            s3Client.putObject( putObjectRequest );
            s3Client.shutdown();
        } catch ( AmazonServiceException e ) {
            e.printStackTrace();
        }
    }
}
