package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.mapper.LiveEnterLogMapper;
import com.qiqilm.server.admin.mapper.LiveLogMapper;
import com.qiqilm.server.admin.service.ILiveLogService;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class LiveLogServiceImpl implements ILiveLogService {
    @Autowired
    private LiveLogMapper liveLogMapper;

    @Resource
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private RobotMessage robotMessage;

    @Autowired
    private SysConfigCacheUtil sysConfigCacheUtil;
	@Value( "${spring.profiles.active}" )
	private String profile;

    public void banchUpdateEnterLog() {
        List<String> strArticleCountList = strRedisTemplate.opsForList().range(Constants.LIVEENTERLOG, 10000, -1);
        if (CollectionUtils.isEmpty(strArticleCountList)) {
            return ;
        }

        strRedisTemplate.unlink(Constants.LIVEENTERLOG);
		String flag = sysConfigCacheUtil.getConf( "messageBot" );
		String online_user_telegram = sysConfigCacheUtil.getConf( "online_user_telegram" );
		if ( "0".equals( flag ) ) {
			return;
		}
		if(!profile.startsWith("77")||profile.equals("7700")){
			return;
		}
        Map<String, Integer> cmap = new HashMap<>();
        for (String id : strArticleCountList) {
            cmap.putIfAbsent(id, 0);
            cmap.put(id, cmap.get(id) + 1);
        }

        long now = System.currentTimeMillis();
        dobanchUpdate(cmap);
        log.info("批量插入进直播间会员数：{},执行时间:{}ms", cmap.size(), System.currentTimeMillis() - now);

        try {
            String paytext="10分钟直播间活跃人数测试数据:"+cmap.size();
            robotMessage.sendByChatId(paytext, "-485027924");

        } catch (Exception e) {
            log.error("电报发送消息失败" + e.getMessage());
        }
    }
    public void dobanchUpdate(Map<String, Integer> cmap) {
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        LiveEnterLogMapper mapper = session.getMapper(LiveEnterLogMapper.class);
        String sp = "_";
        int count = 0;
        for (String id : cmap.keySet()) {
            try {
                String[] l = id.split(sp);
                int i = mapper.addTimes(id, cmap.get(id));
                if (i < 1) {
                    mapper.addEnterLog(id, l[1].concat(sp).concat(l[2]), l[0], cmap.get(id));
                }
                count += 1;
                if (count >= 500) {
                    session.commit();
                    count = 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (count > 0) {
            session.commit();
        }
        session.close();

   }

}
