package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveProplog;
import com.qiqilm.server.admin.mapper.LiveProplogMapper;
import com.qiqilm.server.admin.service.ILiveProplogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 用户送礼日志Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class LiveProplogServiceImpl implements ILiveProplogService {
    @Autowired
    private LiveProplogMapper liveProplogMapper;
    @Value( "${spring.profiles.active}" )
    private String profile;

    /**
     * 查询用户送礼日志
     *
     * @param id 用户送礼日志ID
     * @return 用户送礼日志
     */
    @Override
    public LiveProplog selectLiveProplogById(Long id) {
        return liveProplogMapper.selectLiveProplogById(id);
    }

    /**
     * 查询用户送礼日志列表
     *
     * @param liveProplog 用户送礼日志
     * @return 用户送礼日志
     */
    @Override
    public List<LiveProplog> selectLiveProplogList(LiveProplog liveProplog) {
        if (liveProplog.getSelectDate() != null) {
            liveProplog.setStartTime(liveProplog.getSelectDate()[0] + " 00:00:00");
            liveProplog.setEndTime(liveProplog.getSelectDate()[1] + " 23:59:59");
        }
        List<LiveProplog> liveProplogs = null;
        if("7706".equals(profile)){
            liveProplogs = liveProplogMapper.selectLiveProplogList7706(liveProplog);
        } else {
            liveProplogs = liveProplogMapper.selectLiveProplogList(liveProplog);
        }
        return liveProplogs;
    }

    /**
     * 新增用户送礼日志
     *
     * @param liveProplog 用户送礼日志
     * @return 结果
     */
    @Override
    public int insertLiveProplog(LiveProplog liveProplog) {
        return liveProplogMapper.insertLiveProplog(liveProplog);
    }

    /**
     * 修改用户送礼日志
     *
     * @param liveProplog 用户送礼日志
     * @return 结果
     */
    @Override
    public int updateLiveProplog(LiveProplog liveProplog) {
        return liveProplogMapper.updateLiveProplog(liveProplog);
    }

    /**
     * 批量删除用户送礼日志
     *
     * @param ids 需要删除的用户送礼日志ID
     * @return 结果
     */
    @Override
    public int deleteLiveProplogByIds(Long[] ids) {
        return liveProplogMapper.deleteLiveProplogByIds(ids);
    }

    /**
     * 删除用户送礼日志信息
     *
     * @param id 用户送礼日志ID
     * @return 结果
     */
    @Override
    public int deleteLiveProplogById(Long id) {
        return liveProplogMapper.deleteLiveProplogById(id);
    }

    @Override
    public AjaxResult getCount(LiveProplog liveProplog) {
        if (liveProplog.getSelectDate() != null) {
            liveProplog.setStartTime(liveProplog.getSelectDate()[0] + " 00:00:00");
            liveProplog.setEndTime(liveProplog.getSelectDate()[1] + " 23:59:59");
        }
       LiveProplog liveProplog1= liveProplogMapper.countPropMoney(liveProplog);
       if (Objects.isNull(liveProplog1)){
           LiveProplog liveProplog2=new LiveProplog();
           liveProplog2.setTotalPorp(BigDecimal.ZERO);
           return AjaxResult.success(liveProplog2);
       }
        return AjaxResult.success(liveProplog1);
    }
}
