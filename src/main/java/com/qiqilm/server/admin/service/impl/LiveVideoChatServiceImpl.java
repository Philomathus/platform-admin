package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.MemberForbidUtil;
import com.qiqilm.server.admin.domain.LiveVideoChat;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.SpeakIpBlackList;
import com.qiqilm.server.admin.mapper.LiveVideoChatMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.mapper.SpeakIpBlackListMapper;
import com.qiqilm.server.admin.service.ILiveVideoChatService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 会员发言Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class LiveVideoChatServiceImpl implements ILiveVideoChatService {
	@Autowired
	private LiveVideoChatMapper liveVideoChatMapper;
	@Autowired
	private MemberForbidUtil    memberForbidUtil;

	@Autowired
	private MemberInfoMapper       memberInfoMapper;
	@Autowired
	private SpeakIpBlackListMapper speakIpBlackListMapper;

	/**
	 * 查询会员发言
	 *
	 * @param id 会员发言ID
	 * @return 会员发言
	 */
	@Override
	public LiveVideoChat selectLiveVideoChatById( Long id ) {
		return liveVideoChatMapper.selectLiveVideoChatById( id );
	}

	/**
	 * 查询会员发言列表
	 *
	 * @param liveVideoChat 会员发言
	 * @return 会员发言
	 */
	@Override
	public List<LiveVideoChat> selectLiveVideoChatList( LiveVideoChat liveVideoChat ) {
		return liveVideoChatMapper.selectLiveVideoChatList( liveVideoChat );
	}

	/**
	 * 新增会员发言
	 *
	 * @param liveVideoChat 会员发言
	 * @return 结果
	 */
	@Override
	public int insertLiveVideoChat( LiveVideoChat liveVideoChat ) {
		liveVideoChat.setCreateTime( DateUtils.getNowDate() );
		return liveVideoChatMapper.insertLiveVideoChat( liveVideoChat );
	}

	/**
	 * 修改会员发言
	 *
	 * @param liveVideoChat 会员发言
	 * @return 结果
	 */
	@Override
	public int updateLiveVideoChat( LiveVideoChat liveVideoChat ) {
		return liveVideoChatMapper.updateLiveVideoChat( liveVideoChat );
	}

	/**
	 * 批量删除会员发言
	 *
	 * @param ids 需要删除的会员发言ID
	 * @return 结果
	 */
	@Override
	public int deleteLiveVideoChatByIds( Long[] ids ) {
		return liveVideoChatMapper.deleteLiveVideoChatByIds( ids );
	}

	/**
	 * 删除会员发言信息
	 *
	 * @param id 会员发言ID
	 * @return 结果
	 */
	@Override
	public int deleteLiveVideoChatById( Long id ) {
		return liveVideoChatMapper.deleteLiveVideoChatById( id );
	}

	@Override
	public void setSpeakForbid( List<LiveVideoChat> list ) {
		if ( !list.isEmpty() ) {
			Set<String> pUserIds = new HashSet<>();
			list.forEach( videoChat -> {
				if ( videoChat.getFromPlatform() != null ) {
					pUserIds.add( videoChat.getFromPlatform() );

					long forbidExpire = memberForbidUtil.getUserForbidExpire( videoChat.getFromPlatform() );
					videoChat.setForbid( forbidExpire > 0 );
				}
			} );

			List<String> memberIdList = memberInfoMapper.selectMemberSpeak( pUserIds.toArray( new String[ 0 ] ) );
			for ( String memberId : memberIdList ) {
				for ( LiveVideoChat videoChat : list ) {
					if ( memberId.equals( videoChat.getFromPlatform() ) ) {
						videoChat.setNoSpeaking( true );
					}
				}
			}
		}
	}

	@Override
	public String suspendUser( String pUserId, boolean flag, Integer num, String userIp,String msg,String banAccount ) {
		if ( memberForbidUtil.setPlatformUserSpeak( pUserId, flag ) ) {
			memberInfoMapper.updateSpeak( pUserId, num );
		}
		if ( flag ) {
			SpeakIpBlackList speakIpBlackList = new SpeakIpBlackList();
			speakIpBlackList.setUserId( pUserId );
			speakIpBlackList.setUserIp( userIp );
			speakIpBlackList.setCreateTime( new Date() );
			speakIpBlackList.setMsg("操作人:"+banAccount+",发言内容:"+msg);
			speakIpBlackListMapper.insertSpeakIpBlackList( speakIpBlackList );

			// 封停账号
			MemberInfo update = new MemberInfo();
			update.setId( pUserId );
			update.setSpeak( "1");
			memberInfoMapper.updateMemberInfo( update );
			memberForbidUtil.setPlatformUserSpeak( pUserId, true );
		} else {
			speakIpBlackListMapper.deleteSpeakIp( userIp );

			// 解封账号
			MemberInfo update = new MemberInfo();
			update.setId( pUserId );
			update.setSpeak( "0" );
			memberInfoMapper.updateMemberInfo( update );
			memberForbidUtil.setPlatformUserSpeak( pUserId, false );
		}

		return null;
	}

	@Override
	public void forbidSendMsg( String pUserId, Integer forbidTime, Integer videoId ) {
		memberForbidUtil.setUserForbid( pUserId, videoId, Duration.ofSeconds( forbidTime ) );
	}
}
