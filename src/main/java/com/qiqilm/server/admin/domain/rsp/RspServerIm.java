package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

/**
 * IM即时通讯服务配置对象 server_im
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class RspServerIm{
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * appId
     */
    private String appId;

    /**
     * 管理员账号
     */
    private String identify;

    /**
     * 全员组
     */
    private String fullGroup;

    /**
     * 在线组
     */
    private String onlineGroup;

    /**
     * 服务商
     */
    private Integer provider;

    /**
     * 状态
     */
    private Integer isEffect;



    public String[] toCodes() {
        return new String[]{ "tim_sdkappid", "tim_sdk_key", "full_group_id", "on_line_group_id", "tim_identifier" };
    }

    public String getVal(String code) {
        switch (code){
            case "tim_sdkappid":
                return appId;
            case "tim_identifier":
                return identify;
            case "full_group_id":
                return fullGroup;
            case "on_line_group_id":
                return onlineGroup;
            default:
                return null;
        }
    }
}
