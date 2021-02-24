package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 speak_ip_black_list
 *
 * @author 77tv
 * @date 2021-02-22
 */
@Data
public class SpeakIpBlackList extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    private Integer id;
    private String userId;
    private String userIp;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("userIp", getUserIp())
            .toString();
    }
}