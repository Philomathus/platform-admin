package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.MultiKeyCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
public class UpdateNickNameTask {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Async
    public void updateNickNameCache() {
        Map<String, String> scanMap = stringRedisTemplate.execute( (RedisCallback<Map<String, String>>) connection -> {
            Map<String, String> resultMaps = new HashMap<>();

            JedisCommands    commands         = (JedisCommands) connection.getNativeConnection();
            MultiKeyCommands multiKeyCommands = (MultiKeyCommands) commands;

            ScanParams scanParams = new ScanParams();
            scanParams.match( "CX:platform:token-user:*" );
            scanParams.count( 500 );
            ScanResult<String> scan = multiKeyCommands.scan( "0", scanParams );
            while ( null != scan.getCursor() ) {
                for ( String scanResult : scan.getResult() ) {
                    Map<Object, Object> resultMap = stringRedisTemplate.opsForHash().entries( scanResult );
                    Object              nikeName  = resultMap.get( "nikeName" );
                    Object              userId    = resultMap.get( "userId" );
                    if ( nikeName != null && StringUtils.indexOfAny( nikeName.toString(), "花儿", "奶昔", "初见", "密爱" ) >= 0 ) {
                        resultMaps.put( userId.toString(), scanResult );
                    }
                }
                if ( !scan.getCursor().equals( "0" ) ) {
                    scan = multiKeyCommands.scan( scan.getCursor(), scanParams );
                } else {
                    break;
                }

            }
            return resultMaps;
        } );
        Set<String> keySet = scanMap.keySet();
        keySet.stream().sorted().limit( 200 ).collect( Collectors.toSet() );
    }
}
