package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RspMemberChannel {

    @Excel(name = "登录备注信息")
    private String email;

    @Excel(name = "数量")
    private Integer number;

    @Excel(name = "渠道号")
    private String inviterCode;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date regTime;

    @Excel(name = "0：游客，1：会员")
    private String channelCode;

    /** 状态(0= 禁用 1=正常 2=测试号3=超管号) */
    @Excel(name = "状态")
    private String status;

}
