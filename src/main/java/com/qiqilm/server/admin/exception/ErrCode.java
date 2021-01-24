package com.qiqilm.server.admin.exception;

/** 错误码说明
 * Created by admin on 2017/12/12.
 */
public interface ErrCode {

    //***********业务错误码**************//
    //session 成功
    Integer SUCCESS = 0;
    //session 异常
    Integer SESSION_EXPIRE_FAIL = 401;

    //业务逻辑错误
    Integer BUSINESS_LOGIC_FAIL = 500;


}
