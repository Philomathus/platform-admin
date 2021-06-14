package com.qiqilm.server.admin.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan( basePackages = "com.qiqilm.server.admin.mapper.data", sqlSessionFactoryRef = "SecondarySqlSessionFactory" )
public class SecondaryDataSourceConfig {

	@Bean( name = "SecondaryDataSource" )
	@ConfigurationProperties( prefix = "spring.datasource.secondary" )
	public DataSource getPrimaryDateSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean( name = "SecondaryTransactionManager" )
	@Primary
	public DataSourceTransactionManager transactionManager( @Qualifier( "SecondaryDataSource" ) DataSource datasource ) {
		return new DataSourceTransactionManager( datasource );
	}

	@Bean( name = "SecondarySqlSessionFactory" )
	public SqlSessionFactory secondarySqlSessionFactory( @Qualifier( "SecondaryDataSource" ) DataSource datasource,
														 MybatisProperties mybatisProperties ) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource( datasource );
		bean.setMapperLocations( new PathMatchingResourcePatternResolver().getResources( "classpath*:mapper/data/*.xml" ) );
		bean.setTypeAliasesPackage( "com.qiqilm.server.admin.domain" );
		bean.setConfiguration( mybatisProperties.getConfiguration() );
		return bean.getObject();
	}

	@Bean( "SecondarySqlSessionTemplate" )
	public SqlSessionTemplate secondarySqlSessionTemplate(
			@Qualifier( "SecondarySqlSessionFactory" ) SqlSessionFactory sessionfactory ) {
		return new SqlSessionTemplate( sessionfactory );
	}
}