package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.PageUtil;
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

    @Async
    public void updateNickNameCache() throws Exception {

        if (!ArrayUtils.contains(new String[]{"7708", "7710"}, profile)) {
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
            scanParams.count(500);
            ScanResult<String> scan = multiKeyCommands.scan("0", scanParams);
            while (null != scan.getCursor()) {
                log.warn("扫描到了{}", scan.getResult().size());
                for (String scanResult : scan.getResult()) {
                    Map<Object, Object> resultMap = stringRedisTemplate.opsForHash().entries(scanResult);
                    Object nikeName = resultMap.get("nikeName");
                    Object userId = resultMap.get("userId");

                    log.warn("userId:{},nikeName:{}", userId.toString(), nikeName.toString() );

                    if (StringUtils.indexOfAny(nikeName.toString(), "花儿", "奶昔", "初见", "密爱") >= 0) {
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
        int totalpage = scanMapList.size() % pagesize;
        for (int i = 0; i < totalpage; i++) {
            List<String> subList = PageUtil.pageBySubList(scanMapList, pagesize, i);
            List<MemberInfo> memberInfos = memberInfoMapper.selectNikeNameById(subList);
            for (MemberInfo memberInfo : memberInfos) {
                String key = scanMap.get(memberInfo.getId());
                log.warn("key:{},nikeName:{}", key, memberInfo.getNickName() );
                stringRedisTemplate.opsForHash().put(key, "nikeName", memberInfo.getNickName());
            }
        }
        log.warn("缓存更新结束");
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.indexOfAny("花儿Zzjzs", "花儿", "奶昔", "初见", "密爱"));
    }
}
