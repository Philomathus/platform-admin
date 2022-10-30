package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.domain.LiveMsgEngage;
import com.qiqilm.server.admin.domain.WheelLottery;
import com.qiqilm.server.admin.domain.vo.H5PluginVo;
import com.qiqilm.server.admin.mapper.LiveMountMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class LiveCacheUtil {
    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private LiveMountMapper liveMountMapper;

    public void setRedis( String key, String identify, Object value ) {
        String valStr;
        if ( value instanceof String ) {
            valStr = ( String ) value;
        } else {
            valStr = JsonUtil.object2Json( value );
        }
        redisUtil.strSet( Constants.LIVE_PREX + key + ":" + identify, valStr );
    }

    public void setRedis( String key, String identify, Object value, int seconds ) {
        String valStr;
        if ( value instanceof String ) {
            valStr = ( String ) value;
        } else {
            valStr = JsonUtil.object2Json( value );
        }
        redisUtil.strSet( Constants.LIVE_PREX + key + ":" + identify, valStr, Duration.ofSeconds( seconds ) );
    }

    public String getRedis( String key, String identify ) {
        return redisUtil.strGet( Constants.LIVE_PREX + key + ":" + identify );
    }

    public void removeRedis( String key, String identify ) {
        redisUtil.unlink( Constants.LIVE_PREX + key + ":" + identify );
    }

    public void setLock( String key, String identify, int seconds ) {
        setRedis( key + ":lock", identify, "lock", seconds );
    }

    public void setLock( String key, String identify ) {
        setRedis( key + ":lock", identify, "lock" );
    }

    public boolean isLock( String key, String identify ) {
        String redis = getRedis( key + ":lock", identify );
        return Strings.isNotBlank( redis );
    }

    public synchronized boolean checkAndSetLock( String key, String identify ) {
        boolean lock = isLock( key, identify );
        if ( !lock ) {
            setLock( key, identify );
        }
        return lock;
    }

    public synchronized boolean checkAndSetLock( String key, String identify, int seconds ) {
        boolean lock = isLock( key, identify );
        if ( !lock ) {
            setLock( key, identify, seconds );
        }
        return lock;
    }

    /**
     * 添加管理员签名
     */
    public void addAdminSign( String adminKey, String sign ) {
        String signKey = Constants.ADMIN_SIGN + adminKey;
        redisUtil.strSet( signKey, sign, Duration.ofDays( 300 ) );
    }

    public void delAdminSign( String adminKey ) {
        redisUtil.unlink( Constants.ADMIN_SIGN + adminKey );
    }

    /**
     * 获取管理员签名
     */
    public String getAdminSign( String adminKey ) {
        return redisUtil.strGet( Constants.ADMIN_SIGN + adminKey );
    }

    public void refreshMountConfCache() {
        redisUtil.unlink( Constants.LIVE_MOUNT );
        Map<String, String> map       = new HashMap<>();
        LiveMount           liveMount = new LiveMount();
        liveMount.setStatus( "1" );
        for ( LiveMount mount : liveMountMapper.selectLiveMountList( liveMount ) ) {
            map.put( String.valueOf( mount.getId() ), mount.getSvgUrl() );
        }
        redisUtil.hMSet( Constants.LIVE_MOUNT, map );
    }


    public void setWheelLottery( WheelLottery wheelLottery ) {
        setRedis( "wheel-lottery", String.valueOf( wheelLottery.getId() ), wheelLottery );
    }

    public void delWheelLotteryUseKey( int lotteryId ) {
        redisUtil.unlink( getWheelLotteryUseKey( lotteryId ) );
    }

    private String getWheelLotteryUseKey( int lotteryId ) {
        return "live:lottery_wheel_pool:" + lotteryId;
    }

    public void setH5PluginVo( H5PluginVo vo ) {
        setRedis( "h5-plugin", String.valueOf( vo.getType() ), vo );
    }

    public void setLiveMsgEngage( List liveMsgEngageList ) {
        if ( liveMsgEngageList.isEmpty() ) {
            redisUtil.unlink( "live:live-msg-engage" );
        }
        redisUtil.strSet( "live:live-msg-engage", JsonUtil.object2Json( liveMsgEngageList ) );
    }

    public List<LiveMsgEngage> getLiveMsgEngage() {
        String value = redisUtil.strGet( "live:live-msg-engage" );
        return StringUtils.isNotBlank( value ) ? JsonUtil.json2Array( value, new TypeReference<List<LiveMsgEngage>>() {} ) : null;
    }

    public void delHostToken( Long userId ) {
        String hostTokenKey = Constants.HOST_TOKEN + userId;

        String token = redisUtil.strGet( hostTokenKey );
        if ( StringUtils.isNotBlank( token ) ) {
            redisUtil.unlink( Constants.TOKEN_HOST + token );
            redisUtil.unlink( hostTokenKey );
        }
    }
}
