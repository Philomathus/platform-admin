package com.qiqilm.server.admin.domain;


import lombok.Data;
import lombok.experimental.Accessors;


import java.util.Date;


@Accessors( chain = true )
@Data
public class SmsFailLog {

	private Integer id;

	/**
	 * 错误码
	 */

	private String code;
	/**
	 * 错误消息
	 */


	private String message;

	/**
	 * 手机号
	 */

	private String phone;


	private String smsName;

	private Date createTime;

	private String smsSubname;
	private static final long serialVersionUID = 1L;

}