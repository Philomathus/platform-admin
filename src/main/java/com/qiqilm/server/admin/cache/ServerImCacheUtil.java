package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.ServerIm;
import com.qiqilm.server.admin.mapper.ServerImMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ServerImCacheUtil {
	public static final String SERVER_IM = Constants.CONFIG_PREX + "serverIm:effect";

	@Autowired
	private RedisUtil      redisUtil;
	@Autowired
	private ServerImMapper serverImMapper;

	public List<String> getValue( List<Object> codes ) {
		this.exists();
		List<Object> objects    = redisUtil.hMGet( SERVER_IM, codes );
		List<String> resultList = new ArrayList<>( objects.size() );
		for ( Object object : objects ) {
			String value = object != null ? object.toString() : null;
			resultList.add( value );
		}
		return resultList;
	}

	public String getValue( String code ) {
		this.exists();
		Object codeValue = redisUtil.hGet( SERVER_IM, code );
		return codeValue == null ? "" : codeValue.toString();
	}

	private void exists() {
		if ( !redisUtil.exists( SERVER_IM ) ) {
			List<ServerIm> serverImList = serverImMapper.selectServerImByEffect();
			if ( serverImList.isEmpty() ) {
				return;
			}
			ServerIm serverIm = serverImList.get( 0 );
			if ( serverIm != null ) {
				this.setServerIm( serverIm );
			}
		}
	}

	public void setServerIm( ServerIm serverIm ) {
		Map<String, String> serverImMap = new HashMap<>();
		for ( String code : serverIm.toCodes() ) {
			serverImMap.put( code, serverIm.getVal( code ) );
		}
		redisUtil.unlink( SERVER_IM );
		redisUtil.hMSet( SERVER_IM, serverImMap );
	}
}
