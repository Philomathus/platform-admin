package com.qiqilm.server.admin.im;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.cache.ServerImCacheUtil;
import com.qiqilm.server.admin.domain.GroupMemberList;
import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.im.vo.*;
import com.qiqilm.server.admin.im.vo.api.*;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Component
public class ImApiImpl implements ImApi {
	@Autowired
	private RestTemplate      restTemplate;
	@Autowired
	private LiveCacheUtil     liveCacheUtil;
	@Autowired
	private ServerImCacheUtil serverImCacheUtil;

	private String getUrl( String api ) {
		List<String> confs = serverImCacheUtil.getValue( Arrays.asList( "tim_sdkappid", "tim_sdk_key", "tim_identifier" ) );

		String tim_sdkappid   = confs.get( 0 );
		String tim_sdk_key    = confs.get( 1 );
		String tim_identifier = confs.get( 2 );
		String sign           = getIMAdminSign( tim_sdkappid, tim_sdk_key, tim_identifier );

		return General.IM_API + api + "?" + "sdkappid=" + tim_sdkappid +
				"&identifier=" + tim_identifier + "&usersig=" + sign +
				"&random=" + General.randomNum() + "&contenttype=" + General.contenttype;
	}

	@Override
	public Map<String, Object> doPost( BaseFuc fuc ) {
		HttpHeaders      httpHeaders = new HttpHeaders();
		final ObjectNode node        = fuc.get();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		final HttpEntity<ObjectNode> entity = new HttpEntity<>( node, httpHeaders );
		String                       url    = getUrl( fuc.getApi() );
		return restTemplate.postForObject( url, entity, Map.class );
	}

	public Object getString( BaseFuc fuc ) {
		HttpHeaders      httpHeaders = new HttpHeaders();
		final ObjectNode node        = fuc.get();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		final HttpEntity<ObjectNode> entity = new HttpEntity<>( node, httpHeaders );
		String                       url    = getUrl( fuc.getApi() );
		return restTemplate.postForObject( url, entity, Object.class );
	}

	@Override
	public <T extends ImRsp> T doPost( BaseFuc fuc, @NotNull Class<T> clazz, int retryNum ) {
		T t = null;
		if ( retryNum > 3 ) {
			log.error( "url:{}访问三次失败，退出重试", getUrl( fuc.getApi() ) );
			return t;
		}
		try {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType( MediaType.APPLICATION_JSON );
			HttpEntity<ObjectNode> entity = new HttpEntity<>( fuc.get(), httpHeaders );
			String                 url    = getUrl( fuc.getApi() );
			t = restTemplate.postForObject( url, entity, clazz );

			if ( !"OK".equals( t.getActionStatus() ) ) {
				throw new RuntimeException( t.getErrorCode() + t.getErrorInfo() );
			}
			return t;
		} catch ( ResourceAccessException e ) {
			retryNum++;
			return doPost( fuc, clazz, retryNum );
		} catch ( Exception e ) {
			log.error( "url:{},im :{}访问三次失败，退出重试", getUrl( fuc.getApi() ), e.getMessage(), e );
			return null;
		}
	}

	private String getIMAdminSign( String tim_sdkappid, String tim_sdk_key, String identifier ) {

		String singn = liveCacheUtil.getAdminSign( identifier );
		if ( singn == null ) {
			singn = TLSSigAPIv2.genSig( tim_sdkappid, tim_sdk_key, identifier, TimeUnit.DAYS.toSeconds( 365 ) );
			liveCacheUtil.addAdminSign( identifier, singn );
		}

		return singn;
	}

	@Override
	public String getSig( String tim_sdkappid, String tim_sdk_key, String id ) {
		return TLSSigAPIv2.genSig( tim_sdkappid, tim_sdk_key, id, TimeUnit.DAYS.toSeconds( 10 ) );
	}


	@Override
	public boolean register( ImInfo info ) {
		Map<String, Object> map = doPost( info );
		boolean             ok  = "OK".equals( map.get( "ActionStatus" ) );
		if ( !ok ) {
			log.error( "主播注册IM 失败原因res:{}", JsonUtil.object2Json( map ) );
		}
		return ok;
	}

	@Override
	public void mulRegister( String... userId ) {
		if ( userId.length == 0 ) {
			return;
		}
		final MulAccountImport anImport = new MulAccountImport();
		anImport.setAccounts( Arrays.asList( userId ) );
		doPost( anImport );
	}

	@Override
	public void deleteAccount( String... userId ) {
		if ( userId.length == 0 ) {
			return;
		}
		final DeleteAccount account = new DeleteAccount();
		account.setDeleteItem( Arrays.asList( userId ) );
		doPost( account );
	}

	@Override
	public AccountCheckRsp accountCheck( String... userId ) {
		if ( userId.length == 0 ) {
			final AccountCheckRsp checkRsp = new AccountCheckRsp();
			checkRsp.setActionStatus( "FAIL" );
			return checkRsp;
		}
		final CheckAccount checkAccount = new CheckAccount();
		checkAccount.setCheckItem( Arrays.asList( userId ) );
		return doPost( checkAccount, AccountCheckRsp.class, 1 );
	}

	@Override
	public boolean kick( String userId ) {
		final Kick kick = new Kick();
		kick.setIdentifier( userId );
		return "OK".equals( doPost( kick ).get( "ActionStatus" ) );
	}

	@Override
	public boolean nospeakingT(String userId,long timeSec ) {
		final SetNoSpeaking kick = new SetNoSpeaking();
		kick.setIdentifier( userId );
		kick.setTimeSec(timeSec);
		return "OK".equals( doPost( kick ).get( "ActionStatus" ) );
	}

