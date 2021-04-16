package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户送礼日志对象 live_proplog
 *
 * @author 77tv
 * @date 2021-01-29
 */
public class LiveProplog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 送礼平台用户ID */
    @Excel(name = "送礼平台用户ID")
    private String pUserId;

    /** 送礼平台用户登录名 */
    @Excel(name = "送礼平台用户登录名")
    private String pUserName;

    /** 礼物id */
    @Excel(name = "礼物id")
    private Long propId;

    /** 道具名 */
    @Excel(name = "道具名")
    private String propName;

    /** 钻石（from_user_id减少的钻石）合计 */
    @Excel(name = "钻石", suffix = "f=rom_user_id减少的钻石")
    private BigDecimal totalDiamonds;

    /** 印票(to_user_id增加的印票）合计;is_red_envelope=1时,为主播获得的：钻石 数量 */
    @Excel(name = "印票(to_user_id增加的印票）合计;is_red_envelope=1时,为主播获得的：钻石 数量")
    private BigDecimal totalTicket;

    /** 平台用户当前余额 */
    @Excel(name = "平台用户当前余额")
    private BigDecimal currentDiamonds;

    /** 收 */
    @Excel(name = "收")
    private Long toUserId;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createtime;

    /**
     * 选择日期
     */
    private String[] selectDate;

    private String startTime;
    private String endTime;
    private BigDecimal totalPorp;

    public BigDecimal getTotalPorp() {
        return totalPorp;
    }

    public void setTotalPorp(BigDecimal totalPorp) {
        this.totalPorp = totalPorp;
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

    public String[] getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(String[] selectDate) {
        this.selectDate = selectDate;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getpUserId() {
        return pUserId;
    }
    public void setpUserName(String pUserName) {
        this.pUserName = pUserName;
    }

    public String getpUserName() {
        return pUserName;
    }
    public void setPropId(Long propId) {
        this.propId = propId;
    }

    public Long getPropId() {
        return propId;
    }
    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropName() {
        return propName;
    }
    public void setTotalDiamonds(BigDecimal totalDiamonds) {
        this.totalDiamonds = totalDiamonds;
    }

    public BigDecimal getTotalDiamonds() {
        return totalDiamonds;
    }
    public void setTotalTicket(BigDecimal totalTicket) {
        this.totalTicket = totalTicket;
    }

    public BigDecimal getTotalTicket() {
        return totalTicket;
    }
    public void setCurrentDiamonds(BigDecimal currentDiamonds) {
        this.currentDiamonds = currentDiamonds;
    }

    public BigDecimal getCurrentDiamonds() {
        return currentDiamonds;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pUserId", getpUserId())
            .append("pUserName", getpUserName())
            .append("propId", getPropId())
            .append("propName", getPropName())
            .append("totalDiamonds", getTotalDiamonds())
            .append("totalTicket", getTotalTicket())
            .append("currentDiamonds", getCurrentDiamonds())
            .append("toUserId", getToUserId())
            .append("createtime", getCreatetime())
            .toString();
    }
}
