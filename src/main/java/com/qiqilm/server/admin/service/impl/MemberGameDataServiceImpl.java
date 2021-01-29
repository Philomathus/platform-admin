package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员注单数据Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberGameDataServiceImpl implements IMemberGameDataService {
    @Autowired
    private MemberGameDataMapper memberGameDataMapper;

    /**
     * 查询会员注单数据
     *
     * @param id 会员注单数据ID
     * @return 会员注单数据
     */
    @Override
    public MemberGameData selectMemberGameDataById(String id) {
        return memberGameDataMapper.selectMemberGameDataById(id);
    }

    /**
     * 查询会员注单数据列表
     *
     * @param memberGameData 会员注单数据
     * @return 会员注单数据
     */
    @Override
    public List<MemberGameData> selectMemberGameDataList(MemberGameData memberGameData) {
        if (memberGameData.getSelectDate() != null) {
            memberGameData.setStartTime(memberGameData.getSelectDate()[0] + " 00:00:00");
            memberGameData.setEndTime(memberGameData.getSelectDate()[1] + " 23:59:59");
        }
        return memberGameDataMapper.selectMemberGameDataList(memberGameData);
    }

    /**
     * 新增会员注单数据
     *
     * @param memberGameData 会员注单数据
     * @return 结果
     */
    @Override
    public int insertMemberGameData(MemberGameData memberGameData) {
        return memberGameDataMapper.insertMemberGameData(memberGameData);
    }

    /**
     * 修改会员注单数据
     *
     * @param memberGameData 会员注单数据
     * @return 结果
     */
    @Override
    public int updateMemberGameData(MemberGameData memberGameData) {
        return memberGameDataMapper.updateMemberGameData(memberGameData);
    }

    /**
     * 批量删除会员注单数据
     *
     * @param ids 需要删除的会员注单数据ID
     * @return 结果
     */
    @Override
    public int deleteMemberGameDataByIds(String[] ids) {
        return memberGameDataMapper.deleteMemberGameDataByIds(ids);
    }

    /**
     * 删除会员注单数据信息
     *
     * @param id 会员注单数据ID
     * @return 结果
     */
    @Override
    public int deleteMemberGameDataById(String id) {
        return memberGameDataMapper.deleteMemberGameDataById(id);
    }
}
