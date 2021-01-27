package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * IP白名单对象 system_ip_white
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class SystemIpWhite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String ipId;

    /** IP白名单 */
    @Excel(name = "IP白名单")
    private String ipAddress;

    /** IP白名单启用状态 */
    @Excel(name = "IP白名单启用状态")
    private String ipStatus;

    /** 添加管理员 */
    @Excel(name = "添加管理员")
    private String ipAdmin;

    /** 备注 */
    @Excel(name = "备注")
    private String mark;

    /** IP登录数量 */
    @Excel(name = "IP登录数量")
    private Long ipCount;

    public void setIpId(String ipId) {
        this.ipId = ipId;
    }

    public String getIpId() {
        return ipId;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpStatus(String ipStatus) {
        this.ipStatus = ipStatus;
    }

    public String getIpStatus() {
        return ipStatus;
    }
    public void setIpAdmin(String ipAdmin) {
        this.ipAdmin = ipAdmin;
    }

    public String getIpAdmin() {
        return ipAdmin;
    }
    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
    public void setIpCount(Long ipCount) {
        this.ipCount = ipCount;
    }

    public Long getIpCount() {
        return ipCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ipId", getIpId())
            .append("ipAddress", getIpAddress())
            .append("ipStatus", getIpStatus())
            .append("ipAdmin", getIpAdmin())
            .append("mark", getMark())
            .append("ipCount", getIpCount())
            .toString();
    }
}
