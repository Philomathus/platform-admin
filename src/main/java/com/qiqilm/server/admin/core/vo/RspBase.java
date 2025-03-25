package com.qiqilm.server.admin.core.vo;

import com.qiqilm.server.admin.exception.ErrCode;
import lombok.Data;

/**
 * <p>Title: RspBase</p>
 * <p>Description: Http操作结果对象</p>
 *
 * @author admin
 */
@Data
public class RspBase<T>{
    private int code;//ErrCode
    private String msg = "成功";
    private T data;

    public RspBase( T data) {
        this.data = data;
    }

    public RspBase msg( final String msg ) {
        this.msg = msg;
        return this;
    }

    public static RspBase ok() {
        return new RspBase();
    }


    public RspBase(){}
    /**
     * 业务异常提示
     * @param error
     * @return
     */
    public static RspBase  businessError( String error ) {
        RspBase res= new RspBase();
        res.code = ErrCode.BUSINESS_LOGIC_FAIL;
        res.msg = error;
        return res;
    }

    /**
     * 登录异常
     * @param error
     * @return
     */
    public static RspBase  sessionError( String error ) {
        RspBase res= new RspBase();
        res.code = ErrCode.SESSION_EXPIRE_FAIL;
        res.msg = error;
        return res;
    }


}
