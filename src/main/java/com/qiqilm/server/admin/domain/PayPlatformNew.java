package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_platform_new
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class PayPlatformNew extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String name;

    /** 平台编码 */
    @Excel(name = "平台编码")
    private String code;

    /** 商户ID */
    @Excel(name = "商户ID")
    private String merId;

    /** 机构号 */
    @Excel(name = "机构号")
    private String orgId;

    /** 平台下单接口地址 */
    @Excel(name = "平台下单接口地址")
    private String platPayUrl;

    /** 平台订单查询地址 */
    @Excel(name = "平台订单查询地址")
    private String platQueryUrl;

    /** md5加密密钥 */
    @Excel(name = "md5加密密钥")
    private String signMd5;

    /** 加密公钥 */
    @Excel(name = "加密公钥")
    private String signPublicKey;

    /** 解密私钥 */
    @Excel(name = "解密私钥")
    private String signPrivateKey;

    /** 平台IP白名单 */
    @Excel(name = "平台IP白名单")
    private String platWhiteIpList;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updator;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getMerId() {
        return merId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgId() {
        return orgId;
    }
    public void setPlatPayUrl(String platPayUrl) {
        this.platPayUrl = platPayUrl;
    }

    public String getPlatPayUrl() {
        return platPayUrl;
    }
    public void setPlatQueryUrl(String platQueryUrl) {
        this.platQueryUrl = platQueryUrl;
    }

    public String getPlatQueryUrl() {
        return platQueryUrl;
    }
    public void setSignMd5(String signMd5) {
        this.signMd5 = signMd5;
    }

    public String getSignMd5() {
        return signMd5;
    }
    public void setSignPublicKey(String signPublicKey) {
        this.signPublicKey = signPublicKey;
    }

    public String getSignPublicKey() {
        return signPublicKey;
    }
    public void setSignPrivateKey(String signPrivateKey) {
        this.signPrivateKey = signPrivateKey;
    }

    public String getSignPrivateKey() {
        return signPrivateKey;
    }
    public void setPlatWhiteIpList(String platWhiteIpList) {
        this.platWhiteIpList = platWhiteIpList;
    }

    public String getPlatWhiteIpList() {
        return platWhiteIpList;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }
    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getUpdator() {
        return updator;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("merId", getMerId())
            .append("orgId", getOrgId())
            .append("platPayUrl", getPlatPayUrl())
            .append("platQueryUrl", getPlatQueryUrl())
            .append("signMd5", getSignMd5())
            .append("signPublicKey", getSignPublicKey())
            .append("signPrivateKey", getSignPrivateKey())
            .append("platWhiteIpList", getPlatWhiteIpList())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("updator", getUpdator())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
