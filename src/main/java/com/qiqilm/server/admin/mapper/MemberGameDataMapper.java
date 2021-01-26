package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.MemberGameData;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface MemberGameDataMapper {



	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberGameData 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberGameData> selectMemberGameDataList(MemberGameData memberGameData);


}