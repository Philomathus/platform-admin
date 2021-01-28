package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.service.IMemberWithdrawLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberWithdrawLogServiceImpl implements IMemberWithdrawLogService {
    @Autowired
    private MemberWithdrawLogMapper memberWithdrawLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberWithdrawLog selectMemberWithdrawLogById(String id) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberWithdrawLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberWithdrawLog> selectMemberWithdrawLogList(MemberWithdrawLog memberWithdrawLog) {
        return memberWithdrawLogMapper.selectMemberWithdrawLogList(memberWithdrawLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberWithdrawLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberWithdrawLog(MemberWithdrawLog memberWithdrawLog) {
        memberWithdrawLog.setCreateTime(DateUtils.getNowDate());
        return memberWithdrawLogMapper.insertMemberWithdrawLog(memberWithdrawLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberWithdrawLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberWithdrawLog(MemberWithdrawLog memberWithdrawLog) {
        memberWithdrawLog.setUpdateTime(DateUtils.getNowDate());
        return memberWithdrawLogMapper.updateMemberWithdrawLog(memberWithdrawLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberWithdrawLogByIds(String[] ids) {
        return memberWithdrawLogMapper.deleteMemberWithdrawLogByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberWithdrawLogById(String id) {
        return memberWithdrawLogMapper.deleteMemberWithdrawLogById(id);
    }
}
