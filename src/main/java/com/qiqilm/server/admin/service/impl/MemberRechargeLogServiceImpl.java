package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.mapper.MemberRechargeLogMapper;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberRechargeLogServiceImpl implements IMemberRechargeLogService {
    @Autowired
    private MemberRechargeLogMapper memberRechargeLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberRechargeLog selectMemberRechargeLogById(String id) {
        return memberRechargeLogMapper.selectMemberRechargeLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberRechargeLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberRechargeLog> selectMemberRechargeLogList(MemberRechargeLog memberRechargeLog) {
        String[] selectDate = memberRechargeLog.getSelectDate();
        if (selectDate!=null && selectDate.length>0) {
            memberRechargeLog.setStartDate(selectDate[0]);
            memberRechargeLog.setEndDate(selectDate[1]);
        }
        return memberRechargeLogMapper.selectMemberRechargeLogList(memberRechargeLog);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberRechargeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberRechargeLog(MemberRechargeLog memberRechargeLog) {
        memberRechargeLog.setCreateTime(DateUtils.getNowDate());
        return memberRechargeLogMapper.insertMemberRechargeLog(memberRechargeLog);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberRechargeLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberRechargeLog(MemberRechargeLog memberRechargeLog) {
        memberRechargeLog.setUpdateTime(DateUtils.getNowDate());
        return memberRechargeLogMapper.updateMemberRechargeLog(memberRechargeLog);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberRechargeLogByIds(String[] ids) {
        return memberRechargeLogMapper.deleteMemberRechargeLogByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberRechargeLogById(String id) {
        return memberRechargeLogMapper.deleteMemberRechargeLogById(id);
    }

    @Override
    public Map listCount(MemberRechargeLog memberRechargeLog) {
        String[] selectDate = memberRechargeLog.getSelectDate();
        if (selectDate!=null && selectDate.length>0) {
            memberRechargeLog.setStartDate(selectDate[0]);
            memberRechargeLog.setEndDate(selectDate[1]);
        }
        return memberRechargeLogMapper.listCount(memberRechargeLog);
    }
}
