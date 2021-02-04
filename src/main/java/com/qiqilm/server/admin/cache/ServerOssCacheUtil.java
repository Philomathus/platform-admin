package com.qiqilm.server.admin.cache;

import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qicheng
 */
@Log4j2
@Component
public class ServerOssCacheUtil {
	public static final String SERVER_OSS = Constants.CONFIG_PREX + "serverOss:effect";

	@Autowired
	private RedisUtil redisUtil;

}
