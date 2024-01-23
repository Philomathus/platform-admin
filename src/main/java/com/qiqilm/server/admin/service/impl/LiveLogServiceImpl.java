package com.qiqilm.server.admin.service.impl;

import com.google.common.collect.ImmutableMap;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.LiveEnterLogMapper;
import com.qiqilm.server.admin.service.ILiveLogService;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Log4j2
public class LiveLogServiceImpl implements ILiveLogService {

    @Resource
    private StringRedisTemplate strRedisTemplate;
    @Resource
    private SqlSessionTemplate  sqlSessionTemplate;

    private static final long pageSize = 1000;

    public Map<String, Long> banchUpdateEnterLog() {
        Long size = strRedisTemplate.opsForList().size( Constants.LIVEENTERLOG );
        if ( size == null || size == 0 ) {
            log.error( "进直播间会员数为空" );
            return Collections.emptyMap();
        }
        List<String> strArticleCountList = new ArrayList<>();

        long pageCount = size % pageSize == 0 ? ( size / pageSize ) : ( size / pageSize + 1 );
        for ( int pageNum = 1; pageNum <= pageCount; pageNum++ ) {
            long from = ( pageNum - 1 ) * pageSize;
            long to   = Math.min( pageNum * pageSize, size );
            if ( from > to ) {
                from = to;
            }
            strArticleCountList.addAll( strRedisTemplate
                    .opsForList()
                    .range( Constants.LIVEENTERLOG, from, pageNum == pageCount ? -1 : to ) );
        }
        strRedisTemplate.unlink( Constants.LIVEENTERLOG );

        Map<String, Integer> cmap = new HashMap<>();
        for ( String id : strArticleCountList ) {
            cmap.putIfAbsent( id, 0 );
            cmap.put( id, cmap.get( id ) + 1 );
        }
        long now = System.currentTimeMillis();
        try {
            dobanchUpdate( cmap );
        } catch ( Exception e ) {
            log.error( "批量插入进直播间会员数异常", e );
        }

        log.info( "批量插入进直播间会员数：{},执行时间:{}ms", cmap.size(), System.currentTimeMillis() - now );
        return ImmutableMap.of( "size", size, "cmapSize", ( long ) cmap.size() );

    }

    public void dobanchUpdate( Map<String, Integer> cmap ) {
        SqlSession         session = sqlSessionTemplate.getSqlSessionFactory().openSession( ExecutorType.BATCH, false );
        LiveEnterLogMapper mapper  = session.getMapper( LiveEnterLogMapper.class );
        String             sp      = "_";
        int                count   = 0;
        for ( String id : cmap.keySet() ) {
            try {
                String[] l = id.split( sp );
                int      i = mapper.addTimes( id, cmap.get( id ) );
                if ( i < 1 ) {
                    mapper.addEnterLog( id, l[ 1 ].concat( sp ).concat( l[ 2 ] ), l[ 0 ], cmap.get( id ) );
                }
                count += 1;
                if ( count >= 500 ) {
                    session.commit();
                    count = 0;
                }

            } catch ( Exception e ) {
                log.error( "插入会员进入日志异常", e );
            }
        }
        if ( count > 0 ) {
            session.commit();
        }
        session.close();

    }

}
