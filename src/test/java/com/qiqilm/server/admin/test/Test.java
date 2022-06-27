package com.qiqilm.server.admin.test;

import com.qiqilm.server.admin.PlatformAdminApplication;
import com.qiqilm.server.admin.service.IPayAgentService;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricStringEncryptor;
import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
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
		String jdbc = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77jp_main?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println(jdbc);

		String jdbc2 = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77jp_live?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println("live:" + jdbc2);

		String jdbc3 = stringEncryptor.encrypt("jdbc:mysql://pc-j6c1ah19tg2g51r3u.rwlb.rds.aliyuncs.com:3306/77jp_lottery?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
		System.out.println("lottery:" + jdbc3);

		String username = stringEncryptor.encrypt("r-j6cjt35tpd7v92rya6.redis.rds.aliyuncs.com");
		System.out.println(username);

		String password = stringEncryptor.encrypt("yL#Drba&R6!XDTMm");
		System.out.println(password);
	}

	@org.junit.jupiter.api.Test
	public void test2() {
		String jdbc = stringEncryptor.decrypt("6rDd6DG59esK4w7l7gXnnsSr6ARLR6v3Mi+sG5Jq16QiuuO5M7ts0fjQBdDruqyzJaRNGjEqNgQS3TWW9HYA6S3iG/beLr4ObtUICwfmQhn7GlxyLYd9eSn/jul1kL36urnigNx4T2m7yYru6toDC1EPzto77PvVdiM+b63ETQ3sNuPGtyuHz/JrSEqmWqh6PkqpqdYFbvKiAdUBarIq6rombPveCxbmrVbxKRGcuXO0mVAUH17g2ePAZKBRK+oV+34XyE+thWsYYqyoms4pwQ==");
		System.out.println(jdbc);

//		String username = stringEncryptor.decrypt("ntsELfEvY1lBOmFyTcgga5DF5OyuB9qA5FrxnMQdlwe14iRRN7BZPddMBZ9oDB+W");
//		System.out.println(username);
//
//		String password = stringEncryptor.decrypt("FNO20k6i7eNncA5GwwfAkYUhkNXMPlh/VGkMNBoADnG6lPbL6ebkqsR4xJdfM6kHM/0P8R1dZs2+LpMaUWj4Cw==");
//		System.out.println(password);
	}

	public static void main(String[] args) {

	}
}
