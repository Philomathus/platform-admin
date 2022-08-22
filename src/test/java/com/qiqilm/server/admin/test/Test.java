package com.qiqilm.server.admin.test;

import com.qiqilm.server.admin.PlatformAdminApplication;
import com.qiqilm.server.admin.config.LiveCenterConfig;
import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Log4j2
@SpringBootTest( classes = { PlatformAdminApplication.class } )// 指定启动类
@ActiveProfiles( "7700" )
public class Test {

	@Autowired
	private StringEncryptor stringEncryptor;

	@org.junit.jupiter.api.Test
	public void test1() {
		String jdbc = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77xy_main?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println("main: " + jdbc);

		String jdbc2 = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77xy_live?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println("live: " + jdbc2);

		String jdbc3 = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77xy_lottery?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println("lottery: " + jdbc3);

		String username = stringEncryptor.encrypt("r-j6cgbsd4ajjojv0bj8.redis.rds.aliyuncs.com");
		System.out.println("redisHost: " + username);

		String password = stringEncryptor.encrypt("!J!2M**9lQqjv!%R");
		System.out.println("redisPass: " + password);
	}

	@org.junit.jupiter.api.Test
	public void test2() {
		String jdbc = stringEncryptor.decrypt("v1g9jT15U5Rj/Y+/o4zJNuXtRcEnyuTPHR1gnUvYUYzZltQRmjrpNVQYdch3E4K7A6JKvRNU1FFqmRQ7dEPHfw==");
		System.out.println(jdbc);

//		String username = stringEncryptor.decrypt("ntsELfEvY1lBOmFyTcgga5DF5OyuB9qA5FrxnMQdlwe14iRRN7BZPddMBZ9oDB+W");
//		System.out.println(username);
//
//		String password = stringEncryptor.decrypt("FNO20k6i7eNncA5GwwfAkYUhkNXMPlh/VGkMNBoADnG6lPbL6ebkqsR4xJdfM6kHM/0P8R1dZs2+LpMaUWj4Cw==");
//		System.out.println(password);
	}

	@org.junit.jupiter.api.Test
	public void test3() {
		System.out.println(LiveCenterConfig.me.getLiveSubAgents());
	}
}
