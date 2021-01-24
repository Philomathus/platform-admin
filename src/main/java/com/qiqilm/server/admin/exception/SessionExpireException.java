package com.qiqilm.server.admin.exception;

/** session无效异常
 * Created by admin on 2018/1/12.
 */
public class SessionExpireException extends RuntimeException {

    public SessionExpireException( String errCode){
        super(errCode);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
