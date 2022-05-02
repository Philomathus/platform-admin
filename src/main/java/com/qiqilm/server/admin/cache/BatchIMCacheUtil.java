package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class BatchIMCacheUtil {

    private static final String BATCH_IM = "platform-lottery:batchIm:list";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void push(String data) {
        redisUtil.lRightPush(BATCH_IM, data);
    }

    public List<String> mutiGet() {
        Long size = redisUtil.lSize(BATCH_IM);
        List<Object> resultList = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            byte[] key = BATCH_IM.getBytes(StandardCharsets.UTF_8);
            stringRedisConn.watch(key);
            stringRedisConn.multi();
            List<byte[]> result = stringRedisConn.lRange(key, 0, size);
            stringRedisConn.lTrim(key, size, 0);
            //stringRedisConn.unwatch();
            stringRedisConn.exec();
            return result;
        });
        if (!resultList.isEmpty()) {
            resultList = (List<Object>) resultList.get(0);
            if (!resultList.isEmpty()) {
                return (List<String>) resultList.get(0);
            }
        }
        return Collections.emptyList();
    }
}
