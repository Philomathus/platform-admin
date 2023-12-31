package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

//@Component
@Log4j2
public class MemberTask implements Serializable {

    @Resource
    private RedisUtil redisUtil;

    private static final long serialVersionUID = 1L;

    //@Scheduled(cron = "0/30 * * * * ?")
    public void checkLogin() {
        log.info( "检测用户超时" + DateUtils.getTime() );
        Map<Object, Object>                 tokenKeys = redisUtil.hGetAll( "tokenKeys" );
        Iterator<Map.Entry<Object, Object>> entries   = tokenKeys.entrySet().iterator();
        while ( entries.hasNext() ) {
            Map.Entry<Object, Object> entry      = entries.next();
            Object                    value      = entry.getValue();
            Map                       map        = JsonUtil.json2Object( ( String ) value, Map.class );
            Object                    expireDate = map.get( "expireDate" );
            if ( ( long ) expireDate < System.currentTimeMillis() ) {
                String token    = JsonUtil.json2Object( ( String ) map.get( "loginUser" ), LoginUser.class ).getToken();
                String tokenKey = TokenService.getTokenKey( token );
                redisUtil.hDelete( "tokenKeys", tokenKey );
            }

        }
    }

}
