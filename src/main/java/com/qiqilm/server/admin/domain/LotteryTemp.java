package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 彩票即时信息对象 lottery_temp
 *
 * @author 77tv
 * @date 2021-02-23
 */
@Data
public class LotteryTemp extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 彩种id */
    private Long id;


    /** 当前期数 */
    @Excel(name = "当前期数")
    private String issue;


    /** 开奖时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开奖时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date ktime;


    /** 上期期号 */
    @Excel(name = "上期期号")
    private String issueJust;


    /** 上期开奖 */
    @Excel(name = "上期开奖")
    private String codeJust;


    /** 0=开启中1=封盘中 */
    @Excel(name = "0=开启中1=封盘中")
    private Long su;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("issue", getIssue())
            .append("ktime", getKtime())
            .append("issueJust", getIssueJust())
            .append("codeJust", getCodeJust())
            .append("su", getSu())
            .toString();
    }
}
