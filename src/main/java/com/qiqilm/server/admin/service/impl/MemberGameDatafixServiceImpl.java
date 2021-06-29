package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.mapper.MemberGameDatafixMapper;
import com.qiqilm.server.admin.service.IMemberGameDatafixService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberGameDatafixServiceImpl implements IMemberGameDatafixService {
    @Resource
    private MemberGameDatafixMapper memberGameDatafixMapper;


    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberGameDatafix 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberGameDatafix> selectMemberGameDatafixList(MemberGameDatafix memberGameDatafix) {
        return memberGameDatafixMapper.selectMemberGameDatafixList(memberGameDatafix);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberGameDatafix 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberGameDatafix(MemberGameDatafix memberGameDatafix) {
        return memberGameDatafixMapper.insertMemberGameDatafix(memberGameDatafix);
    }

    @Override
    public int deleteMemberGameDatafixByIds(String[] ids) {
        return memberGameDatafixMapper.deleteMemberGameDatafixByIds(ids);
    }

    @Override
    public int deleteMemberGameDatafixById(String id) {
        return memberGameDatafixMapper.deleteMemberGameDatafixById(id);
    }

    @Override
    public MemberGameDatafix selectMemberGameDatafixById(String id) {
        return memberGameDatafixMapper.selectMemberGameDatafixById(id);
    }

}