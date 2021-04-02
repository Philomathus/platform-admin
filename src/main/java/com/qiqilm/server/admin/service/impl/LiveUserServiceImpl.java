package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.mapper.LiveFamilyMapper;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 主播用户信息Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveUserServiceImpl implements ILiveUserService {
	@Autowired
	private LiveUserMapper   liveUserMapper;
	@Autowired
	private LiveFamilyMapper liveFamilyMapper;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;

	/**
	 * 查询主播用户信息
	 *
	 * @param id 主播用户信息ID
	 * @return 主播用户信息
	 */
	@Override
	public LiveUser selectLiveUserById( Long id ) {
		LiveUser liveUser = liveUserMapper.selectLiveUserById( id );
		if ( liveUser != null ) {
			if ( StringUtils.isNotBlank( liveUser.getMobile() ) ) {
				liveUser.setMobile( new StringBuilder( liveUser.getMobile() ).replace( 3, 7, "****" ).toString() );
			}
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			if ( StringUtils.isNotBlank( liveUser.getIdentifyHoldImage() )
					&& !liveUser.getIdentifyHoldImage().startsWith( "http" ) ) {
				liveUser.setIdentifyHoldImage( domainValue + liveUser.getIdentifyHoldImage() );
			}
			if ( StringUtils.isNotBlank( liveUser.getIdentifyNagativeImage() )
					&& !liveUser.getIdentifyNagativeImage().startsWith( "http" ) ) {
				liveUser.setIdentifyNagativeImage( domainValue + liveUser.getIdentifyNagativeImage() );
			}
			if ( StringUtils.isNotBlank( liveUser.getIdentifyPositiveImage() )
					&& !liveUser.getIdentifyPositiveImage().startsWith( "http" ) ) {
				liveUser.setIdentifyPositiveImage( domainValue + liveUser.getIdentifyPositiveImage() );
			}
		}
		return liveUser;
	}

	/**
	 * 查询主播用户信息列表
	 *
	 * @param liveUser 主播用户信息
	 * @return 主播用户信息
	 */
	@Override
	public List<LiveUser> selectLiveUserList( LiveUser liveUser ) {
		List<LiveUser> liveUsers = liveUserMapper.selectLiveUserList( liveUser );
		for ( LiveUser user : liveUsers ) {
			if ( StringUtils.isNotBlank( user.getMobile() ) ) {
				user.setMobile( new StringBuilder( user.getMobile() ).replace( 3, 7, "****" ).toString() );
			}
		}
		return liveUsers;
	}

	/**
	 * 修改主播用户信息
	 *
	 * @param liveUser 主播用户信息
	 * @return 结果
	 */
	@Override
	public int updateLiveUser( LiveUser liveUser ) {
		liveUser.setUpdateTime( DateUtils.getNowDate() );
		return liveUserMapper.updateLiveUser( liveUser );
	}

	@Override
	public AjaxResult updateFamilyID( Long familyID, Long userId ) {
		LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById( familyID );
		if ( liveFamily != null || familyID == 0 ) {
			if ( familyID == 0 ) {
				int oldFamilyId = liveUserMapper.getFamilyId( userId );
				int i           = liveUserMapper.updateFamilyID( familyID, userId );
				int num         = liveUserMapper.getNumFamily( oldFamilyId );
				liveFamilyMapper.updateFamilyID( num, oldFamilyId );
			} else {
				int oldFamilyId = liveUserMapper.getFamilyId( userId );
				int i           = liveUserMapper.updateFamilyID( familyID, userId );
				int num         = liveUserMapper.getNumFamily( oldFamilyId );
				liveFamilyMapper.updateFamilyID( num, oldFamilyId );
				int newnum = liveUserMapper.getNumFamily( familyID.intValue() );
				liveFamilyMapper.updateFamilyID( newnum, familyID.intValue() );
			}
			RedisCacheUtil.me.clear( userId, LiveUser.class );
			return AjaxResult.success();

		}
		return AjaxResult.error();
	}

	@Override
	public List<RspLotteryBet> selectAnchorAward( ReqLotteryBat req ) {
		return liveUserMapper.selectAnchorAward( req );
	}
}
