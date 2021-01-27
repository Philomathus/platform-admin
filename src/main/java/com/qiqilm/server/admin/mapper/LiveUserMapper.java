package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * //用户信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveUserMapper {
	/**
	 * 查询//用户信息
	 *
	 * @param id //用户信息ID
	 * @return //用户信息
	 */
	public LiveUser selectLiveUserById(Long id);

	/**
	 * 查询//用户信息列表
	 *
	 * @param liveUser //用户信息
	 * @return //用户信息集合
	 */
	public List<LiveUser> selectLiveUserList(LiveUser liveUser);

	/**
	 * 新增//用户信息
	 *
	 * @param liveUser //用户信息
	 * @return 结果
	 */
	public int insertLiveUser(LiveUser liveUser);

	/**
	 * 修改//用户信息
	 *
	 * @param liveUser //用户信息
	 * @return 结果
	 */
	public int updateLiveUser(LiveUser liveUser);

	/**
	 * 删除//用户信息
	 *
	 * @param id //用户信息ID
	 * @return 结果
	 */
	public int deleteLiveUserById(Long id);

	/**
	 * 批量删除//用户信息
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveUserByIds(Long[] ids );

    @Select( "SELECT family_id FROM 7700_live.live_user where id = ${userId}" )
    int getFamilyId(@Param( "userId" ) Long userId);

    @Update( "update 7700_live.live_user set family_id = ${familyID} where id= ${userId}" )
    int updateFamilyID( @Param( "familyID" ) Long familyID, @Param( "userId" ) Long userId );

    @Select( "SELECT count(id) as num FROM 7700_live.live_user where family_id = ${family_id}" )
    int getNumFamily(@Param( "family_id" ) Integer family_id);
}
