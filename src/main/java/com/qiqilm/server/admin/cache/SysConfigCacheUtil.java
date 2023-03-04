package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.mapper.ConfigEnvironmentMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class SysConfigCacheUtil {
    public static final String SYS_CONFIG_KEY = "sys_config";

    @Autowired
    private RedisUtil               redisUtil;
    @Autowired
    private ConfigDomainCacheUtil   configDomainCacheUtil;
    @Autowired
    private ConfigEnvironmentMapper configEnvironmentMapper;

    public List<String> getConf( List<Object> codes ) {
        Boolean exists = redisUtil.exists( SYS_CONFIG_KEY );
        if ( exists == null || !exists ) {
            this.refreshConfCache();
        }
        List<Object> objects    = redisUtil.hMGet( SYS_CONFIG_KEY, codes );
        List<String> resultList = new ArrayList<>( objects.size() );
        for ( Object object : objects ) {
            String value = object != null ? configDomainCacheUtil.dynamicValue( object.toString() ) : null;
            resultList.add( value );
        }
        return resultList;
    }

    public String getConf( String code, String defaultValue ) {
        Boolean exists = redisUtil.exists( SYS_CONFIG_KEY );
        if ( exists == null || !exists ) {
            this.refreshConfCache();
        }
        Object value = redisUtil.hGet( SYS_CONFIG_KEY, code );
        return value != null ? configDomainCacheUtil.dynamicValue( value.toString() ) : defaultValue;
    }

    public String getConf( String code ) {
        return getConf( code, "" );
    }

    public BigDecimal getConfBd( String code ) {
        try {
            return new BigDecimal( getConf( code, "0" ) );
        } catch ( NumberFormatException e ) {
            return BigDecimal.ZERO;
        }
    }

    public int getConfInt( String code ) {
        try {
            return Integer.parseInt( getConf( code, "0" ) );
        } catch ( NumberFormatException e ) {
            return 0;
        }
    }

    public int getConfInt( String code, int defaultValue ) {
        try {
            return Integer.parseInt( getConf( code, defaultValue + "" ) );
        } catch ( NumberFormatException e ) {
            return 0;
        }
    }

    public boolean getConfBool( String code ) {
        return getConfInt( code ) > 0;
    }


    public void refreshConfCache() {
        ConfigEnvironment query = new ConfigEnvironment();
        query.setEnvStatus( 1 );
        List<ConfigEnvironment> configEnvironments = configEnvironmentMapper.selectConfigEnvironmentList( query );

        Map<Object, Object> map = configEnvironments.stream().collect( Collectors.toMap( ConfigEnvironment::getEnvCode, ( env ) ->
                env.getEnvValue() == null ? "" : env.getEnvValue() ) );
        redisUtil.unlink( SYS_CONFIG_KEY );
        redisUtil.hMSet( SYS_CONFIG_KEY, map );
    }

    public void setConfCache( ConfigEnvironment configEnvironment ) {
        redisUtil.hSet( SYS_CONFIG_KEY, configEnvironment.getEnvCode(), configEnvironment.getEnvValue() );
    }

    public void deleteCache( String... envCodes ) {
        redisUtil.hDelete( SYS_CONFIG_KEY, envCodes );
    }
}
