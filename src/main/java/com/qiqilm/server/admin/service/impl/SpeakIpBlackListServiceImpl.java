package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.SpeakIpBlackListMapper;
import com.qiqilm.server.admin.domain.SpeakIpBlackList;
import com.qiqilm.server.admin.service.ISpeakIpBlackListService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-22
 */
@Service
public class SpeakIpBlackListServiceImpl implements ISpeakIpBlackListService {
    @Autowired
    private SpeakIpBlackListMapper speakIpBlackListMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public SpeakIpBlackList selectSpeakIpBlackListById(String id) {
        return speakIpBlackListMapper.selectSpeakIpBlackListById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param speakIpBlackList 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<SpeakIpBlackList> selectSpeakIpBlackListList(SpeakIpBlackList speakIpBlackList) {
        return speakIpBlackListMapper.selectSpeakIpBlackListList(speakIpBlackList);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param speakIpBlackList 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertSpeakIpBlackList(SpeakIpBlackList speakIpBlackList) {
        speakIpBlackList.setCreateTime(DateUtils.getNowDate());
        return speakIpBlackListMapper.insertSpeakIpBlackList(speakIpBlackList);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param speakIpBlackList 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateSpeakIpBlackList(SpeakIpBlackList speakIpBlackList) {
        return speakIpBlackListMapper.updateSpeakIpBlackList(speakIpBlackList);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteSpeakIpBlackListByIds(String[] ids) {
        return speakIpBlackListMapper.deleteSpeakIpBlackListByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteSpeakIpBlackListById(String id) {
        return speakIpBlackListMapper.deleteSpeakIpBlackListById(id);
    }
}