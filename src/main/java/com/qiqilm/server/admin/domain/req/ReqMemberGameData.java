package com.qiqilm.server.admin.domain.req;

import lombok.Data;

@Data
public class ReqMemberGameData {
    /**
     * 选择日期
     */
    private String[] selectDate;

    private String startTime;

    private String endTime;

    private String tableLast;

    /** 账号 */
    private String account;

    /** 代理编号 */
    private String agent;

    /** 子平台名称 */
    private String sonPlatformName;

    private String gameId;
}
