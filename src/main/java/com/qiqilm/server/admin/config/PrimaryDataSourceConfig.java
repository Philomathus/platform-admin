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
@MapperScan( basePackages = "com.qiqilm.server.admin.mapper", sqlSessionFactoryRef = "PrimarySqlSessionFactory" )
public class PrimaryDataSourceConfig {

	@Bean( name = "PrimaryDataSource" )
	@Primary
	@ConfigurationProperties( prefix = "spring.datasource.primary" )
	public DataSource getPrimaryDateSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean( name = "PrimaryTransactionManager" )
	@Primary
	public DataSourceTransactionManager transactionManager( @Qualifier( "PrimaryDataSource" ) DataSource datasource ) {
		return new DataSourceTransactionManager( datasource );
	}

	@Bean( name = "PrimarySqlSessionFactory" )
	@Primary
	public SqlSessionFactory primarySqlSessionFactory( @Qualifier( "PrimaryDataSource" ) DataSource datasource,
													   MybatisProperties mybatisProperties ) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource( datasource );
		bean.setMapperLocations( new PathMatchingResourcePatternResolver().getResources( "classpath*:mapper/*.xml" ) );
		bean.setTypeAliasesPackage( "com.qiqilm.server.admin.domain" );
		bean.setConfiguration( mybatisProperties.getConfiguration() );
		return bean.getObject();
	}

	@Bean( "PrimarySqlSessionTemplate" )
	@Primary
	public SqlSessionTemplate primarySqlSessionTemplate(
			@Qualifier( "PrimarySqlSessionFactory" ) SqlSessionFactory sessionfactory ) {
		return new SqlSessionTemplate( sessionfactory );
	}

}
