package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代充人欢迎语配置对象 chat_welcome_config
 *
 * @author 77tv
 * @date 2021-09-24
 */
@Data
public class ChatWelcomeConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 代充人id */
    @Excel(name = "代充人id")
    private Long agentId;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 操作时间 */
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date operatorTime;

    /** 排序 */
    @Excel(name = "排序")
    private Long sort;

    private String nickName;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("agentId", getAgentId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("operator", getOperator())
            .append("operatorTime", getOperatorTime())
            .append("sort", getSort())
            .toString();
    }
}
