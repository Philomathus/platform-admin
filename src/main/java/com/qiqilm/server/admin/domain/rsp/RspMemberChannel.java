package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class RspMemberChannel {

    @Excel(name = "网易异常备注",width = 60,orderNum = "2")
    private String email;

    @Excel(name = "数量",width = 10,orderNum = "1")
    private Integer number;

    @Excel(name = "邀请码",width = 10,orderNum = "3")
    private String inviterCode;

   /* *//** 注册时间 *//*
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date regTime;*/

    @Excel(name = "0：游客，1：会员",width = 20,orderNum = "4")
    private String channelcode;

    /** 状态(0= 禁用 1=正常 2=测试号3=超管号) */
    /*@Excel(name = "状态")
    private String status;*/

}
