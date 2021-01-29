package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 game_platform
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class GamePlatform extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 平台类型（见EnumGamePlatform） */
    private Long id;

    /** 代理（渠道）号 */
    @Excel(name = "代理", readConverterExp = "渠=道")
    private String agent;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String name;

    /** API接口 */
    @Excel(name = "API接口")
    private String apiUrl;

    /** 查询注单 */
    @Excel(name = "查询注单")
    private String recordUrl;

    /** DES密钥 */
    @Excel(name = "DES密钥")
    private String des;

    /** MD5密钥 */
    @Excel(name = "MD5密钥")
    private String md5;

    /** 站点标识 */
    @Excel(name = "站点标识")
    private String linecode;

    /** 洗码比例 */
    @Excel(name = "洗码比例")
    private BigDecimal rateClean;

    /** 游戏类型ID */
    @Excel(name = "游戏类型ID")
    private String gameTypeid;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 打码比例 */
    @Excel(name = "打码比例")
    private BigDecimal rateBeat;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgent() {
        return agent;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }
    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    public String getRecordUrl() {
        return recordUrl;
    }
    public void setDes(String des) {
        this.des = des;
    }

    public String getDes() {
        return des;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }
    public void setLinecode(String linecode) {
        this.linecode = linecode;
    }

    public String getLinecode() {
        return linecode;
    }
    public void setRateClean(BigDecimal rateClean) {
        this.rateClean = rateClean;
    }

    public BigDecimal getRateClean() {
        return rateClean;
    }
    public void setGameTypeid(String gameTypeid) {
        this.gameTypeid = gameTypeid;
    }

    public String getGameTypeid() {
        return gameTypeid;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public void setRateBeat(BigDecimal rateBeat) {
        this.rateBeat = rateBeat;
    }

    public BigDecimal getRateBeat() {
        return rateBeat;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("agent", getAgent())
            .append("name", getName())
            .append("apiUrl", getApiUrl())
            .append("recordUrl", getRecordUrl())
            .append("des", getDes())
            .append("md5", getMd5())
            .append("linecode", getLinecode())
            .append("rateClean", getRateClean())
            .append("gameTypeid", getGameTypeid())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("rateBeat", getRateBeat())
            .toString();
    }
}