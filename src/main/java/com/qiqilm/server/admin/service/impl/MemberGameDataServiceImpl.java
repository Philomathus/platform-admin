package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qiqilm.server.admin.service.IMemberGameDataService;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberGameDataServiceImpl implements IMemberGameDataService {
    @Autowired
    private MemberGameDataMapper memberGameDataMapper;


    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberGameData 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberGameData> selectMemberGameDataList(MemberGameData memberGameData) {
        return memberGameDataMapper.selectMemberGameDataList(memberGameData);
    }

}