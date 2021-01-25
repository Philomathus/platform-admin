package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.IMemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MemberInfoServiceImpl implements IMemberInfoService {
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberInfo selectMemberInfoById(String id) {
        return memberInfoMapper.selectMemberInfoById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberInfo 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo) {
        return memberInfoMapper.selectMemberInfoList(memberInfo);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.insertMemberInfo(memberInfo);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberInfo 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberInfo(MemberInfo memberInfo) {
        return memberInfoMapper.updateMemberInfo(memberInfo);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoByIds(String[] ids) {
        return memberInfoMapper.deleteMemberInfoByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoById(String id) {
        return memberInfoMapper.deleteMemberInfoById(id);
    }
}
