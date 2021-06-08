package com.qiqilm.server.admin.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.qiqilm.server.admin.domain.ActivityCashBack;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-06-07
 */
public interface ActivityCashBackMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public ActivityCashBack selectActivityCashBackById(Long id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param activityCashBack 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<ActivityCashBack> selectActivityCashBackList(ActivityCashBack activityCashBack);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param activityCashBack 【请填写功能名称】
	 * @return 结果
	 */
	public int insertActivityCashBack(ActivityCashBack activityCashBack);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param activityCashBack 【请填写功能名称】
	 * @return 结果
	 */
	public int updateActivityCashBack(ActivityCashBack activityCashBack);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteActivityCashBackById(Long id);

	Integer selectActivityCashBackBycash(@Param("cash") BigDecimal cash);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteActivityCashBackByIds(Long[] ids );
}
