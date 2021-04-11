package com.qiqilm.server.admin.test;

import com.qiqilm.server.admin.PlatformAdminApplication;
import com.qiqilm.server.admin.service.IPayAgentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Log4j2
@SpringBootTest( classes = { PlatformAdminApplication.class } )// 指定启动类
@ActiveProfiles( "7700" )
public class Test {
	@Autowired
	private IPayAgentService payAgentService;

	@org.junit.jupiter.api.Test
	public void test() {
		payAgentService.queryAgent4Status5Min();
	}
}
