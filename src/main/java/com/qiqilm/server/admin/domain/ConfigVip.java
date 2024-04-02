package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 【请填写功能名称】对象 config_vip
 *
 * @author 77tv
 * @date 2021-02-02
 */
public class ConfigVip extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统主键 */
    private String id;

    /** vip等级 */
    @Excel(name = "vip等级")
    private Integer levelFlag;

    /** 打码值 */
    @Excel(name = "打码值")
    private BigDecimal dmMoney;

    /** 晋级彩金 */
    @Excel(name = "晋级彩金")
    private BigDecimal jjcj;

    /** 周俸禄 */
    @Excel(name = "周俸禄")
    private BigDecimal zfl;

    /** 月俸禄 */
    @Excel(name = "月俸禄")
    private BigDecimal yfl;

    /** 通道加速(1是0否) */
    @Excel(name = "通道加速(1是0否)")
    private Long tdjs;

    /** 专属客服(1是0否) */
    @Excel(name = "专属客服(1是0否)")
    private Long zskf;

    /** 操作人 */
    @Excel(name = "操作人")
    private String opName;

    /** 需求打码量 */
    @Excel(name = "需求打码量")
    private BigDecimal levelMoney;

    private BigDecimal weekCharge;
    private BigDecimal monthCharge;
    private BigDecimal bcodeMultiple;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setLevelFlag(Integer levelFlag) {
        this.levelFlag = levelFlag;
    }

    public Integer getLevelFlag() {
        return levelFlag;
    }
    public void setDmMoney(BigDecimal dmMoney) {
        this.dmMoney = dmMoney;
    }

    public BigDecimal getDmMoney() {
        return dmMoney;
    }
    public void setJjcj(BigDecimal jjcj) {
        this.jjcj = jjcj;
    }

    public BigDecimal getJjcj() {
        return jjcj;
    }
    public void setZfl(BigDecimal zfl) {
        this.zfl = zfl;
    }

    public BigDecimal getZfl() {
        return zfl;
    }
    public void setYfl(BigDecimal yfl) {
        this.yfl = yfl;
    }

    public BigDecimal getYfl() {
        return yfl;
    }
    public void setTdjs(Long tdjs) {
        this.tdjs = tdjs;
    }

    public Long getTdjs() {
        return tdjs;
    }
    public void setZskf(Long zskf) {
        this.zskf = zskf;
    }

    public Long getZskf() {
        return zskf;
    }
    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }
    public void setLevelMoney(BigDecimal levelMoney) {
        this.levelMoney = levelMoney;
    }

    public BigDecimal getLevelMoney() {
        return levelMoney;
    }

    public BigDecimal getWeekCharge() {
        return weekCharge;
    }

    public void setWeekCharge( BigDecimal weekCharge ) {
        this.weekCharge = weekCharge;
    }

    public BigDecimal getMonthCharge() {
        return monthCharge;
    }

    public void setMonthCharge( BigDecimal monthCharge ) {
        this.monthCharge = monthCharge;
    }

    public BigDecimal getBcodeMultiple() {
        return bcodeMultiple;
    }

    public void setBcodeMultiple( BigDecimal bcodeMultiple ) {
        this.bcodeMultiple = bcodeMultiple;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("levelFlag", getLevelFlag())
            .append("dmMoney", getDmMoney())
            .append("jjcj", getJjcj())
            .append("zfl", getZfl())
            .append("yfl", getYfl())
            .append("tdjs", getTdjs())
            .append("zskf", getZskf())
            .append("createTime", getCreateTime())
            .append("opName", getOpName())
            .append("updateTime", getUpdateTime())
            .append("levelMoney", getLevelMoney())
            .toString();
    }
}