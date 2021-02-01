package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.req.ReqPayJour;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import com.qiqilm.server.admin.mapper.MemberPayJourMapper;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 线上充值信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class MemberPayJourServiceImpl implements IMemberPayJourService {
	@Autowired
	private MemberPayJourMapper memberPayJourMapper;

	/**
	 * 查询线上充值信息
	 *
	 * @param id 线上充值信息ID
	 * @return 线上充值信息
	 */
	@Override
	public MemberPayJour selectMemberPayJourById( String id ) {
		return memberPayJourMapper.selectMemberPayJourById( id );
	}

	/**
	 * 查询线上充值信息列表
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 线上充值信息
	 */
	@Override
	public List<MemberPayJour> selectMemberPayJourList( MemberPayJour memberPayJour ) {
		return memberPayJourMapper.selectMemberPayJourList( memberPayJour );
	}

	@Override
	public List<RspPayJour> findList( ReqPayJour req ) {
		String[] selectDate = req.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			req.setSelectStartDate( selectDate[ 0 ] );
			req.setSelectEndDate( selectDate[ 1 ] );
		}
		return memberPayJourMapper.findList( req );
	}

	/**
	 * 新增线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	@Override
	public int insertMemberPayJour( MemberPayJour memberPayJour ) {
		memberPayJour.setCreateTimes( DateFormatUtils.formate( new Date() ) );
		return memberPayJourMapper.insertMemberPayJour( memberPayJour );
	}

	/**
	 * 修改线上充值信息
	 *
	 * @param memberPayJour 线上充值信息
	 * @return 结果
	 */
	@Override
	public int updateMemberPayJour( MemberPayJour memberPayJour ) {
		return memberPayJourMapper.updateMemberPayJour( memberPayJour );
	}

	@Override
	public Map listCount( ReqPayJour req ) {
		String[] selectDate = req.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			req.setSelectStartDate( selectDate[ 0 ] );
			req.setSelectEndDate( selectDate[ 1 ] );
		}
		return memberPayJourMapper.listCount( req );
	}

	@Override
	public RspPayJour selectById( String id ) {
		return memberPayJourMapper.selectById(id);
	}
}
