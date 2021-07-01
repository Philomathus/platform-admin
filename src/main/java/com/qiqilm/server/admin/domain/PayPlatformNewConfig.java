package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 【请填写功能名称】对象 pay_platform_config
 *
 * @author 77tv
 * @date 2021-06-30
 */
@Data
public class PayPlatformNewConfig {
    /** 支付平台名称 */
    @Excel(name = "支付平台名称")
    private String payPlatfrom;

    /** 支付平台编码 */
    @Excel(name = "支付平台编码")
    private String payCode;

    /** 下单商户ID参数名称 */
    @Excel(name = "下单商户ID参数名称")
    private String payMerid;

    /** 支付通道参数名称 */
    @Excel(name = "支付通道参数名称")
    private String payMethod;

    /** 下单订单号参数名称 */
    @Excel(name = "下单订单号参数名称")
    private String payOrderno;

    /** 下单金额参数名称,以逗号分隔,后面0为元,1为分 */
    @Excel(name = "下单金额参数名称,以逗号分隔,后面0为元,1为分")
    private String payMoney;

    /** 回调地址参数名称 */
    @Excel(name = "回调地址参数名称")
    private String payCallbackurl;

    /** 下单成功跳转地址名称 */
    @Excel(name = "下单成功跳转地址名称")
    private String payReturnurl;

    /** 下单IP参数名称 */
    @Excel(name = "下单IP参数名称")
    private String payIp;

    /** 下单应用ID参数名称 */
    @Excel(name = "下单应用ID参数名称")
    private String payAppid;

    /** 下单时间戳参数名称,以逗号分隔,后面0为秒,1为毫秒 */
    @Excel(name = "下单时间戳参数名称,以逗号分隔,后面0为秒,1为毫秒")
    private String payTimesecond;

    /** 下单时间格式参数，以逗号分隔,后面 0为yyyy-mm-dd HH:mm:ss, 1为yyyyMMddHHmmss */
    @Excel(name = "下单时间格式参数，以逗号分隔,后面 0为yyyy-mm-dd HH:mm:ss, 1为yyyyMMddHHmmss")
    private String payTime;

    /** 下单UUID任意值参数名称 */
    @Excel(name = "下单UUID任意值参数名称")
    private String payUuidname;

    /** 下单额外参数param1 */
    @Excel(name = "下单额外参数param1")
    private String payParam1;

    /** 下单额外参数param2 */
    @Excel(name = "下单额外参数param2")
    private String payParam2;

    /** 下单额外参数param3 */
    @Excel(name = "下单额外参数param3")
    private String payParam3;

    /** 下单固定值额外参数param4,以逗号分割 */
    @Excel(name = "下单固定值额外参数param4,以逗号分割")
    private String payParam4;

    /** 下单固定值额外参数param5,以逗号分割 */
    @Excel(name = "下单固定值额外参数param5,以逗号分割")
    private String payParam5;

    /** 下单签名参数名称 */
    @Excel(name = "下单签名参数名称")
    private String paySign;

    /** 下单加密排序方式 0为ASCII码排序 */
    @Excel(name = "下单加密排序方式 0为ASCII码排序")
    private Integer paySort;

    /** 下单拼接密钥方式   0为&key= 1为key= 2为直接拼接 */
    @Excel(name = "下单拼接密钥方式   0为&key= 1为key= 2为直接拼接")
    private Integer paySplice;

    /** 下单MD5加密转大小写，0转大写，1转小写 */
    @Excel(name = "下单MD5加密转大小写，0转大写，1转小写")
    private Integer payCase;

    /** 下单请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get */
    @Excel(name = "下单请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get")
    private Integer payHttpmethod;

    /** 下单返回值类型 0为String 1为Map */
    @Excel(name = "下单返回值类型 0为String 1为Map")
    private Integer payReturntype;

    /** 下单返回状态判断参数名称,以逗号分隔，后面是成功下单的值 */
    @Excel(name = "下单返回状态判断参数名称,以逗号分隔，后面是成功下单的值")
    private String payStatus;

    /** 下单返回嵌套map的参数名称，如没有为空 */
    @Excel(name = "下单返回嵌套map的参数名称，如没有为空")
    private String payMapname;

    /** 返回链接参数名称 */
    @Excel(name = "返回链接参数名称")
    private String payUrl;

    /** 返回失败原因参数名称 */
    @Excel(name = "返回失败原因参数名称")
    private String payFailreason;

    /** 回调商户订单号参数名称 */
    @Excel(name = "回调商户订单号参数名称")
    private String callbackOrderno;

