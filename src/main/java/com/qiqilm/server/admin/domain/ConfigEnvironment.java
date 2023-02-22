package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

/**
 * 【请填写功能名称】对象 config_environment
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
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

    @Excel(name = "DEVICE_TYPE")
    private String deviceType;
}
