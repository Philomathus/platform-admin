package com.qiqilm.server.admin.config;

import com.qiqilm.server.admin.interceptor.AccessLimitInterceptor;
import com.qiqilm.server.admin.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfiguration implements WebMvcConfigurer {
	@Autowired
	private RepeatSubmitInterceptor repeatSubmitInterceptor;
	@Autowired
	private AccessLimitInterceptor accessLimitInterceptor;

	@Override
	public void addResourceHandlers( ResourceHandlerRegistry registry ) {
		registry.addResourceHandler( "/webjars/**" )
				.addResourceLocations( "classpath:/META-INF/resources/webjars/" );
	}

	/**
	 * 自定义拦截规则
	 */
	@Override
	public void addInterceptors( InterceptorRegistry registry ) {
		registry.addInterceptor( repeatSubmitInterceptor ).addPathPatterns( "/**" );
		registry.addInterceptor( accessLimitInterceptor ).addPathPatterns("/member/memberGameData/**");
	}

	/**
	 * 跨域配置
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration               config = new CorsConfiguration();
		//config.setAllowCredentials( true );
		// 设置访问源地址
		config.addAllowedOrigin( "*" );
		// 设置访问源请求头
		config.addAllowedHeader( "*" );
		// 设置访问源请求方法
		config.addAllowedMethod( "*" );
		// 对接口配置跨域设置
		source.registerCorsConfiguration( "/**", config );
		return new CorsFilter( source );
	}
}
