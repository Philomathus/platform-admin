package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberDirtyWordsMapper;
import com.qiqilm.server.admin.domain.MemberDirtyWords;
import com.qiqilm.server.admin.service.IMemberDirtyWordsService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberDirtyWordsServiceImpl implements IMemberDirtyWordsService {
    @Autowired
    private MemberDirtyWordsMapper memberDirtyWordsMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public MemberDirtyWords selectMemberDirtyWordsById() {
        return memberDirtyWordsMapper.selectMemberDirtyWordsById();
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param memberDirtyWords 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateMemberDirtyWords(MemberDirtyWords memberDirtyWords) {
        return memberDirtyWordsMapper.updateMemberDirtyWords(memberDirtyWords);
    }

}