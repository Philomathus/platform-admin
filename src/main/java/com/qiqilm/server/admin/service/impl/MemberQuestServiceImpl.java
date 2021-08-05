package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberQuestMapper;
import com.qiqilm.server.admin.domain.MemberQuest;
import com.qiqilm.server.admin.service.IMemberQuestService;

/**
 * 会员任务列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-20
 */
@Service
public class MemberQuestServiceImpl implements IMemberQuestService {
    @Autowired
    private MemberQuestMapper memberQuestMapper;

    /**
     * 查询会员任务列表
     *
     * @param id 系统编号
     * @return 会员任务
     */
    @Override
    public MemberQuest selectMemberQuestById(String id) {
        return memberQuestMapper.selectMemberQuestById(id);
    }

    /**
     * 查询会员任务列表
     *
     * @param memberQuest 会员任务
     * @return 会员任务列表
     */
    @Override
    public List<MemberQuest> selectMemberQuestList(MemberQuest memberQuest) {
        return memberQuestMapper.selectMemberQuestList(memberQuest);
    }

    @Override
    public int addMemberScore(MemberQuest memberQuest) {
        return memberQuestMapper.updateMemberQuest(memberQuest);
    }


    /**
     * 新增会员任务
     *
     * @param memberQuest 【请填写功能名称】
     * @return 执行结果
     */
    @Override
    public int insertMemberQuest(MemberQuest memberQuest) {
        return memberQuestMapper.insertMemberQuest(memberQuest);
    }

    /**
     * 修改会员任务
     *
     * @param memberQuest 会员任务
     * @return 执行结果
     */
    @Override
    public int updateMemberQuest(MemberQuest memberQuest) {
        return memberQuestMapper.updateMemberQuest(memberQuest);
    }

    /**
     * 批量删除会员任务
     *
     * @param ids 系统编号
     * @return 执行结果
     */
    @Override
    public int deleteMemberQuestByIds(String[] ids) {
        return memberQuestMapper.deleteMemberQuestByIds(ids);
    }

    /**
     * 删除会员任务
     *
     * @param id 系统编号
     * @return 执行结果
     */
    @Override
    public int deleteMemberQuestById(String id) {
        return memberQuestMapper.deleteMemberQuestById(id);
    }
}