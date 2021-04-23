package com.qiqilm.server.admin.task;


import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LiveUserBank;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.ILiveUserBankService;
import com.qiqilm.server.admin.service.IMemberCardService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
public class BankCardAddressTask {

	@Autowired
	private IMemberCardService   memberCardService;
	@Autowired
	private ILiveUserBankService liveUserBankService;

	@Autowired
	private RedisUtil          redisUtil;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;

	@Scheduled( fixedDelay = 60000, initialDelay = 1 )
	public void runTask() {
		if ( !redisUtil.adminLock( EnumLock.adminTask, getClass().getSimpleName(), 59 ) ) {
			return;
		}
		log.warn( "开始执行银行归属地查询" );
		List<MemberCard> listMemberCard = memberCardService.getBankCardInfo();
		String           apiUrl         = sysConfigCacheUtil.getConf( "bank_address_ip_url" );
		for ( MemberCard m : listMemberCard ) {
			String bankAccount = m.getBankAccount();
			String bankAddress = getRealBankAddress( bankAccount, apiUrl );
            if (StringUtils.isEmpty(bankAddress)) {
                bankAddress = "未知";
            }
            m.setRealBankAddress( bankAddress );
		}
		memberCardService.updateMemberCardList( listMemberCard );
		log.warn( "开始执行银行归属地查询成功 - 成功数量：{}", listMemberCard.size() );
	}

	// @Scheduled( fixedDelay = 120000, initialDelay = 1 )
	public void runTaskLive() {
		List<LiveUserBank> listMemberCard = liveUserBankService.getBankCardInfo();
		String             apiUrl         = sysConfigCacheUtil.getConf( "bank_address_ip_url" );
		for ( LiveUserBank m : listMemberCard ) {
			String bankAccount = m.getBankAccount();
			String bankAddress = getRealBankAddress( bankAccount, apiUrl );
			m.setRealBankAddress( bankAddress );
			liveUserBankService.updateLiveUserBank( m );
		}
	}

	private String getRealBankAddress( String bankAccount, String apiUrl ) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity   httpEntity   = new HttpEntity( bankAccount, httpHeaders );
		RestTemplate restTemplate = new RestTemplate();
		String       result       = restTemplate.postForObject( apiUrl, httpEntity, String.class );
		if ( Strings.isNotBlank( result ) ) {
			Map    map         = JsonUtil.json2Map( result );
			Map    dataMap     = ( Map ) map.get( "data" );
            if (Objects.isNull(dataMap)) {
                return null;
            }else {
			String bankAddress = dataMap.getOrDefault( "bankAddress", "账号非法" ).toString();
			return bankAddress;}
		}
		return null;
	}
}
