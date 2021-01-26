package com.qiqilm.server.admin.service.impl;

import java.util.List;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberCardMapper;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.service.IMemberCardService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberCardServiceImpl implements IMemberCardService {
    @Autowired
    private MemberCardMapper memberCardMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberCard selectMemberCardById(String id) {
        return memberCardMapper.selectMemberCardById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberCard 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberCard> selectMemberCardList(MemberCard memberCard) {
        return memberCardMapper.selectMemberCardList(memberCard);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberCard 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberCard(MemberCard memberCard) {
        memberCard.setCreateTime(DateUtils.getNowDate());
        return memberCardMapper.insertMemberCard(memberCard);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberCard 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberCard(MemberCard memberCard) {
        return memberCardMapper.updateMemberCard(memberCard);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberCardByIds(String[] ids) {
        return memberCardMapper.deleteMemberCardByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteMemberCardById(String id) {
        return memberCardMapper.deleteMemberCardById(id);
    }
}
