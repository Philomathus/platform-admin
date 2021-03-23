package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveProplog;
import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.vo.LiveVideoPropVo;
import org.apache.ibatis.annotations.Param;

/**
 * 送礼物Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveVideoPropMapper {
	/**
	 * 查询送礼物
	 *
	 * @param id 送礼物ID
	 * @return 送礼物
	 */
	public LiveVideoProp selectLiveVideoPropById(Long id);

	/**
	 * 查询送礼物列表
	 *
	 * @param liveVideoProp 送礼物
	 * @return 送礼物集合
	 */
	public List<LiveVideoProp> selectLiveVideoPropList(LiveVideoProp liveVideoProp);


	public List<LiveVideoPropVo> findVideoPropList(@Param("start") long start, @Param("end") long end);

	/**
	 * 新增送礼物
	 *
	 * @param liveVideoProp 送礼物
	 * @return 结果
	 */
	public int insertLiveVideoProp(LiveVideoProp liveVideoProp);

	/**
	 * 修改送礼物
	 *
	 * @param liveVideoProp 送礼物
	 * @return 结果
	 */
	public int updateLiveVideoProp(LiveVideoProp liveVideoProp);

	/**
	 * 删除送礼物
	 *
	 * @param id 送礼物ID
	 * @return 结果
	 */
	public int deleteLiveVideoPropById(Long id);

	/**
	 * 批量删除送礼物
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLiveVideoPropByIds(Long[] ids );
}
