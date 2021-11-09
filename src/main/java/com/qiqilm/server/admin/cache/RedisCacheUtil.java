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

    public <T> T get( Serializable cacheId, Supplier<T> supplier ) {
        Class<?> tClass = supplier.get().getClass();
        String   keyM   = "autoCache:" + tClass.getSimpleName() + ":" + cacheId;
        String   s      = redisUtil.strGet( keyM );
        if ( Objects.isNull( s ) ) {
            return this.update( keyM, supplier );
        } else {
            try {
                return JsonUtil.json2Object( s, JsonUtil.getJavaType( tClass.getName() ) );
            } catch ( Exception e ) {
                return this.update( keyM, supplier );
            }
        }
    }

    private <T> T update( String keyM, Supplier<T> supplier ) {
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

    public <T> boolean clear( Serializable cacheId, T t ) {
        String keyM = "autoCache:" + t.getClass().getSimpleName() + ":" + cacheId;
        return redisUtil.unlink( keyM );
    }
}
