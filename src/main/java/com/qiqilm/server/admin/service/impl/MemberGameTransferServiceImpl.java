package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberGameTransferMapper;
import com.qiqilm.server.admin.domain.MemberGameTransfer;
import com.qiqilm.server.admin.service.IMemberGameTransferService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-08-05
 */
@Service
public class MemberGameTransferServiceImpl implements IMemberGameTransferService {
	@Autowired
	private MemberGameTransferMapper memberGameTransferMapper;

	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	@Override
	public MemberGameTransfer selectMemberGameTransferById(String id) {
		return memberGameTransferMapper.selectMemberGameTransferById(id);
	}

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberGameTransfer 【请填写功能名称】
	 * @return 【请填写功能名称】
	 */
	@Override
	public List<MemberGameTransfer> selectMemberGameTransferList(MemberGameTransfer memberGameTransfer) {
		if (memberGameTransfer.getSelectDate() != null && memberGameTransfer.getSelectDate().length > 0){
			memberGameTransfer.setStartTime(memberGameTransfer.getSelectDate()[ 0 ] + " 00:00:00");
			memberGameTransfer.setEndTime(memberGameTransfer.getSelectDate()[ 1 ] + " 23:59:59");
		}
		return memberGameTransferMapper.selectMemberGameTransferList(memberGameTransfer);
	}

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberGameTransfer 【请填写功能名称】
	 * @return 结果
	 */
	@Override
	public int insertMemberGameTransfer(MemberGameTransfer memberGameTransfer) {
		return memberGameTransferMapper.insertMemberGameTransfer(memberGameTransfer);
	}

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberGameTransfer 【请填写功能名称】
	 * @return 结果
	 */
	@Override
	public int updateMemberGameTransfer(MemberGameTransfer memberGameTransfer) {
		return memberGameTransferMapper.updateMemberGameTransfer(memberGameTransfer);
	}

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的【请填写功能名称】ID
	 * @return 结果
	 */
	@Override
	public int deleteMemberGameTransferByIds(String[] ids) {
		return memberGameTransferMapper.deleteMemberGameTransferByIds(ids);
	}

	/**
	 * 删除【请填写功能名称】信息
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	@Override
	public int deleteMemberGameTransferById(String id) {
		return memberGameTransferMapper.deleteMemberGameTransferById(id);
	}
}