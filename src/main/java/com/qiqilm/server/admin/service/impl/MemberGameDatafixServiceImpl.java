package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberGameDatafixMapper;
import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.service.IMemberGameDatafixService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberGameDatafixServiceImpl implements IMemberGameDatafixService {
    @Autowired
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

}