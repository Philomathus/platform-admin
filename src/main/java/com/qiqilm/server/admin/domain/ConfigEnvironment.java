package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 config_environment
 *
 * @author 77tv
 * @date 2021-01-27
 */
public class ConfigEnvironment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单类型 M是类型C是属性
     */
    private String menuType;
    /** 参数标题  */
    private String envTitle;

    /** 参数编码 */
    private String envCode;

    /** 参数值 */
    @Excel(name = "参数值")
    private String envValue;

    /** 参数说明 */
    @Excel(name = "参数说明")
    private String envDes;

    /** 参数组 */
    @Excel(name = "参数组")
    private Long envGroup;

    /** 排序 */
    @Excel(name = "排序")
    private Long envSort;

    /** 状态 1启用 0禁用 */
    @Excel(name = "状态 1启用 0禁用")
    private Integer envStatus;

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getEnvTitle() {
        return envTitle;
    }

    public void setEnvTitle(String envTitle) {
        this.envTitle = envTitle;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getEnvCode() {
        return envCode;
    }
    public void setEnvValue(String envValue) {
        this.envValue = envValue;
    }

    public String getEnvValue() {
        return envValue;
    }
    public void setEnvDes(String envDes) {
        this.envDes = envDes;
    }

    public String getEnvDes() {
        return envDes;
    }
    public void setEnvGroup(Long envGroup) {
        this.envGroup = envGroup;
    }

    public Long getEnvGroup() {
        return envGroup;
    }
    public void setEnvSort(Long envSort) {
        this.envSort = envSort;
    }

    public Long getEnvSort() {
        return envSort;
    }
    public void setEnvStatus(Integer envStatus) {
        this.envStatus = envStatus;
    }

    public Integer getEnvStatus() {
        return envStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("envCode", getEnvCode())
            .append("envValue", getEnvValue())
            .append("envDes", getEnvDes())
            .append("envGroup", getEnvGroup())
            .append("envSort", getEnvSort())
            .append("envStatus", getEnvStatus())
            .toString();
    }
}
