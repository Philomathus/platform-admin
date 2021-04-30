package com.qiqilm.server.admin.im;

import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.im.vo.*;
import com.qiqilm.server.admin.im.vo.api.ImInfo;

import java.util.List;
import java.util.Map;

public interface ImApi {
	Map<String, Object> doPost( BaseFuc fuc );

	<T extends ImRsp> T doPost( BaseFuc fuc, Class<T> clazz, int retryNum );

	//sig
	String getSig( String tim_sdkappid, String tim_sdk_key, String id );

	//account 成员
	boolean register( ImInfo info ); //注册

	void mulRegister( String... userId );  //多注册

	void deleteAccount( String... userId );  //删除

	AccountCheckRsp accountCheck( String... userId );  //信息

	boolean kick( String userId );  //踢登

	boolean nospeakingT( String userId,Long timeSec );  //禁言

	AccountStatusRsp status( boolean needDetail, String... userId );  //状态

	//message
	MessageRsp sendMessage( String sendId, String receiverId, String... msg ); //单发

	MessageRsp sendMessage( String sendId, String receiverId, MessageType... msg ); //单发

	//group
	String createGroup( String admin, GroupType groupType, String groupName );  //建群

	List<String> allGroup( String userId );       //查所在群

	boolean destroyGroup( String groupId );        //删群

	//	void addGroupMember( String groupId, String... userId );     //添加成员

	MsgRsp sendSystemNotify( String groupId, String content, String... userId );  //群通知

	ImRsp sendGroupMessage( String groupId, String userId, MessageType... message ); //群消息

	Object getGroupHistory( String groupId, Integer msgSeq, int size );  //消息历史

	GroupInfoRsp getGroupInfo( String... groupId ); // 获取群详细资料

	ForbidListRsp getShutted( String groupId ); //获取禁言列表

    UserForbid getUserShutted( String userId ); //获取用户禁言信息

	ImRsp forbidSendMsg( String groupId, int shutUpTime, String... userId );

	OnlineMemberNumRsp getOnlineMemberNum( String groupId ); //获取直播群在线人数

	//history


	//回调相关

    //history
    GroupMemberListRsp getGroupUser( String groupId, PageVO vo );   //组成员
}
