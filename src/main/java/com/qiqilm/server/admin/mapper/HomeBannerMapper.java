package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.HomeBanner;

/**
 * 首页轮播图Mapper接口
 *
 * @author 77tv
 * @date 2021-02-07
 */
public interface HomeBannerMapper {
	/**
	 * 查询首页轮播图
	 *
	 * @param id 首页轮播图ID
	 * @return 首页轮播图
	 */
	public HomeBanner selectHomeBannerById(String id);

	/**
	 * 查询首页轮播图列表
	 *
	 * @param homeBanner 首页轮播图
	 * @return 首页轮播图集合
	 */
	public List<HomeBanner> selectHomeBannerList(HomeBanner homeBanner);

	/**
	 * 新增首页轮播图
	 *
	 * @param homeBanner 首页轮播图
	 * @return 结果
	 */
	public int insertHomeBanner(HomeBanner homeBanner);

	/**
	 * 修改首页轮播图
	 *
	 * @param homeBanner 首页轮播图
	 * @return 结果
	 */
	public int updateHomeBanner(HomeBanner homeBanner);

	/**
	 * 删除首页轮播图
	 *
	 * @param id 首页轮播图ID
	 * @return 结果
	 */
	public int deleteHomeBannerById(String id);

	/**
	 * 批量删除首页轮播图
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteHomeBannerByIds(String[] ids );
}
