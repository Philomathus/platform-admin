package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 在线会员列表对象 member_online
 *
 * @author 77tv
 * @date 2021-03-22
 */
@Data
public class MemberOnline extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    private String id;

    /** 最后活跃时间 */
    @Excel(name = "最后活跃时间")
    private Long onlineTime;

    private String userName;
    private String vip;
    private String nickName;
    private String loginIp;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date online;

    private Integer total;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userName", getUserName())
            .append("vip", getVip())
            .append("nickName", getNickName())
            .append("loginIp", getLoginIp())
            .append("onlineTime", getOnlineTime())
            .toString();
    }
}
