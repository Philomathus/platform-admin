package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.IMemberBcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 会员打码数据Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberBcodeServiceImpl implements IMemberBcodeService {
	@Resource
	private MemberBcodeMapper memberBcodeMapper;

	@Resource
	private MemberInfoMapper memberInfoMapper;

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
	public AjaxResult getTotalData(MemberBcode memberBcode) {
		if ( memberBcode.getSelectDate() != null ) {
			memberBcode.setStartTime( memberBcode.getSelectDate()[ 0 ] + " 00:00:00" );
			memberBcode.setEndTime( memberBcode.getSelectDate()[ 1 ] + " 23:59:59" );
		}
		MemberBcode memberBcode1 = memberBcodeMapper.getTotalData(memberBcode);
		if (Objects.isNull(memberBcode1)){
			MemberBcode memberBcode2=new MemberBcode();
			memberBcode2.setCountCur(BigDecimal.ZERO);
			memberBcode2.setTotal(BigDecimal.ZERO);
			return AjaxResult.success(memberBcode2);
		}
		return AjaxResult.success( memberBcode1 );
	}

	@Override
	public int updateMemberBcode(MemberBcode memberBcode) {
		BigDecimal add = memberBcode.getCur();
		if(add.compareTo(BigDecimal.ZERO)<0){
			add = BigDecimal.ZERO;
		}
		MemberBcode db = memberBcodeMapper.selectMemberBcodeById(memberBcode.getId());
		if(add.compareTo(db.getIncome())<0){
			memberBcode.setStatus(0);
		}else{
			memberBcode.setStatus(1);
		}
		int c = memberBcodeMapper.updateMemberBcode(memberBcode);
		if(c>0){
			BigDecimal addCode = add.subtract(db.getCur());
			memberInfoMapper.updateBeatCode( db.getUserId(), addCode, addCode );
		}
		return c;
	}
}
