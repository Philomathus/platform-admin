package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveVideoPropMapper;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.service.ILiveVideoPropService;

/**
 * 送礼物Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveVideoPropServiceImpl implements ILiveVideoPropService {
    @Autowired
    private LiveVideoPropMapper liveVideoPropMapper;


    /**
     * 查询送礼物列表
     *
     * @param liveVideoProp 送礼物
     * @return 送礼物
     */
    @Override
    public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp) {
        if ( liveVideoProp.getSelectDate() != null && liveVideoProp.getSelectDate().length > 0 ) {
            liveVideoProp.setStartTime( liveVideoProp.getSelectDate()[ 0 ] + " 00:00:00");
            liveVideoProp.setEndTime( liveVideoProp.getSelectDate()[ 1 ] + " 23:59:59");
        }
        return liveVideoPropMapper.selectLiveVideoPropList(liveVideoProp);
    }

    @Override
    public LiveVideoProp getCount(LiveVideoProp liveVideoProp) {
        if ( liveVideoProp.getSelectDate() != null && liveVideoProp.getSelectDate().length > 0 ) {
            liveVideoProp.setStartTime( liveVideoProp.getSelectDate()[ 0 ] + " 00:00:00");
            liveVideoProp.setEndTime( liveVideoProp.getSelectDate()[ 1 ] + " 23:59:59");
        }
        return liveVideoPropMapper.getCount(liveVideoProp);
    }

}
