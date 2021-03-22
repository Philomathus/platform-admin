package com.qiqilm.server.admin.service.impl;

import java.util.List;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberOnlineMapper;
import com.qiqilm.server.admin.domain.MemberOnline;
import com.qiqilm.server.admin.service.IMemberOnlineService;

/**
 * 在线会员列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-22
 */
@Service
public class MemberOnlineServiceImpl implements IMemberOnlineService {
    @Autowired
    private MemberOnlineMapper memberOnlineMapper;

    /**
     * 查询在线会员列表列表
     *
     * @param memberOnline 在线会员列表
     * @return 在线会员列表
     */
    @Override
    public List<MemberOnline> selectMemberOnlineList(MemberOnline memberOnline) {
        return memberOnlineMapper.selectMemberOnlineList(memberOnline);
    }

    @Override
    public MemberOnline selectMemberOnlineListCountTotal(MemberOnline memberOnline) {
        return memberOnlineMapper.selectMemberOnlineListCountTotal(memberOnline);
    }
}
