package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 家族申请对象 live_family_join
 *
 * @author 77tv
 * @date 2021-06-09
 */
@Data
public class LiveFamilyJoin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 家族ID */
    @Excel(name = "家族ID")
    private Long familyId;

    /** 会员ID */
    @Excel(name = "会员ID")
    private Long userId;

    /** 审核状态；0待审核、1通过审核、2 拒绝通过、3已退出家族 */
    @Excel(name = "审核状态；0待审核、1通过审核、2 拒绝通过、3已退出家族")
    private Long status;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 主播昵称 */
    @Excel(name = "主播昵称")
    private String nickName;

    /** 头像 */
    @Excel(name = "头像")
    private String headImage;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("familyId", getFamilyId())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .append("status", getStatus())
            .append("memo", getMemo())
            .append("nickName", getNickName())
            .append("headImage", getHeadImage())
            .toString();
    }
}
