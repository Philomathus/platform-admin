package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.MultiKeyCommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class UpdateNickNameTask {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${spring.profiles.active}")
    private String profile;

    private static <T> List<T> pageSubList(List<T> list, int pagesize, int currentPage) {
        return list.stream().skip((long) pagesize * (currentPage - 1)).limit(pagesize).collect(Collectors.toList());
    }

    @Async
    public void updateNickNameCache() throws Exception {

        if (!ArrayUtils.contains(new String[]{"7703"}, profile)) {
            return;
        }

        if (!redisUtil.adminLock(EnumLock.adminTask, getClass().getSimpleName(), 999999)) {
            return;
        }

        Map<String, String> scanMap = stringRedisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {
            Map<String, String> resultMaps = new HashMap<>();

            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            MultiKeyCommands multiKeyCommands = (MultiKeyCommands) commands;

            ScanParams scanParams = new ScanParams();
            scanParams.match("CX:platform:token-user:*");
            scanParams.count(1000);
            ScanResult<String> scan = multiKeyCommands.scan("0", scanParams);
            while (null != scan.getCursor()) {
                for (String scanResult : scan.getResult()) {
                    List<Object> multiGet = stringRedisTemplate.opsForHash().multiGet(scanResult, Arrays.asList("nickName", "userId"));
                    Object nickName = multiGet.get(0);
                    Object userId = multiGet.get(1);

                    if (nickName == null || userId == null) {
                        if( stringRedisTemplate.opsForHash().size(scanResult) == 1 ){
                            stringRedisTemplate.unlink(scanResult);
                        } else {
                            log.warn(scanResult + " === " + JsonUtil.object2Json(multiGet));
                        }
                        continue;
                    }

                    if (StringUtils.indexOfAny(nickName.toString(), "甜心") >= 0) {
                        resultMaps.put(userId.toString(), scanResult);
                    }
                }
                if (!scan.getCursor().equals("0")) {
                    scan = multiKeyCommands.scan(scan.getCursor(), scanParams);
                } else {
                    break;
                }

            }
            return resultMaps;
        });
        List<String> scanMapList = scanMap.keySet().stream().sorted().collect(Collectors.toList());

        log.warn("共扫描数量：{}", scanMapList.size());

        int pagesize = 200;
        int totalpage = (scanMapList.size() - 1) / pagesize + 1;
        for (int i = 1; i <= totalpage; i++) {
            List<String> subList = pageSubList(scanMapList, pagesize, i);
            List<MemberInfo> memberInfos = memberInfoMapper.selectNikeNameById(subList);
            for (MemberInfo memberInfo : memberInfos) {
                String key = scanMap.get(memberInfo.getId());
                stringRedisTemplate.opsForHash().put(key, "nickName", memberInfo.getNickName());
            }

            log.warn(memberInfos.size());
        }
        log.warn("缓存更新结束");
    }
}
