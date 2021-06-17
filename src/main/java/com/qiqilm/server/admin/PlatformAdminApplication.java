package com.qiqilm.server.admin;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot应用类
 *
 * @author qicheng
 */
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class )
@ServletComponentScan
@EnableAspectJAutoProxy( exposeProxy = true )
@EnableAsync
@EnableScheduling
public class PlatformAdminApplication implements ApplicationContextAware {
	public static String activeProfile = "";

	public static void main( String[] args ) {
		SpringApplication.run( PlatformAdminApplication.class, args );
	}

	@Override
	public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
		activeProfile = applicationContext.getEnvironment().getActiveProfiles()[ 0 ];
	}
}

