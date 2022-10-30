package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.cache.ServerImCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.RspServerIm;
import com.qiqilm.server.admin.domain.ServerIm;
import com.qiqilm.server.admin.mapper.ServerImMapper;
import com.qiqilm.server.admin.service.IServerImService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * IM即时通讯服务配置Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Log4j2
@Service
public class ServerImServiceImpl implements IServerImService {
    @Autowired
    private ServerImMapper    serverImMapper;
    @Autowired
    private LiveCacheUtil     liveCacheUtil;
    @Autowired
    private ServerImCacheUtil serverImCacheUtil;
    @Autowired
    private TokenService      tokenService;

    /**
     * 查询IM即时通讯服务配置
     *
     * @param id IM即时通讯服务配置ID
     *
     * @return IM即时通讯服务配置
     */
    @Override
    public ServerIm selectServerImById( Long id ) {
        return serverImMapper.selectServerImById( id );
    }

    /**
     * 查询IM即时通讯服务配置列表
     *
     * @param serverIm IM即时通讯服务配置
     *
     * @return IM即时通讯服务配置
     */
    @Override
    public List<RspServerIm> selectServerImList( ServerIm serverIm ) {
        return serverImMapper.selectServerImList( serverIm );
    }

    /**
     * 新增IM即时通讯服务配置
     *
     * @param serverIm IM即时通讯服务配置
     *
     * @return 结果
     */
    @Override
    public int insertServerIm( ServerIm serverIm ) {
        return serverImMapper.insertServerIm( serverIm );
    }

    /**
     * 修改IM即时通讯服务配置
     *
     * @param serverIm IM即时通讯服务配置
     *
     * @return 结果
     */
    @Override
    public int updateServerIm( ServerIm serverIm ) {
        int      i           = serverImMapper.updateServerIm( serverIm );
        ServerIm newServerIm = serverImMapper.selectServerImById( serverIm.getId() );
        if ( i > 0 && newServerIm.getIsEffect() == 1 ) {
            serverImCacheUtil.setServerIm( newServerIm );
        }
        return i;
    }

    /**
     * 批量删除IM即时通讯服务配置
     *
     * @param ids 需要删除的IM即时通讯服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerImByIds( Long[] ids ) {
        return serverImMapper.deleteServerImByIds( ids );
    }

    /**
     * 删除IM即时通讯服务配置信息
     *
     * @param id IM即时通讯服务配置ID
     *
     * @return 结果
     */
    @Override
    public int deleteServerImById( Long id ) {
        return serverImMapper.deleteServerImById( id );
    }

    @Override
    public AjaxResult effect( long id ) {
        if ( liveCacheUtil.checkAndSetLock( "server", "im", 60 ) ) {
            return AjaxResult.error( "切换间隔必须大于1分钟" );
        }
        ServerIm info = this.selectServerImById( id );
        if ( Objects.isNull( info ) ) {
            return AjaxResult.error( "数据记录不存在" );
        }
        String[] codes = info.toCodes();
        if ( Objects.isNull( codes ) || codes.length == 0 ) {
            return AjaxResult.error( "不完整的数据记录" );
        }
        try {
            this.updateEffect( id );
            serverImCacheUtil.setServerIm( info );

            // 源切换通知  需要有个主播登录，才能激活
            //			String timSdkappid = serverImCacheUtil.getValue( "tim_sdkappid" );
            //			String timSdkKey   = serverImCacheUtil.getValue( "tim_sdk_key" );
            String identifier = serverImCacheUtil.getValue( "tim_identifier" );
            //			String singn = TLSSigAPIv2.genSig( timSdkappid, timSdkKey, identifier,
            //					TimeUnit.DAYS.toSeconds( 365 ) );
            //			liveCacheUtil.addAdminSign( identifier, singn );
            liveCacheUtil.delAdminSign( identifier );

            return AjaxResult.success();
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        return AjaxResult.error();
    }

    @Transactional( rollbackFor = Exception.class )
    void updateEffect( long id ) {
        List<ServerIm> serverImList = serverImMapper.selectServerImByEffect();

        for ( ServerIm serverIm : serverImList ) {
            ServerIm update = new ServerIm();
            update.setId( serverIm.getId() );
            update.setIsEffect( 0 );
            serverImMapper.updateServerIm( update );
        }
        ServerIm update = new ServerIm();
        update.setId( id );
        update.setIsEffect( 1 );
        serverImMapper.updateServerIm( update );
    }
}
