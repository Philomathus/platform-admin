package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.domain.req.ReqMemberOnline;
import com.qiqilm.server.admin.domain.rsp.RspMemberOnline;
import com.qiqilm.server.admin.mapper.MemberOnlineMapper;
import com.qiqilm.server.admin.mapper.MessageSendMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Log4j2
@Component
public class MessageSendCountTask {


	@Autowired
	private MemberCacheManager memberCacheManager;

	@Autowired
	private MessageSendMapper messageSendMapper;

	@Autowired
	private MemberOnlineMapper memberOnlineMapper;
	@Autowired
	private RobotMessage robotMessage;

	//30分钟执行
	@Scheduled( fixedDelay = 1800000, initialDelay = 1 )
	public void runTask() {

		String flag = memberCacheManager.getWebSetVal( "messageBot" );
		if ( flag.equals( "0" ) ) {
			return;
		}
		long now_time=System.currentTimeMillis()/1000 - 360;

		ReqMemberOnline dto =new ReqMemberOnline();
		dto.setNow_time(now_time);

		RspMemberOnline memberOnline = memberOnlineMapper.sumCount(dto);


		Integer count = messageSendMapper.getLiveCount();
		String  text  = "当前在线主播人数:" + count+"\n在线会员数量:"+memberOnline.getCount();
		robotMessage.sendByChatId( text, "-485027924" );

		String  day       = DateFormatUtils.formate( new Date(), "yyyy-MM-dd" );
		String  beginTime = day + " 00:00:00";
		String  endTime   = day + " 23:59:59";
		Integer payCount  = messageSendMapper.getPayCount( beginTime, endTime );

		Integer curCount  = messageSendMapper.getCurCount( beginTime, endTime );

		BigDecimal bigDecimal = BigDecimal.ZERO;
		if (curCount>0) {
			bigDecimal = new BigDecimal( payCount*1.0/curCount*100).setScale(2, BigDecimal.ROUND_HALF_UP);
			String paytext = "近200单充值成功率:" + bigDecimal+"%";
			robotMessage.sendByChatId( paytext, "-485027924" );
		}
	}
}
