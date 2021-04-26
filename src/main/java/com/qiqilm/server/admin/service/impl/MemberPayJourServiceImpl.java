package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberPayJour;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.domain.rsp.RspPayJour;
import com.qiqilm.server.admin.mapper.MemberPayJourMapper;
import com.qiqilm.server.admin.mapper.PayChannelNewMapper;
import com.qiqilm.server.admin.service.IMemberPayJourService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	private PayChannelNewMapper payChannelNewMapper;

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
		String[] selectDate = memberPayJour.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			memberPayJour.setSelectStartDate( selectDate[ 0 ] );
			memberPayJour.setSelectEndDate( selectDate[ 1 ] );
		}
		List<MemberPayJour> memberPayJours = memberPayJourMapper.selectMemberPayJourList( memberPayJour );
		List<PayChannelNew> payChannelNews = payChannelNewMapper.selectPayChannelName();
		for ( MemberPayJour me : memberPayJours ) {
			for ( PayChannelNew pa : payChannelNews ) {
				if ( me.getChannelId().equals( String.valueOf( pa.getId() ) ) ) {
					me.setPlatformName( pa.getPayPlatformName() );
					me.setChannelName( pa.getName() );
					me.setPayRate( pa.getPayRate() );
				}
			}
		}
		return memberPayJours;
	}

	@Override
	public Map listCount( MemberPayJour memberPayJour ) {
		String[] selectDate = memberPayJour.getSelectDate();
		if ( selectDate != null && selectDate.length > 0 ) {
			memberPayJour.setSelectStartDate( selectDate[ 0 ] );
			memberPayJour.setSelectEndDate( selectDate[ 1 ] );
		}
		return memberPayJourMapper.listCount( memberPayJour );
	}

	@Override
	public List<RspPayJour> selectMemberPayJourLists(MemberPayJour memberPayJour) {
		return memberPayJourMapper.selectMemberPayJourLists(memberPayJour);
	}

	@Override
	public Map listCounts(MemberPayJour memberPayJour) {
		return memberPayJourMapper.listCounts(memberPayJour);
	}
}
