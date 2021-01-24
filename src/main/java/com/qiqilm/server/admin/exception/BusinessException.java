package com.qiqilm.server.admin.exception;

/** 业务异常
 * Created by admin on 2018/1/12.
 */
public class BusinessException extends RuntimeException {

    public BusinessException( String errCode){
        super(errCode);
    }

    public BusinessException( String errCode, Throwable cause){
        super(errCode,cause);
    }

    public BusinessException( Throwable cause){
        super(cause);
    }

//    @Override
//    public StackTraceElement[] getStackTrace() {
//        return new StackTraceElement[]{};
//    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
