package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.domain.SysDictData;
import com.qiqilm.server.admin.mapper.ConfigEnvironmentMapper;
import com.qiqilm.server.admin.mapper.SysDictDataMapper;
import com.qiqilm.server.admin.service.IConfigEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class ConfigEnvironmentServiceImpl implements IConfigEnvironmentService {
    @Autowired
    private ConfigEnvironmentMapper configEnvironmentMapper;
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param envCode 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigEnvironment selectConfigEnvironmentById(String envCode) {
        return configEnvironmentMapper.selectConfigEnvironmentById(envCode);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigEnvironment> selectConfigEnvironmentList(ConfigEnvironment configEnvironment) {
        return configEnvironmentMapper.selectConfigEnvironmentList(configEnvironment);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public AjaxResult insertConfigEnvironment(ConfigEnvironment configEnvironment) {
        if (configEnvironment.getMenuType().equals("M")) {
            //判断名称是否存在
            if (configEnvironmentMapper.checkType(configEnvironment.getEnvTitle())!=0){
                return AjaxResult.error("名称已存在");
            }
            //判断编码是否存在
            if (configEnvironmentMapper.checkCode(configEnvironment.getEnvCode())!=0){
                return AjaxResult.error("编码已存在");
            }
            SysDictData dictData = new SysDictData();
            dictData.setDictSort(configEnvironment.getEnvSort());
            dictData.setDictLabel(configEnvironment.getEnvTitle());
            dictData.setDictValue(configEnvironmentMapper.getValue());
            dictData.setDictType("config_environment_group");
            dictData.setStatus(String.valueOf(0));
            return toAjax(dictDataMapper.insertDictData(dictData));
        }else {
            //判断名称是否存在
            if (configEnvironmentMapper.checkType2(configEnvironment.getEnvTitle())!=0){
                return AjaxResult.error("名称已存在");
            }
            //判断编码是否存在
            if (configEnvironmentMapper.checkCode2(configEnvironment.getEnvCode())!=0){
                return AjaxResult.error("编码已存在");
            }
            return toAjax(configEnvironmentMapper.insertConfigEnvironment(configEnvironment));
        }
    }
    public AjaxResult toAjax( int rows ) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
    /**
     * 修改【请填写功能名称】
     *
     * @param configEnvironment 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigEnvironment(ConfigEnvironment configEnvironment) {
        return configEnvironmentMapper.updateConfigEnvironment(configEnvironment);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param envCodes 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigEnvironmentByIds(String[] envCodes) {
        return configEnvironmentMapper.deleteConfigEnvironmentByIds(envCodes);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param envCode 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigEnvironmentById(String envCode) {
        return configEnvironmentMapper.deleteConfigEnvironmentById(envCode);
    }

    @Override
    public AjaxResult getTitleIndex(String title, String code) {
        Integer index = configEnvironmentMapper.getTitleIndex(title,code);
        return AjaxResult.success(index);
    }
}
