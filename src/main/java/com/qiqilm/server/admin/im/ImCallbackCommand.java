package com.qiqilm.server.admin.im;

/**
 * @Author kehai
 * @Date 2020/7/10 17:28
 * @Version 1.0
 */
public interface ImCallbackCommand {
	String STATE_CHANGE    = "State.StateChange";
	String JOIN_GROUP      = "Group.CallbackAfterNewMemberJoin";
	String EXIT_GROUP      = "Group.CallbackAfterMemberExit";
	String BEFORE_SEND_MSG = "Group.CallbackBeforeSendMsg";
	String GROUP_FULL      = "Group.CallbackAfterGroupFull";
	String GROUP_DESTROYED = "Group.CallbackAfterGroupDestroyed";

}
