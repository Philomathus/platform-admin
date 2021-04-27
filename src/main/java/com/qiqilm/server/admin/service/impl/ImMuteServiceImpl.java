package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.mapper.ImMuteMapper;
import com.qiqilm.server.admin.service.IImMuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 腾讯IM禁言查询Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-27
 */
@Service
public class ImMuteServiceImpl implements IImMuteService {
    @Autowired
    private ImMuteMapper imMuteMapper;

}
