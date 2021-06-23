package com.qiqilm.server.admin.interceptor;

import com.alibaba.fastjson.JSON;
import com.qiqilm.server.admin.annotation.AccessLimit;
import com.qiqilm.server.admin.cache.AccessCache;
import com.qiqilm.server.admin.constant.AdminConstants;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //判断请求是否属于方法的请求
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //获取方法中的注解，看是否有该注解
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            String token = request.getHeader(Constants.TOKEN);
            String cacheKey = AdminConstants.ACCESS_TIMES + token + ":" + request.getRequestURI();
            String string = (String) redisUtil.hGet( cacheKey, "accesslimit" );
            int rand = (int) (Math.random()*(600-300)+300);
            AccessCache accessCache = JSON.parseObject(string,AccessCache.class);
            if (accessCache == null){
                //第一次访问
                accessCache = new AccessCache();
                accessCache.setFirstVisitTimestamp(System.currentTimeMillis());
                accessCache.setAccessCount(1);
                redisUtil.hSet(cacheKey,"accesslimit", JSON.toJSONString(accessCache));
                redisUtil.expire(cacheKey, Duration.ofSeconds(rand));
            } else if (accessCache.getAccessCount() < maxCount) {
                //访问次数加1
                accessCache.setAccessCount(accessCache.getAccessCount() + 1);
                redisUtil.hSet(cacheKey,"accesslimit",accessCache);
            }else {
                //超出访问次数，判断时间是否超出设定时间
                Long count = System.currentTimeMillis() - accessCache.getFirstVisitTimestamp();
                if (count <= seconds * 1000) {
                    //如果还在设定时间内，则为不合法请求，返回错误信息
                    throw new AccessDeniedException("访问太频繁,距上次访问"+(count/1000)+"秒,只允许"+seconds+"秒访问"+(maxCount+1)+"次,请稍后再试!");
                } else {
                    //如果超出设定时间，则为合理的请求，将之前的请求清空，重新计数
                    accessCache.setFirstVisitTimestamp(System.currentTimeMillis());
                    accessCache.setAccessCount(1);
                    redisUtil.hSet(cacheKey,"accesslimit",JSON.toJSONString(accessCache));
                    redisUtil.expire(cacheKey, Duration.ofSeconds(rand));
                }
            }
        }
        return true;
    }

}
