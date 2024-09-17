package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberMoney;

import java.math.BigDecimal;
import java.util.List;

/**
 * 派送彩金暂存表Mapper接口
 *
 * @author 77tv
 * @date 2022-02-09
 */
public interface MemberMoneyMapper {
    /**
     * 查询派送彩金暂存表
     *
     * @param memberId 派送彩金暂存表ID
     *
     * @return 派送彩金暂存表
     */
    public MemberMoney selectMemberMoneyById( String memberId );

    /**
     * 查询派送彩金暂存表列表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 派送彩金暂存表集合
     */
    public List<MemberMoney> selectMemberMoneyList( MemberMoney memberMoney );

    /**
     * 新增派送彩金暂存表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 结果
     */
    public int insertMemberMoney( MemberMoney memberMoney );

    /**
     * 修改派送彩金暂存表
     *
     * @param memberMoney 派送彩金暂存表
     *
     * @return 结果
     */
    public int updateMemberMoney( MemberMoney memberMoney );

    /**
     * 删除派送彩金暂存表
     *
     * @param memberId 派送彩金暂存表ID
     *
     * @return 结果
     */
    public int deleteMemberMoneyById( String memberId );

    /**
     * 批量删除派送彩金暂存表
     *
     * @param memberIds 需要删除的数据ID
     *
     * @return 结果
     */
    public int deleteMemberMoneyByIds( String[] memberIds );

    public BigDecimal countMoney();

    int insertBatch( List<MemberMoney> memberMoneyList );

    int clear();
}
