package com.qiqilm.server.admin.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveLimitMsgMapper;
import com.qiqilm.server.admin.domain.LiveLimitMsg;
import com.qiqilm.server.admin.service.ILiveLimitMsgService;
import org.springframework.util.CollectionUtils;

/**
 * //昵称限制Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LiveLimitMsgServiceImpl implements ILiveLimitMsgService {
    @Autowired
    private LiveLimitMsgMapper liveLimitMsgMapper;


    @Override
    public String selectLiveLimitMsgById() {

        List<LiveLimitMsg> msgs = liveLimitMsgMapper.selectLiveLimitMsgList();
        if ( !CollectionUtils.isEmpty( msgs ) ) {
            return msgs.stream().map( LiveLimitMsg::getName ).collect( Collectors.joining( "," ) );
        }
        return "";
    }




    /**
     * 修改//昵称限制
     *
     * @param liveLimitMsg //昵称限制
     * @return 结果
     */
    @Override
    public int updateLiveLimitMsg(LiveLimitMsg liveLimitMsg) {

        List<String> strings = Arrays.asList( liveLimitMsg.getName().split( "," ) );
        int          i       = liveLimitMsgMapper.deleteAll();
        liveLimitMsgMapper.insertBatch( strings );
        return 0;
    }

}