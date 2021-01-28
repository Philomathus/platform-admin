package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LivePropMapper;
import com.qiqilm.server.admin.domain.LiveProp;
import com.qiqilm.server.admin.service.ILivePropService;

/**
 * 礼物列Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LivePropServiceImpl implements ILivePropService {
    @Autowired
    private LivePropMapper livePropMapper;

    /**
     * 查询礼物列
     *
     * @param id 礼物列ID
     * @return 礼物列
     */
    @Override
    public LiveProp selectLivePropById(Long id) {
        return livePropMapper.selectLivePropById(id);
    }

    /**
     * 查询礼物列列表
     *
     * @param liveProp 礼物列
     * @return 礼物列
     */
    @Override
    public List<LiveProp> selectLivePropList(LiveProp liveProp) {
        return livePropMapper.selectLivePropList(liveProp);
    }

    /**
     * 新增礼物列
     *
     * @param liveProp 礼物列
     * @return 结果
     */
    @Override
    public int insertLiveProp(LiveProp liveProp) {
        return livePropMapper.insertLiveProp(liveProp);
    }

    /**
     * 修改礼物列
     *
     * @param liveProp 礼物列
     * @return 结果
     */
    @Override
    public int updateLiveProp(LiveProp liveProp) {
        return livePropMapper.updateLiveProp(liveProp);
    }

    /**
     * 批量删除礼物列
     *
     * @param ids 需要删除的礼物列ID
     * @return 结果
     */
    @Override
    public int deleteLivePropByIds(Long[] ids) {
        return livePropMapper.deleteLivePropByIds(ids);
    }

    /**
     * 删除礼物列信息
     *
     * @param id 礼物列ID
     * @return 结果
     */
    @Override
    public int deleteLivePropById(Long id) {
        return livePropMapper.deleteLivePropById(id);
    }

    @Override
    public List<LiveProp> getList( ) {
        return livePropMapper.getList();
    }
}
