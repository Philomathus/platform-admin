package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveBlack;

import java.util.List;

public interface ILiveBlackService {

    List<LiveBlack> selectLiveBlackList(LiveBlack liveBlack);

    AjaxResult deleteLiveBlackById(LiveBlack liveBlack);
}
