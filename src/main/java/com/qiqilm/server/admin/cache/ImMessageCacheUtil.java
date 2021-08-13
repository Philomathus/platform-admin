package com.live.common.util.cache;

import com.live.common.Constants;
import com.live.common.util.JsonUtil;
import com.live.common.util.RedisUtil;
import com.live.common.util.im.vo.api.SendGroupMsg;
import com.live.common.util.im.vo.api.SendSystemNotification;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ImMessageCacheUtil {
	@Autowired
	private             RedisUtil redisUtil;
	public static final String    SYSTEM_NOTIFICATION = Constants.redisPRex + "systemSendMessage";
	public static final String    GROUP_MESSAGE       = Constants.redisPRex + "groupSendMessage:";


	public void setImNotifyMessage( SendSystemNotification notification ) {
		//群组消息存缓存里，从右入栈
		redisUtil.lRightPush( SYSTEM_NOTIFICATION, JsonUtil.object2Json( notification ) );
		log.info( "测试缓存数据更新成功" );
	}

	public SendSystemNotification getNotifyMessage() {
		//获取缓存里群组消息，从左出栈，保持先进先出的原则
		String sendGroupMsg = redisUtil.lLeftPop( SYSTEM_NOTIFICATION );
		return JsonUtil.json2Object( sendGroupMsg, SendSystemNotification.class );
	}

	public long getNotifyMessageNum(){
		return redisUtil.lSize( SYSTEM_NOTIFICATION );
	}

	public void setImGroupMessage( SendGroupMsg sendGroupMsg ) {
		//群组消息存缓存里，从右入栈
		redisUtil.lRightPush( GROUP_MESSAGE, JsonUtil.object2Json( sendGroupMsg ) );
	}

	public SendGroupMsg getGroupMessage() {
		//获取缓存里群组消息，从左出栈，保持先进先出的原则
		String sendGroupMsg = redisUtil.lLeftPop( GROUP_MESSAGE );
		log.info( "测试送礼物消息" + sendGroupMsg );
		return JsonUtil.json2Object( sendGroupMsg, SendGroupMsg.class );
	}

	public long getGroupMessageNum(){
		return redisUtil.lSize( GROUP_MESSAGE );
	}
}
