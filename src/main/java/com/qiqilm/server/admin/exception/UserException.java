package com.qiqilm.server.admin.exception;

/**
 * 用户信息异常类
 *
 * @author 77tv
 */
public class UserException extends BaseException {
	private static final long serialVersionUID = 1L;

	public UserException( String code, Object[] args ) {
		super( "user", code, args, null );
	}
}
