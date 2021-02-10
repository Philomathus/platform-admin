package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.service.IMemberBcodeService;
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
	public MemberBcode selectMemberBcodeById( String id ) {
		return memberBcodeMapper.selectMemberBcodeById( id );
	}

	/**
	 * 查询会员打码数据列表
	 *
	 * @param memberBcode 会员打码数据
	 * @return 会员打码数据
	 */
	@Override
	public List<MemberBcode> selectMemberBcodeList( MemberBcode memberBcode ) {
		if ( memberBcode.getSelectDate() != null ) {
			memberBcode.setStartTime( memberBcode.getSelectDate()[ 0 ] + " 00:00:00" );
			memberBcode.setEndTime( memberBcode.getSelectDate()[ 1 ] + " 23:59:59" );
		}
		return memberBcodeMapper.selectMemberBcodeList( memberBcode );
	}

	/**
	 * 统计
	 *
	 * @return {@link AjaxResult}
	 */
	@Override
	public AjaxResult getTotalData() {
		Map map = memberBcodeMapper.getTotalData();
		return AjaxResult.success( map );
	}
}
