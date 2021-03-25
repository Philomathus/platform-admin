package com.qiqilm.server.admin.enums;

/**
 * 交易类型
 */
public enum EnumMoney {
	/*** 非正常业务流程 type<0 **/
	gm(-1,"人工入款"),//人工入款


	/*** 正常业务流程 type>0 **/
	live(0,"直播消费"),
	platform(1,"平台资金切换"),
	charge(2,"线上充值"),//线上充值
	withdrawauto(3,"会员取款"),
	deposit(4,"银行卡充值"),//银行卡充值
	activity(5,"优惠活动"),
	withdraw(6,"人工取款"),
	codeclean(7,"洗码"),
	wongive(8,"赠送彩金"),
	commission(9,"领取佣金"),
	safebox(10,"保险箱记录"),
	settle(11,"结算入款"),
	redpacket(12,"红包"),
	quest(13,"任务奖金"),
	chargegive(14,"充值彩金"),
	bohui(15,"取款驳回"),
	;
	private int    type;
	private String des;

	public int getType() {
		return type;
	}

	public String getDes() {
		return des;
	}

	EnumMoney( int type, String des ) {
		this.type = type;
		this.des = des;
	}

}
