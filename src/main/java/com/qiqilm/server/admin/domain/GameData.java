package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

/**
 * 会员注单数据对象 member_game_data
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Data
public class GameData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 局记录id **/
    private String recordId;
    /** 局 **/
    private String gameUserNo;
    /** 房间号 **/
    private String serverId;
    /** 投注状态 betState **/
    private String betState;
    /** 日期时间段 */
    private String[] selectDate;

}
