package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.domain.ActivityQuestInfo;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.HomeBannerMapper;
import com.qiqilm.server.admin.domain.HomeBanner;
import com.qiqilm.server.admin.service.IHomeBannerService;
import org.springframework.util.CollectionUtils;

/**
 * 首页轮播图Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-07
 */
@Service
public class HomeBannerServiceImpl implements IHomeBannerService {
    @Autowired
    private HomeBannerMapper      homeBannerMapper;
    @Autowired
    private ConfigDomainCacheUtil configDomainCacheUtil;

    /**
     * 查询首页轮播图
     *
     * @param id 首页轮播图ID
     * @return 首页轮播图
     */
    @Override
    public HomeBanner selectHomeBannerById(String id) {
        return homeBannerMapper.selectHomeBannerById(id);
    }

    /**
     * 查询首页轮播图列表
     *
     * @param homeBanner 首页轮播图
     * @return 首页轮播图
     */
    @Override
    public List<HomeBanner> selectHomeBannerList(HomeBanner homeBanner) {
        List<HomeBanner> homeBanners = homeBannerMapper.selectHomeBannerList( homeBanner );
        if ( !CollectionUtils.isEmpty( homeBanners ) ) {
            String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
            for ( HomeBanner banner : homeBanners ) {
                if ( StringUtils.isNotBlank( banner.getCoverImg() ) && !banner.getCoverImg().startsWith( "http" ) ) {
                    banner.setCoverImg( domainValue + banner.getCoverImg() );
                }
            }
        }
        return homeBanners;
    }

    /**
     * 新增首页轮播图
     *
     * @param homeBanner 首页轮播图
     * @return 结果
     */
    @Override
    public int insertHomeBanner(HomeBanner homeBanner) {
        return homeBannerMapper.insertHomeBanner(homeBanner);
    }

    /**
     * 修改首页轮播图
     *
     * @param homeBanner 首页轮播图
     * @return 结果
     */
    @Override
    public int updateHomeBanner(HomeBanner homeBanner) {
        homeBanner.setUpdateTime(DateUtils.getNowDate());
        return homeBannerMapper.updateHomeBanner(homeBanner);
    }

    /**
     * 批量删除首页轮播图
     *
     * @param ids 需要删除的首页轮播图ID
     * @return 结果
     */
    @Override
    public int deleteHomeBannerByIds(String[] ids) {
        return homeBannerMapper.deleteHomeBannerByIds(ids);
    }

    /**
     * 删除首页轮播图信息
     *
     * @param id 首页轮播图ID
     * @return 结果
     */
    @Override
    public int deleteHomeBannerById(String id) {
        return homeBannerMapper.deleteHomeBannerById(id);
    }
}
