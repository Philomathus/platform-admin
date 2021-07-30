package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberDepositLogMapper;
import com.qiqilm.server.admin.domain.MemberDepositLog;
import com.qiqilm.server.admin.service.IMemberDepositLogService;

/**
 * 人工加分日志Service业务层处理
 *
 * @author 77tv
 * @date 2021-07-29
 */
@Service
public class MemberDepositLogServiceImpl implements IMemberDepositLogService {
    @Autowired
    private MemberDepositLogMapper memberDepositLogMapper;

    /**
     * 查询人工加分日志
     *
     * @param id 人工加分日志ID
     * @return 人工加分日志
     */
    @Override
    public MemberDepositLog selectMemberDepositLogById(Long id) {
        return memberDepositLogMapper.selectMemberDepositLogById(id);
    }

    /**
     * 查询人工加分日志列表
     *
     * @param memberDepositLog 人工加分日志
     * @return 人工加分日志
     */
    @Override
    public List<MemberDepositLog> selectMemberDepositLogList(MemberDepositLog memberDepositLog) {
        return memberDepositLogMapper.selectMemberDepositLogList(memberDepositLog);
    }

    /**
     * 新增人工加分日志
     *
     * @param memberDepositLog 人工加分日志
     * @return 结果
     */
    @Override
    public int insertMemberDepositLog(MemberDepositLog memberDepositLog) {
        return memberDepositLogMapper.insertMemberDepositLog(memberDepositLog);
    }

    /**
     * 修改人工加分日志
     *
     * @param memberDepositLog 人工加分日志
     * @return 结果
     */
    @Override
    public int updateMemberDepositLog(MemberDepositLog memberDepositLog) {
        return memberDepositLogMapper.updateMemberDepositLog(memberDepositLog);
    }

    /**
     * 批量删除人工加分日志
     *
     * @param ids 需要删除的人工加分日志ID
     * @return 结果
     */
    @Override
    public int deleteMemberDepositLogByIds(Long[] ids) {
        return memberDepositLogMapper.deleteMemberDepositLogByIds(ids);
    }

    /**
     * 删除人工加分日志信息
     *
     * @param id 人工加分日志ID
     * @return 结果
     */
    @Override
    public int deleteMemberDepositLogById(Long id) {
        return memberDepositLogMapper.deleteMemberDepositLogById(id);
    }
}
