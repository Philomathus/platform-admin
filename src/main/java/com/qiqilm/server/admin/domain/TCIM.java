package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 腾讯IM对象 live_user_bank
 *
 * @author 77tv
 * @date 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TCIM extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 会员编号 */
    @Excel(name = "群组id")
    private String groupId;

    /** 真实姓名 */
    @Excel(name = "发送人id")
    private Integer msgSeq;

    /** 真实姓名 */
    @Excel(name = "消息个数")
    private Integer size;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("groupId", getGroupId())
            .append("msgSeq", getMsgSeq())
            .append("size", getSize())
            .toString();
    }
}
