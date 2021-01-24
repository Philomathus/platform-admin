package com.qiqilm.server.admin.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author 77tv
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface DataScope {
	/**
	 * 用户表的别名
	 */
	public String userAlias() default "";
}
