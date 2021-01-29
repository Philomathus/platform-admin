package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.service.IMemberBcodeService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员打码数据Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberBcodeServiceImpl implements IMemberBcodeService {
    @Autowired
    private MemberBcodeMapper memberBcodeMapper;

    /**
     * 查询会员打码数据
     *
     * @param id 会员打码数据ID
     * @return 会员打码数据
     */
    @Override
    public MemberBcode selectMemberBcodeById(String id) {
        return memberBcodeMapper.selectMemberBcodeById(id);
    }

    /**
     * 查询会员打码数据列表
     *
     * @param memberBcode 会员打码数据
     * @return 会员打码数据
     */
    @Override
    public List<MemberBcode> selectMemberBcodeList(MemberBcode memberBcode) {
        return memberBcodeMapper.selectMemberBcodeList(memberBcode);
    }

    /**
     * 新增会员打码数据
     *
     * @param memberBcode 会员打码数据
     * @return 结果
     */
    @Override
    public int insertMemberBcode(MemberBcode memberBcode) {
        memberBcode.setCreateTime(DateUtils.getNowDate());
        return memberBcodeMapper.insertMemberBcode(memberBcode);
    }

    /**
     * 修改会员打码数据
     *
     * @param memberBcode 会员打码数据
     * @return 结果
     */
    @Override
    public int updateMemberBcode(MemberBcode memberBcode) {
        return memberBcodeMapper.updateMemberBcode(memberBcode);
    }

    /**
     * 批量删除会员打码数据
     *
     * @param ids 需要删除的会员打码数据ID
     * @return 结果
     */
    @Override
    public int deleteMemberBcodeByIds(String[] ids) {
        return memberBcodeMapper.deleteMemberBcodeByIds(ids);
    }

    /**
     * 删除会员打码数据信息
     *
     * @param id 会员打码数据ID
     * @return 结果
     */
    @Override
    public int deleteMemberBcodeById(String id) {
        return memberBcodeMapper.deleteMemberBcodeById(id);
    }

    /**
     * 获取全部数据
     *
     * @return {@link AjaxResult}
     */
    @Override
    public AjaxResult getTotalData() {
        Map map = memberBcodeMapper.getTotalData();
        return AjaxResult.success(map);
    }
}
