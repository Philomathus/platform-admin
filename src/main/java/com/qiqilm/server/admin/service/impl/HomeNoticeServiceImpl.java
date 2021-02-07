package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.HomeNoticeMapper;
import com.qiqilm.server.admin.domain.HomeNotice;
import com.qiqilm.server.admin.service.IHomeNoticeService;

/**
 * 系统公告Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-07
 */
@Service
public class HomeNoticeServiceImpl implements IHomeNoticeService {
    @Autowired
    private HomeNoticeMapper homeNoticeMapper;

    /**
     * 查询系统公告
     *
     * @param id 系统公告ID
     * @return 系统公告
     */
    @Override
    public HomeNotice selectHomeNoticeById(String id) {
        return homeNoticeMapper.selectHomeNoticeById(id);
    }

    /**
     * 查询系统公告列表
     *
     * @param homeNotice 系统公告
     * @return 系统公告
     */
    @Override
    public List<HomeNotice> selectHomeNoticeList(HomeNotice homeNotice) {
        return homeNoticeMapper.selectHomeNoticeList(homeNotice);
    }

    /**
     * 新增系统公告
     *
     * @param homeNotice 系统公告
     * @return 结果
     */
    @Override
    public int insertHomeNotice(HomeNotice homeNotice) {
        return homeNoticeMapper.insertHomeNotice(homeNotice);
    }

    /**
     * 修改系统公告
     *
     * @param homeNotice 系统公告
     * @return 结果
     */
    @Override
    public int updateHomeNotice(HomeNotice homeNotice) {
        homeNotice.setUpdateTime(DateUtils.getNowDate());
        return homeNoticeMapper.updateHomeNotice(homeNotice);
    }

    /**
     * 批量删除系统公告
     *
     * @param ids 需要删除的系统公告ID
     * @return 结果
     */
    @Override
    public int deleteHomeNoticeByIds(String[] ids) {
        return homeNoticeMapper.deleteHomeNoticeByIds(ids);
    }

    /**
     * 删除系统公告信息
     *
     * @param id 系统公告ID
     * @return 结果
     */
    @Override
    public int deleteHomeNoticeById(String id) {
        return homeNoticeMapper.deleteHomeNoticeById(id);
    }
}
