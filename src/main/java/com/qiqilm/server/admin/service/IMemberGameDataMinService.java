package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.domain.MemberGameData;

import java.util.List;

/**
 * 会员注单数据Service接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
@FunctionalInterface
public interface IMemberGameDataMinService<T> {
	/**
	 * 查询会员注单数据列表
	 *
	 * @param memberGameData 会员注单数据
	 * @return 会员注单数据集合
	 */
	List<T> selectMemberGameDataMinList(MemberGameData memberGameData);

	@FunctionalInterface
	interface BooleanAgent {    boolean getBoolean(); }

}