    /** 回调三方平台订单号参数名称 */
    @Excel(name = "回调三方平台订单号参数名称")
    private String callbackSanorderno;

    /** 回调去除参数param1名称 */
    @Excel(name = "回调去除参数param1名称")
    private String callbackParam1;

    /** 回调去除参数param2名称 */
    @Excel(name = "回调去除参数param2名称")
    private String callbackParam2;

    /** 回调请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get */
    @Excel(name = "回调请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get")
    private Integer callbackMethod;

    /** 回调加密排序方式 0为ASCII码排序 */
    @Excel(name = "回调加密排序方式 0为ASCII码排序")
    private Integer callbackSort;

    /** 回调拼接密钥方式   0为&key=  1为key=  2为直接拼接 */
    @Excel(name = "回调拼接密钥方式   0为&key=  1为key=  2为直接拼接")
    private Integer callbackSplice;

    /** 回调状态判断参数名称，以逗号分隔，后面值为成功回调的值 */
    @Excel(name = "回调状态判断参数名称，以逗号分隔，后面值为成功回调的值")
    private String callbackStatus;

    /** 回调金额参数名称，以逗号分隔，后面0为元，1为分  */
    @Excel(name = "回调金额参数名称，以逗号分隔，后面0为元，1为分 ")
    private String callbackMoney;

    /** 回调成功返回参数名称 */
    @Excel(name = "回调成功返回参数名称")
    private String callbackSuccess;

    /** 回调签名参数名称 */
    @Excel(name = "回调签名参数名称")
    private String callbackSign;

    /** 查询商户ID参数名称 */
    @Excel(name = "查询商户ID参数名称")
    private String queryMerid;

    /** 查询订单号参数名称 */
    @Excel(name = "查询订单号参数名称")
    private String queryOrderno;

    /** 查询时间戳参数名称,后面0为秒，1为毫秒 */
    @Excel(name = "查询时间戳参数名称,后面0为秒，1为毫秒")
    private String queryTimesecond;

    /** 查询时间格式参数，以逗号分隔,后面 0为yyyy-mm-dd HH:mm:ss, 1为yyyyMMddHHmmss */
    @Excel(name = "查询时间格式参数，以逗号分隔,后面 0为yyyy-mm-dd HH:mm:ss, 1为yyyyMMddHHmmss")
    private String queryTime;

    /** 查询UUID任意值参数名称 */
    @Excel(name = "查询UUID任意值参数名称")
    private String queryUuidname;

    /** 查询应用ID参数名称 */
    @Excel(name = "查询应用ID参数名称")
    private String queryAppid;

    /** 固定查询额外参数param1，以逗号分隔 */
    @Excel(name = "固定查询额外参数param1，以逗号分隔")
    private String queryParam1;

    /** 查询签名参数名称 */
    @Excel(name = "查询签名参数名称")
    private String querySign;

    /** 查询加密排序方式 0为ASCII码排序 */
    @Excel(name = "查询加密排序方式 0为ASCII码排序")
    private Integer querySort;

    /** 查询拼接密钥方式   0为&key= 1为key= 2为直接拼接 */
    @Excel(name = "查询拼接密钥方式   0为&key= 1为key= 2为直接拼接")
    private Integer querySplice;

    /** 查询MD5加密转大小写，0转大写，1转小写 */
    @Excel(name = "查询MD5加密转大小写，0转大写，1转小写")
    private Integer queryCase;

    /** 查询请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get */
    @Excel(name = "查询请求方式  0为FORM_URLENCODED   1为FROM_DATA  2为JSON  3为get")
    private Integer queryHttpmethod;

    /** 查询返回值类型 0为String 1为Map */
    @Excel(name = "查询返回值类型 0为String 1为Map")
    private Integer queryReturntype;

    /** 查询返回状态判断参数名称，以逗号分隔，后面值为成功查询的值 */
    @Excel(name = "查询返回状态判断参数名称，以逗号分隔，后面值为成功查询的值")
    private String queryStatus;

    /** 查询返回嵌套map的参数名称，如没有为空 */
    @Excel(name = "查询返回嵌套map的参数名称，如没有为空")
    private String queryMapname;

    /** 查询返回金额参数名称，以逗号分隔，后面0为元，1为分 */
    @Excel(name = "查询返回金额参数名称，以逗号分隔，后面0为元，1为分")
    private String queryMoney;

    /** 查询返回平台订单号参数名称 */
    @Excel(name = "查询返回平台订单号参数名称")
    private String querySanorderno;
}
