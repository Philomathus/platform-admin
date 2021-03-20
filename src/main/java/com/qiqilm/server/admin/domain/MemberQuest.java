package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 member_quest
 *
 * @author 77tv
 * @date 2021-03-20
 */
@Data
public class MemberQuest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员id */
    @Excel(name = "会员id")
    private String memberId;

    /** 任务id */
    @Excel(name = "任务id")
    private String questId;

    /** 0=进行中1=已经完成2 领奖完成 */
    @Excel(name = "0=进行中1=已经完成2 领奖完成")
    private Long status;

    /** 当前任务数量 */
    @Excel(name = "当前任务数量")
    private Long curnum;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("questId", getQuestId())
            .append("status", getStatus())
            .append("curnum", getCurnum())
            .toString();
    }
}