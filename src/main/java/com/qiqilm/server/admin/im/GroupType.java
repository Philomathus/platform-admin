package com.qiqilm.server.admin.im;

/*public interface GroupType {
    // Private/Public/ChatRoom/AVChatRoom/BChatRoom
    String PRIVATE = "Private";
    String PUBLIC = "Public";
    String CHAT_ROOM = "ChatRoom";
    String AV_CHART_ROOM = "AVChatRoom";
    String B_CHAT_ROOM = "BChatRoom";
}*/

public enum GroupType {
	//
	PRIVATE( "Private" ),
	PUBLIC( "Public" ),
	CHAT_ROOM( "Public" ),
	AV_CHART_ROOM( "AVChatRoom" ),
	B_CHAT_ROOM( "BChatRoom" );

	private String val;

	GroupType( String str ) {
		val = str;
	}

	@Override
	public String toString() {
		return val;
	}
}
