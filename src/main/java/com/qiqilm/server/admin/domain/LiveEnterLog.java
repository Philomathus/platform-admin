package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 拉黑对象 live_enter_log
 *
 * @author 77tv
 * @date 2021-04-06
 */
@Data
public class LiveEnterLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 日期_会员ID */
    private String id;

    /** 平台用户ID */
    @Excel(name = "平台用户ID")
    private String pUserId;

    /** 创建日期 */
    @Excel(name = "创建日期")
    private String dtime;

    /** 次数 */
    @Excel(name = "次数")
    private Long times;




}