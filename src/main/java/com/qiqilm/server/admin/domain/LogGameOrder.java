package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员上下分对象 log_game_order
 *
 * @author 77tv
 * @date 2021-01-29
 */
public class LogGameOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 本地ID */
    private String id;

    /** 1上分2下分 */
    @Excel(name = "1上分2下分")
    private Long type;

    /** 玩家ID */
    @Excel(name = "玩家ID")
    private String memberId;

    /** 账号 */
    @Excel(name = "账号")
    private String userName;

    /** 本地平台id */
    @Excel(name = "本地平台id")
    private Long platformId;

    /** 0开始1失败2成功3异常 */
    @Excel(name = "0开始1失败2成功3异常")
    private Long status;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal money;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date eTime;
    /**
     * 选择日期
     */
    private String[] selectDate;

    private String startTime;
    private String endTime;

    public String[] getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String[] selectDate) {
        this.selectDate = selectDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getPlatformId() {
        return platformId;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setbTime(Date bTime) {
        this.bTime = bTime;
    }

    public Date getbTime() {
        return bTime;
    }
    public void seteTime(Date eTime) {
        this.eTime = eTime;
    }

    public Date geteTime() {
        return eTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("type", getType())
            .append("memberId", getMemberId())
            .append("userName", getUserName())
            .append("platformId", getPlatformId())
            .append("status", getStatus())
            .append("money", getMoney())
            .append("bTime", getbTime())
            .append("eTime", geteTime())
            .toString();
    }
}
