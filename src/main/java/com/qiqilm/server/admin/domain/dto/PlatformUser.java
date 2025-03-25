package com.qiqilm.server.admin.domain.dto;

import com.qiqilm.server.admin.enums.PlatformUserKey;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data

public class PlatformUser {

	private String  userId;

	private String  nickName;

	private String  headImage;

	private Integer status;

	private Integer vip;

	private long time;

	public PlatformUser() {
	}

	public PlatformUser(Map<Object, Object> userInfo ) {
		this.setUserId( userInfo.getOrDefault( PlatformUserKey.USER_ID.getKey(), "" ).toString() );
		this.setNickName( userInfo.getOrDefault( PlatformUserKey.NICK_NAME.getKey(), "" ).toString() );
		this.setHeadImage( userInfo.getOrDefault( PlatformUserKey.HEAD_IMAGE.getKey(), "1" ).toString() );
		this.setStatus( Integer.parseInt( userInfo.getOrDefault( PlatformUserKey.STATUS.getKey(), "1" ).toString() ) );
		this.setVip( Integer.parseInt( userInfo.getOrDefault( PlatformUserKey.VIP.getKey(), "1" ).toString() ) );

	}

	public Map<String, Object> toUserInfoMap() {
		Map<String, Object> userMap = new HashMap<>();
		userMap.put( "user_id", this.getUserId() );
		userMap.put( "nick_name", this.getNickName() );
		userMap.put( "head_image", this.getHeadImage() );
		userMap.put( "user_level", this.getVip() );
		return userMap;
	}


}
