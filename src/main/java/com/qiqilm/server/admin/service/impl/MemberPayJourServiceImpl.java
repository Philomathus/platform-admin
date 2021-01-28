package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberPayJourMapper;
import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.service.IMemberPayJourService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberPayJourServiceImpl implements IMemberPayJourService {
    @Autowired
    private MemberPayJourMapper memberPayJourMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberPayJour selectMemberPayJourById(String id) {
        return memberPayJourMapper.selectMemberPayJourById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberPayJour 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberPayJour> selectMemberPayJourList(MemberPayJour memberPayJour) {
        return memberPayJourMapper.selectMemberPayJourList(memberPayJour);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberPayJour 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberPayJour(MemberPayJour memberPayJour) {
        memberPayJour.setCreateTime(DateUtils.getNowDate());
        return memberPayJourMapper.insertMemberPayJour(memberPayJour);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberPayJour 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberPayJour(MemberPayJour memberPayJour) {
        memberPayJour.setUpdateTime(DateUtils.getNowDate());
        return memberPayJourMapper.updateMemberPayJour(memberPayJour);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberPayJourByIds(String[] ids) {
        return memberPayJourMapper.deleteMemberPayJourByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberPayJourById(String id) {
        return memberPayJourMapper.deleteMemberPayJourById(id);
    }
}
