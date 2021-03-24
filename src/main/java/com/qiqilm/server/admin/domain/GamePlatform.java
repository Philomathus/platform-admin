package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 game_platform
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class GamePlatform extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 平台类型（见EnumGamePlatform） */
    private Integer id;

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
    private Integer status;

    /** 打码比例 */
    @Excel(name = "打码比例")
    private BigDecimal rateBeat;


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