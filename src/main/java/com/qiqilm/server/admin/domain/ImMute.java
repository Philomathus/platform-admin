package com.qiqilm.server.admin.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 腾讯IM禁言查询对象 live_user_bank
 *
 * @author 77tv
 * @date 2021-04-27
 */
@Data
public class ImMute{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String userId;
    private String nickName;
    private String hostNickName;
    private String muteRemark;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("nickName", getNickName())
                .append("hostNickName", getHostNickName())
                .toString();
    }
}
