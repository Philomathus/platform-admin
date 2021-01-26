package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.H5PluginMapper;
import com.qiqilm.server.admin.domain.H5Plugin;
import com.qiqilm.server.admin.service.IH5PluginService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class H5PluginServiceImpl implements IH5PluginService {
    @Autowired
    private H5PluginMapper h5PluginMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public H5Plugin selectH5PluginById(Long id) {
        return h5PluginMapper.selectH5PluginById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param h5Plugin 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<H5Plugin> selectH5PluginList(H5Plugin h5Plugin) {
        return h5PluginMapper.selectH5PluginList(h5Plugin);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param h5Plugin 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertH5Plugin(H5Plugin h5Plugin) {
        return h5PluginMapper.insertH5Plugin(h5Plugin);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param h5Plugin 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateH5Plugin(H5Plugin h5Plugin) {
        return h5PluginMapper.updateH5Plugin(h5Plugin);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteH5PluginByIds(Long[] ids) {
        return h5PluginMapper.deleteH5PluginByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteH5PluginById(Long id) {
        return h5PluginMapper.deleteH5PluginById(id);
    }
}
