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
		String jdbc = stringEncryptor.encrypt("jdbc:mysql://pc-j6ct6o187g7dis32y.rwlb.rds.aliyuncs"
				+ ".com:3306/77te_main?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly"
				+ "=false");
		System.out.println("main: " + jdbc);

		String jdbc2 = stringEncryptor.encrypt("jdbc:mysql://pc-j6ct6o187g7dis32y.rwlb.rds.aliyuncs"
				+ ".com:3306/77te_live?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly"
				+ "=false");
		System.out.println("live: " + jdbc2);

		String jdbc3 = stringEncryptor.encrypt("jdbc:mysql://pc-j6ct6o187g7dis32y.rwlb.rds.aliyuncs"
				+ ".com:3306/77te_lottery?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true"
				+ "&failOverReadOnly=false");
		System.out.println("lottery: " + jdbc3);

		String username = stringEncryptor.encrypt("r-j6cqmjlq5xexziok7j.redis.rds.aliyuncs.com");
		System.out.println("redisHost: " + username);

		String password = stringEncryptor.encrypt("sGDwkEKhU!z#a5%s");
		System.out.println("redisPass: " + password);
	}

	@org.junit.jupiter.api.Test
	public void test2() {
		String jdbc = stringEncryptor.decrypt("1mr9hspXjV0N/0JxFuNb7P1yHROzzQaOC/YRlg0dtLSIBZfTzrSHg7cv1P/2jIeWGqx3hQJe1meiHwo5m2oc5sss2WFUMvb3UkyRMn+CfG33Dhqw1m3iAK5uvkpEQ1xPiETNip2NQpHG/9+zPuVpwVed/tn30XNt6kpYAyRrjA0A8i+dPZqDl4Lv8FlYtPfWp6tFb43FA4bEbCCg44bhmubXVWX177hgh23wXQ7SVoDNXU/bqvNqC+jMtQlOi0D29NgmTwX/OybNMsjM915r6A==");
		System.out.println(jdbc);

//		String username = stringEncryptor.decrypt("jTdX7av9+6T6OSj6Uzijxc4gKm3AgoyysmQsscXqVgUTxRXYT4hrQXN0YRcDrwzg6MUp0dagLUDGGnZ4k0OvLQ==");
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
