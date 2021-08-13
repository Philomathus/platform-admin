package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberGameTransfer;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-08-05
 */
public interface IMemberGameTransferService {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public MemberGameTransfer selectMemberGameTransferById(String id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberGameTransfer 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MemberGameTransfer> selectMemberGameTransferList(MemberGameTransfer memberGameTransfer);

    /**
     * 新增【请填写功能名称】
     *
     * @param memberGameTransfer 【请填写功能名称】
     * @return 结果
     */
    public int insertMemberGameTransfer(MemberGameTransfer memberGameTransfer);

    /**
     * 修改【请填写功能名称】
     *
     * @param memberGameTransfer 【请填写功能名称】
     * @return 结果
     */
    public int updateMemberGameTransfer(MemberGameTransfer memberGameTransfer);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteMemberGameTransferByIds(String[] ids );

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteMemberGameTransferById(String id);
}