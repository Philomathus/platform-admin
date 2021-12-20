package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MobileLimit;
import com.qiqilm.server.admin.mapper.MobileLimitMapper;
import com.qiqilm.server.admin.service.IMobileLimitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 手机号限制Service业务层处理
 *
 * @author 77tv
 * @date 2021-12-09
 */
@Service
public class MobileLimitServiceImpl implements IMobileLimitService {
    @Resource
    private MobileLimitMapper mobileLimitMapper;

    /**
     * 查询手机号限制
     *
     * @param id 手机号限制ID
     * @return 手机号限制
     */
    @Override
    public MobileLimit selectMobileLimitById(Long id) {
        return mobileLimitMapper.selectMobileLimitById(id);
    }

    /**
     * 查询手机号限制列表
     *
     * @param mobileLimit 手机号限制
     * @return 手机号限制
     */
    @Override
    public List<MobileLimit> selectMobileLimitList(MobileLimit mobileLimit) {
        return mobileLimitMapper.selectMobileLimitList(mobileLimit);
    }

    /**
     * 新增手机号限制
     *
     * @param mobileLimit 手机号限制
     * @return 结果
     */
    @Override
    public int insertMobileLimit(MobileLimit mobileLimit) {
        return mobileLimitMapper.insertMobileLimit(mobileLimit);
    }

    /**
     * 修改手机号限制
     *
     * @param mobileLimit 手机号限制
     * @return 结果
     */
    @Override
    public int updateMobileLimit(MobileLimit mobileLimit) {
        return mobileLimitMapper.updateMobileLimit(mobileLimit);
    }

    /**
     * 批量删除手机号限制
     *
     * @param ids 需要删除的手机号限制ID
     * @return 结果
     */
    @Override
    public int deleteMobileLimitByIds(Long[] ids) {
        return mobileLimitMapper.deleteMobileLimitByIds(ids);
    }

    /**
     * 删除手机号限制信息
     *
     * @param id 手机号限制ID
     * @return 结果
     */
    @Override
    public int deleteMobileLimitById(Long id) {
        return mobileLimitMapper.deleteMobileLimitById(id);
    }
}
