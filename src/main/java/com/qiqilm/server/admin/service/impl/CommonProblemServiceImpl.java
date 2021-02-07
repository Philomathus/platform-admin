package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.CommonProblemMapper;
import com.qiqilm.server.admin.domain.CommonProblem;
import com.qiqilm.server.admin.service.ICommonProblemService;

/**
 * 常见问题Service业务层处理
 *
 * @author 77tv
 * @date 2021-02-07
 */
@Service
public class CommonProblemServiceImpl implements ICommonProblemService {
    @Autowired
    private CommonProblemMapper commonProblemMapper;

    /**
     * 查询常见问题
     *
     * @param id 常见问题ID
     * @return 常见问题
     */
    @Override
    public CommonProblem selectCommonProblemById(String id) {
        return commonProblemMapper.selectCommonProblemById(id);
    }

    /**
     * 查询常见问题列表
     *
     * @param commonProblem 常见问题
     * @return 常见问题
     */
    @Override
    public List<CommonProblem> selectCommonProblemList(CommonProblem commonProblem) {
        return commonProblemMapper.selectCommonProblemList(commonProblem);
    }

    /**
     * 新增常见问题
     *
     * @param commonProblem 常见问题
     * @return 结果
     */
    @Override
    public int insertCommonProblem(CommonProblem commonProblem) {
        return commonProblemMapper.insertCommonProblem(commonProblem);
    }

    /**
     * 修改常见问题
     *
     * @param commonProblem 常见问题
     * @return 结果
     */
    @Override
    public int updateCommonProblem(CommonProblem commonProblem) {
        return commonProblemMapper.updateCommonProblem(commonProblem);
    }

    /**
     * 批量删除常见问题
     *
     * @param ids 需要删除的常见问题ID
     * @return 结果
     */
    @Override
    public int deleteCommonProblemByIds(String[] ids) {
        return commonProblemMapper.deleteCommonProblemByIds(ids);
    }

    /**
     * 删除常见问题信息
     *
     * @param id 常见问题ID
     * @return 结果
     */
    @Override
    public int deleteCommonProblemById(String id) {
        return commonProblemMapper.deleteCommonProblemById(id);
    }
}
