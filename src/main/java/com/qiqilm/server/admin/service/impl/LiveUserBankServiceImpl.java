package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.LiveUserBankMapper;
import com.qiqilm.server.admin.domain.LiveUserBank;
import com.qiqilm.server.admin.service.ILiveUserBankService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-23
 */
@Service
public class LiveUserBankServiceImpl implements ILiveUserBankService {
    @Autowired
    private LiveUserBankMapper liveUserBankMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveUserBank selectLiveUserBankById(Long id) {
        return liveUserBankMapper.selectLiveUserBankById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveUserBank 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveUserBank> selectLiveUserBankList(LiveUserBank liveUserBank) {
        return liveUserBankMapper.selectLiveUserBankList(liveUserBank);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveUserBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveUserBank(LiveUserBank liveUserBank) {
        liveUserBank.setCreateTime(DateUtils.getNowDate());
        return liveUserBankMapper.insertLiveUserBank(liveUserBank);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveUserBank 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveUserBank(LiveUserBank liveUserBank) {
        return liveUserBankMapper.updateLiveUserBank(liveUserBank);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserBankByIds(Long[] ids) {
        return liveUserBankMapper.deleteLiveUserBankByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserBankById(Long id) {
        return liveUserBankMapper.deleteLiveUserBankById(id);
    }

    @Override
    public List<LiveUserBank> getBankCardInfo() {
       return liveUserBankMapper.getLiveUserBankInfo();
    }
}