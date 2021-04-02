package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberQuestMapper;
import com.qiqilm.server.admin.domain.MemberQuest;
import com.qiqilm.server.admin.service.IMemberQuestService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-20
 */
@Service
public class MemberQuestServiceImpl implements IMemberQuestService {
    @Autowired
    private MemberQuestMapper memberQuestMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberQuest selectMemberQuestById(String id) {
        return memberQuestMapper.selectMemberQuestById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberQuest 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberQuest> selectMemberQuestList(MemberQuest memberQuest) {
        return memberQuestMapper.selectMemberQuestList(memberQuest);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberQuest 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberQuest(MemberQuest memberQuest) {
        return memberQuestMapper.insertMemberQuest(memberQuest);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberQuest 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberQuest(MemberQuest memberQuest) {
        return memberQuestMapper.updateMemberQuest(memberQuest);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberQuestByIds(String[] ids) {
        return memberQuestMapper.deleteMemberQuestByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberQuestById(String id) {
        return memberQuestMapper.deleteMemberQuestById(id);
    }
}