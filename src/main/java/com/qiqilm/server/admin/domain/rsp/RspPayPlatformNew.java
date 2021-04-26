package com.qiqilm.server.admin.domain.rsp;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 pay_platform_new
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class RspPayPlatformNew {
    /** 主键 */
    private String id;

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

    /** 平台IP白名单 */
    @Excel(name = "平台IP白名单")
    private String platWhiteIpList;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updator;

}
