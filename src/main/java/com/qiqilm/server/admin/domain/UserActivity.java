package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

@Data
public class UserActivity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 用户id */
    @Excel(name = "用户id")
    private String id;

    /** 参加的活动 */
    @Excel(name = "参加的活动")
    private String msg;

    /**   状态 0 未处理  1已处理 3不符合*/
    @Excel(name = "状态")
    private int status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "修改时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    /** 操作人员 */
    @Excel( name = "操作人员" )
    private String updateBy;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("msg", getMsg())
                .append("status", getStatus())
                .toString();
    }

}
