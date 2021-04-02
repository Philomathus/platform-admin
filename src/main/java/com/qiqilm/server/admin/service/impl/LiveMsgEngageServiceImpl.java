package com.qiqilm.server.admin.service.impl;

import java.util.List;
import java.util.Objects;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveMsgEngageMapper;
import com.qiqilm.server.admin.domain.LiveMsgEngage;
import com.qiqilm.server.admin.service.ILiveMsgEngageService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-22
 */
@Service
public class LiveMsgEngageServiceImpl implements ILiveMsgEngageService {
    @Autowired
    private LiveMsgEngageMapper liveMsgEngageMapper;
    @Autowired
    private LiveCacheUtil liveCacheUtil;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveMsgEngage selectLiveMsgEngageById(Integer id) {
        return liveMsgEngageMapper.selectLiveMsgEngageById(id);
    }

    /**
     * 查询主播互动列表
     *
     * @param liveMsgEngage
     *
     */
    @Override
    public List<LiveMsgEngage> selectLiveMsgEngageList(LiveMsgEngage liveMsgEngage) {
        return liveMsgEngageMapper.selectLiveMsgEngageList(liveMsgEngage);
    }

    /**
     * 新增
     *
     * @param liveMsgEngage 新增主播互动信息
     * @return 结果
     */
    @Override
    public AjaxResult insertLiveMsgEngage(LiveMsgEngage liveMsgEngage) {
        String msg = liveMsgEngage.getMsg();
        LiveMsgEngage liveMsgEngage1=liveMsgEngageMapper.searchliveMsgEngage(msg);
        if (Objects.nonNull(liveMsgEngage1)){
           return AjaxResult.success(msg+"已新增不能重复");
        }
        List<LiveMsgEngage> msgEngageList=liveMsgEngageMapper.selectLiveMsgEngageList(null);
        if (msgEngageList.size()>=10){
            return AjaxResult.success("主播互动消息只能10条");
        }
        liveMsgEngageMapper.insertLiveMsgEngage(liveMsgEngage);
        List<LiveMsgEngage> liveMsgEngageList=liveMsgEngageMapper.selectLiveMsgEngageList(null);
        liveCacheUtil.setLiveMsgEngage(liveMsgEngageList);
        return AjaxResult.success("成功");
    }

    /**
     * 修改
     *
//     * @param liveMsgEngage修改主播互动信息
     * @return 结果
     */
    @Override
    public AjaxResult updateLiveMsgEngage(LiveMsgEngage liveMsgEngage) {
        LiveMsgEngage liveMsgEngage2=liveMsgEngageMapper.searchliveMsgEngage(liveMsgEngage.getMsg());
        if (Objects.nonNull(liveMsgEngage2)){
          return   AjaxResult.success("互动消息重复");
        }
        liveMsgEngageMapper.updateLiveMsgEngage(liveMsgEngage);
        List<LiveMsgEngage> liveMsgEngageList=liveMsgEngageMapper.selectLiveMsgEngageList( null );
        liveCacheUtil.setLiveMsgEngage(liveMsgEngageList);
        return AjaxResult.success("编辑成功");
    }

    /**
     * 批量删除
     *
     * @param ids 需要删除的主播互动信息
     * @return 结果
     */
    @Override
    public int deleteLiveMsgEngageByIds(Integer[] ids) {
        int i = liveMsgEngageMapper.deleteLiveMsgEngageByIds(ids);
        if(i>0){
            List<LiveMsgEngage> liveMsgEngageList=liveMsgEngageMapper.selectLiveMsgEngageList( null );
            liveCacheUtil.setLiveMsgEngage(liveMsgEngageList);
        }
        return i;
    }

    /**
     * 删除主播互动信息
     *
     * @param id 删除主播互动信息ID
     * @return 结果
     */
    @Override
    public int deleteLiveMsgEngageById(Integer id) {
        int i = liveMsgEngageMapper.deleteLiveMsgEngageById(id);
        if(i>0){
            List<LiveMsgEngage> liveMsgEngageList=liveMsgEngageMapper.selectLiveMsgEngageList( null );
            liveCacheUtil.setLiveMsgEngage(liveMsgEngageList);
        }
        return i;
    }
}