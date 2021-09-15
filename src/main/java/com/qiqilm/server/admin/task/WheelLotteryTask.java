package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.domain.WheelUserDice;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.WheelDiceConfigMapper;
import com.qiqilm.server.admin.mapper.WheelUserDiceMapper;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@Component
public class WheelLotteryTask {
	@Resource
	private IMemberRechargeLogService memberRechargeLogService;
	@Resource
	private WheelDiceConfigMapper wheelDiceConfigMapper;
	@Resource
	private SysConfigCacheUtil sysConfigCacheUtil;
	@Resource
	private WheelUserDiceMapper wheelUserDiceMapper;
	@Resource
	private RedisUtil redisUtil;

	@Scheduled(cron="0 0 16 * * ?")// 每天16:00点执行一次
	public void cashBackTask() {
		String lottery_wheel_switch = sysConfigCacheUtil.getConf("lottery_wheel_switch","0");
		if(!("1").equals(lottery_wheel_switch)){
			return;
		}
		if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 100 ) ) {
			return;
		}
		//查询昨天公司入款金额
		List<MemberRechargeLog> memberRechargeLogs = memberRechargeLogService.memberRechargeLogLists();
		if (memberRechargeLogs == null || memberRechargeLogs.size() == 0){
			log.warn("昨日没充值数据");
			return;
		}
		long now = System.currentTimeMillis();
		for (MemberRechargeLog memberRechargeLog:memberRechargeLogs){
			//抽奖次数
			Integer diceBycash = wheelDiceConfigMapper.selectWheelDiceBycash(memberRechargeLog.getRechargeMoney());
			if (diceBycash!=null){
				//抽奖次数
				try {
					this.updateLotteryTimes(memberRechargeLog.getMemberId(),diceBycash);
				} catch (Exception e) {
					log.error( e.getMessage(), e );
				}
			}
		}
		log.info("博饼抽奖任务执行时间:{}ms", System.currentTimeMillis() - now);
	}

	private boolean updateLotteryTimes(String userId,Integer times){
		WheelUserDice wheelUserDice=wheelUserDiceMapper.selectWheelUserDiceById(userId);
		WheelUserDice wheelDice=new WheelUserDice();
		wheelDice.setTimes(times);
		if ( wheelUserDice == null) {
			wheelDice.setId(userId);
			return wheelUserDiceMapper.insertWheelUserDice(wheelDice)>0;
		}
		return wheelUserDiceMapper.updateWheelUserDiceTimes(wheelDice)>0;
	}

}
