package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.RedisCacheUtil;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveFamily;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.mapper.LiveFamilyMapper;
import com.qiqilm.server.admin.mapper.LiveUserMapper;
import com.qiqilm.server.admin.service.ILiveFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 家族Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class LiveFamilyServiceImpl implements ILiveFamilyService {
	@Autowired
	private LiveFamilyMapper liveFamilyMapper;
	@Autowired
	private LiveUserMapper   liveUserMapper;

	/**
	 * 查询家族
	 *
	 * @param id 家族ID
	 * @return 家族
	 */
	@Override
	public LiveFamily selectLiveFamilyById( Long id ) {
		return liveFamilyMapper.selectLiveFamilyById( id );
	}

	/**
	 * 查询家族列表
	 *
	 * @param liveFamily 家族
	 * @return 家族
	 */
	@Override
	public List<LiveFamily> selectLiveFamilyList( LiveFamily liveFamily ) {
		return liveFamilyMapper.selectLiveFamilyList( liveFamily );
	}

	/**
	 * 新增家族
	 *
	 * @param liveFamily 家族
	 * @return 结果
	 */
	@Override
	public AjaxResult insertLiveFamily( LiveFamily liveFamily ) {
		String     name          = liveFamily.getName();
		LiveFamily getliveFamily = liveFamilyMapper.selectLiveFamilyName( name );
		if ( Objects.nonNull( getliveFamily ) ) {
			return AjaxResult.error( 0, liveFamily.getName() + "，家族已被创建" );
		}
		Long     userId   = liveFamily.getUserId();
		LiveUser liveUser = liveUserMapper.selectLiveUserById( userId );
		if ( Objects.isNull( liveUser ) ) {
			return AjaxResult.error( 0, "主播不存在,无法创建家族" );
		} else {
			if ( liveUser.getIsBan() == 1 ) {
				return AjaxResult.error( 0, "该主播已被禁播,无法创建家族" );
			}
			if ( liveUser.getFamilyId() != 0 ) {
				return AjaxResult.error( 0, "该主播已有家族,无法创建家族" );
			}
			if ( liveUser.getFamilyChieftain() != null && liveUser.getFamilyChieftain() == 1) {
					return AjaxResult.error( 0, "该主播已是家族长,无法再创建家族" );
			}
		}
		liveFamily.setCreateTimes( System.currentTimeMillis() / 1000 );
		liveFamily.setCreateDate( new Date() );
		String a = new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date( System.currentTimeMillis() ) );
		liveFamily.setCreateY( Long.parseLong( a.substring( 0, 4 ) ) );
		liveFamily.setCreateM( Long.parseLong( a.substring( 5, 7 ) ) );
		long b = Long.parseLong( a.substring( 8, 10 ) );
		liveFamily.setCreateD( b );
		liveFamily.setCreateW( ( b / 7 ) + 1 );
		liveFamily.setStatus( 0 );
		liveFamily.setContribution( 0L );
		liveFamily.setFamilyLevel( 1L );
		liveFamily.setVideoTime( 0L );
		liveFamily.setScore( 0L );
		liveFamily.setLiveLevel( 1L );
		liveFamily.setFamilyRecom( ( int ) ( ( Math.random() * 9 + 1 ) * 100000 ) + "" );
		liveFamilyMapper.insertLiveFamily( liveFamily );

		liveUser.setId( userId );
		liveUser.setFamilyId( liveFamily.getId() );
		liveUser.setFamilyChieftain( 1 );
		liveUserMapper.updateLiveUser( liveUser );

		RedisCacheUtil.me.clear( userId, LiveUser.class );
		return AjaxResult.success( "新增" + liveFamily.getName() + "家族成功" );
	}

	/**
	 * 修改家族
	 *
	 * @param liveFamily 家族
	 * @return 结果
	 */
	@Override
	public AjaxResult updateLiveFamily( LiveFamily liveFamily ) {
		LiveFamily getliveFamily = liveFamilyMapper.selectLiveFamilyName( liveFamily.getName() );

		//判断是否封停解封
		if ( liveFamily.getStatus() != null ) {
			if ( liveFamily.getStatus() == 8 ) {
				liveFamily.setStatus( 3 );
				//将此家族下所有主播禁播
				liveFamilyMapper.updateLiveFamily( liveFamily );
				int familyId = new Long( liveFamily.getId() ).intValue();
				liveUserMapper.updateLiveUserIsBanStopByFamilyId( familyId, "家族封停原因:" + liveFamily.getMemo() );
			} else if ( liveFamily.getStatus() == 9 ) {
				liveFamily.setStatus( 1 );
				liveFamilyMapper.updateLiveFamily( liveFamily );
				int familyId = new Long( liveFamily.getId() ).intValue();
				liveUserMapper.updateLiveUserIsBanKeepByFamilyId( familyId, "家族解封原因:" + liveFamily.getMemo() );
			}
		}

		//如果修改了家族长id
		if ( liveFamily.getUserId() != null && !getliveFamily.getUserId().equals( liveFamily.getUserId() ) ) {
			LiveUser liveUser = liveUserMapper.selectLiveUserById( liveFamily.getUserId() );
			if ( Objects.isNull( liveUser ) ) {
				return AjaxResult.error( 0, "主播不存在,无法成为此家族长" );
			} else {
				if ( liveUser.getIsBan() == 1 ) {
					return AjaxResult.error( 0, "该主播已被禁播,无法成为此家族长" );
				}
				if ( liveUser.getFamilyId() != 0 ) {
					return AjaxResult.error( 0, "该主播已有家族,无法成为此家族长" );
				}
				if ( liveUser.getFamilyChieftain() != null ) {
					if ( liveUser.getFamilyChieftain() == 1 ) {
						return AjaxResult.error( 0, "该主播已是家族长,无法再成为此家族长" );
					}
				}
			}

			liveUser.setId( getliveFamily.getUserId() );
			liveUser.setFamilyId( 0L );
			liveUser.setFamilyChieftain( 0 );
			liveUserMapper.updateLiveUser( liveUser );

			LiveUser liveUser1 = new LiveUser();
			liveUser1.setId( liveFamily.getUserId() );
			liveUser1.setFamilyId( getliveFamily.getId() );
			liveUser1.setFamilyChieftain( 1 );
			liveUserMapper.updateLiveUser( liveUser1 );
		}
		liveFamilyMapper.updateLiveFamily( liveFamily );

		RedisCacheUtil.me.clear( getliveFamily.getUserId(), LiveUser.class );
		RedisCacheUtil.me.clear( liveFamily.getUserId(), LiveUser.class );
		return AjaxResult.success( "修改" + liveFamily.getName() + "家族成功" );
	}


	/**
	 * 删除家族信息
	 *
	 * @param id 家族ID
	 * @return 结果
	 */
	@Override
	public int deleteLiveFamilyById( Long id ) {
		LiveFamily liveFamily = liveFamilyMapper.selectLiveFamilyById( id );
		LiveUser   liveUser   = new LiveUser();
		liveUser.setId( liveFamily.getUserId() );
		liveUser.setFamilyId( 0L );
		liveUser.setFamilyChieftain( 0 );
		liveUserMapper.updateLiveUser( liveUser );

		//将此家族下所有主播的family改成0
		int familyId = new Long( id ).intValue();
		liveUserMapper.updateLiveUserByFamilyId( familyId );

		return liveFamilyMapper.updateLiveFamilyStatusById( id );
	}
}
