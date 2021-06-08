package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.domain.MemberBcode;
import com.qiqilm.server.admin.domain.MemberRechargeLog;
import com.qiqilm.server.admin.mapper.ActivityCashBackMapper;
import com.qiqilm.server.admin.mapper.MemberBcodeMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.IMemberRechargeLogService;
import com.qiqilm.server.admin.utils.UuidUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Log4j2
@Component
public class MemberCashBackTask {
	@Resource
	private IMemberRechargeLogService memberRechargeLogService;
	@Resource
	private ActivityCashBackMapper activityCashBackMapper;
	@Resource
	private MemberBcodeMapper memberBcodeMapper;
	@Resource
	private MemberInfoMapper memberInfoMapper;

	@Scheduled(cron="0 0 16 * * ?")// 每天16:00点执行一次
	public void cashBackTask() {
		try {
			//查询昨天公司入款金额
			List<MemberRechargeLog> memberRechargeLogs = memberRechargeLogService.memberRechargeLogLists();
			for (MemberRechargeLog log:memberRechargeLogs){
				//要返现金额
				Integer bycash = activityCashBackMapper.selectActivityCashBackBycash(log.getRechargeMoney());
				if (bycash!=null){
					//会员返现
					this.updateMemberCharge(log.getMemberId(),new BigDecimal(bycash),"充值返现");
				}
			}

		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
	}

	private boolean updateMemberCharge( String userId, BigDecimal money, String chargeType ) {
		MemberBcode codeFlow = new MemberBcode();
		codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
		codeFlow.setIncome( money );//
		codeFlow.setCreateTime( new Date() );
		codeFlow.setStatus( 0 );
		codeFlow.setCur( BigDecimal.ZERO );
		codeFlow.setUserId( userId );
		codeFlow.setDes( chargeType );
		return memberBcodeMapper.insertMemberBcode( codeFlow ) > 0
				&& memberInfoMapper.updateMoneySelect( userId, money, null, money, null, null ) > 0;
	}
}
