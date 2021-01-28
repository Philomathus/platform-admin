package com.qiqilm.server.admin.service;

import java.util.List;

import com.qiqilm.server.admin.domain.MemberDirtyWords;

/**
 * 【请填写功能名称】Service接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface IMemberDirtyWordsService {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberDirtyWords selectMemberDirtyWordsById();



	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberDirtyWords 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberDirtyWords(MemberDirtyWords memberDirtyWords);

}