	@Override
	public AccountStatusRsp status( boolean needDetail, String... userId ) {
		if ( userId.length == 0 ) {
			final AccountStatusRsp statusRsp = new AccountStatusRsp();
			statusRsp.setActionStatus( "FAIL" );
			return statusRsp;
		}
		final StatusAccount account = new StatusAccount();
		account.setTo_Account( Arrays.asList( userId ) );
		account.setNeedDetail( needDetail );
		return doPost( account, AccountStatusRsp.class, 1 );
	}

	@Override
	public MessageRsp sendMessage( String sendId, String receiverId, String... msg ) {
		final SendOne sendOne = new SendOne();
		sendOne.setFrom_Account( sendId );
		sendOne.setTo_Account( receiverId );
		final List<ObjectNode> list = Stream.of( msg ).map( MessageType.TIMTextElem::ofNode ).collect( Collectors.toList() );
		sendOne.setMsgBody( list );
		return doPost( sendOne, MessageRsp.class, 1 );
	}

	@Override
	public MessageRsp sendMessage( String sendId, String receiverId, MessageType... msg ) {
		final SendOne sendOne = new SendOne();
		sendOne.setFrom_Account( sendId );
		sendOne.setTo_Account( receiverId );
		final List<ObjectNode> list = Stream.of( msg ).map( MessageType::getNode ).collect( Collectors.toList() );
		sendOne.setMsgBody( list );
		return doPost( sendOne, MessageRsp.class, 1 );
	}

	@Override
	public String createGroup( String admin, GroupType groupType, String groupName ) {
		final CreateGroup group = new CreateGroup();
		group.setName( groupName );
		group.setType( groupType );
		group.setOwner_Account( admin );
		final Map<String, Object> map = doPost( group );

		if ( "OK".equals( map.get( "ActionStatus" ) ) ) {
			return ( String ) map.get( "GroupId" );
		}
		log.error( "创建IM群失败 res:{}", JsonUtil.object2Json( map ) );
		return null;
	}

	@Override
	public List<String> allGroup( String userId ) {
		final GroupList groupList = new GroupList();
		groupList.setMember_Account( userId );
		final Map<String, Object> map = doPost( groupList );
		final List<String> collect =
				( ( List<Map<String, String>> ) map.get( "GroupIdList" ) ).stream().map( e -> e.get( "GroupId" ) ).collect( Collectors.toList() );
		return collect;
	}

	@Override
	public boolean destroyGroup( String groupId ) {
		final DestroyGroup group = new DestroyGroup();
		group.setGroupId( groupId );
		final Map<String, Object> map = doPost( group );
		return "OK".equals( map.get( "ActionStatus" ) );
	}

	//	@Override
	//	public void addGroupMember( String groupId, String... userId ) {
	//		AddGroupMember member = new AddGroupMember();
	//		member.setGroupId( groupId );
	//		member.setMemberList( Arrays.asList( userId ) );
	//		doPost( member );
	//	}

	@Async
	@Override
	public MsgRsp sendSystemNotify( String groupId, String content, String... userId ) {
		SendSystemNotification notification = new SendSystemNotification();
		notification.setGroupId( groupId );
		notification.setContent( content );
		if ( userId.length > 0 ) {
			notification.setMembers( Arrays.asList( userId ) );
		}
		return doPost( notification, MsgRsp.class, 1 );
	}

	@Async
	@Override
	public ImRsp sendGroupMessage( String groupId, String userId, MessageType... message ) {
		SendGroupMsg sendGroupMsg = new SendGroupMsg();
		sendGroupMsg.setGroupId( groupId );
		sendGroupMsg.setMsgBody( Arrays.asList( message ) );
		sendGroupMsg.setFromAccount( userId );
		return doPost( sendGroupMsg, ImRsp.class, 1 );
	}

	@Override
	public String getGroupHistory( String groupId, Integer seq, int size ) {
		MessageHistory history = new MessageHistory();
		history.setGroupId( groupId );
		history.setReqMsgNumber( size );
		history.setReqMsgSeq( seq );

		return JsonUtil.object2Json( getString( history ) );
		// return doPost(history,MessageHistoryRsp.class);
	}


	@Override
	public GroupInfoRsp getGroupInfo( String... groupId ) {
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setGroupIds( Sets.newHashSet( groupId ) );
		return doPost( groupInfo, GroupInfoRsp.class, 1 );
	}

	@Override
	public ForbidListRsp getShutted( String groupId ) {
		ForbidList forbidList = new ForbidList();
		forbidList.setGroupId( groupId );
		return doPost( forbidList, ForbidListRsp.class, 1 );
	}

	@Override
	public ImRsp forbidSendMsg( String groupId, int shutUpTime, String... userId ) {
		ForbidSendMsg forbidSendMsg = new ForbidSendMsg();
		forbidSendMsg.setGroupId( groupId );
		forbidSendMsg.setShutUpTime( shutUpTime );
		forbidSendMsg.setAccounts( userId );
		return doPost( forbidSendMsg, ImRsp.class, 1 );
	}

	@Override
	public OnlineMemberNumRsp getOnlineMemberNum( String groupId ) {
		OnlineMemberNum onlineMemberNum = new OnlineMemberNum();
		onlineMemberNum.setGroupId( groupId );
		return doPost( onlineMemberNum, OnlineMemberNumRsp.class, 1 );
	}

    @Override
    public GroupMemberListRsp getGroupUser(String groupId, PageVO vo) {
        GroupMemberList memberList = new GroupMemberList();
        memberList.setGroupId( groupId );
        memberList.setPageVO( vo );
        return doPost( memberList, GroupMemberListRsp.class, 1 );
    }
}
