package com.qiqilm.server.admin.domain;

import lombok.Data;

@Data
public class LiveGuardUser {
    int    id;
    String anchorId;

    String userId;

    int type;

    String guardEndTime;

    String nickName;

    int vip;

    String headImage;

    String startTime;
}
