package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

@Log4j2
@Component
public class RedisCacheUtil {
    public static RedisCacheUtil me;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    void init() {
        me = this;
    }

    public <T> T get( Serializable cacheId, Class<T> clazz, Supplier<T> supplier ) {
        String   keyM   = "autoCache:" + clazz.getSimpleName() + ":" + cacheId;
        String   s      = redisUtil.strGet( keyM );
        if ( Objects.isNull( s ) ) {
            return this.update( keyM, supplier );
        } else {
            try {
                return JsonUtil.json2Object( s, JsonUtil.getJavaType( clazz.getName() ) );
            } catch ( Exception e ) {
                return this.update( keyM, supplier );
            }
        }
    }

    private <T> T update( String keyM, Supplier<T> supplier ) {
        if ( supplier == null || supplier.get() == null ) {
            return null;
        }
        T      apply = supplier.get();
        String valStr;
        if ( apply instanceof String ) {
            valStr = (String) apply;
        } else {
            valStr = JsonUtil.object2Json( apply );
        }
        redisUtil.strSet( keyM, valStr, Duration.ofMinutes( 5 ) );
        return apply;
    }

    public <T> boolean clear( Serializable cacheId, Class<T> clazz ) {
        String keyM = "autoCache:" + clazz.getSimpleName() + ":" + cacheId;
        return redisUtil.delete( keyM );
    }
}
