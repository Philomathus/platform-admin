package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_agent_platform
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class PayAgentPlatform extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 自定义编码 */
    @Excel(name = "自定义编码")
    private String code;

    /** 代付平台名称 */
    @Excel(name = "代付平台名称")
    private String name;

    /** 商户ID */
    @Excel(name = "商户ID")
    private String merId;

    /** 代付下单地址 */
    @Excel(name = "代付下单地址")
    private String payOrderAddr;

    /** 代付查询地址 */
    @Excel(name = "代付查询地址")
    private String payOrderQueryAddr;

    /** 头部key */
    private String headerKey;

    /** md5加密密钥 */
    private String signMd5;

    /** 加密公钥 */
    private String signPublicKey;

    /** 解密私钥 */
    private String signPrivateKey;

    /** 平台IP白名单 */
    private String platWhiteIpList;

    /** 状态 1启用 0禁用 */
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("merId", getMerId())
            .append("payOrderAddr", getPayOrderAddr())
            .append("payOrderQueryAddr", getPayOrderQueryAddr())
            .append("headerKey", getHeaderKey())
            .append("signMd5", getSignMd5())
            .append("signPublicKey", getSignPublicKey())
            .append("signPrivateKey", getSignPrivateKey())
            .append("platWhiteIpList", getPlatWhiteIpList())
            .append("status", getStatus())
            .toString();
    }
}
