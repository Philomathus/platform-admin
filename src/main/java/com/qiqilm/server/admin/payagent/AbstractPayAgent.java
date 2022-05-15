package com.qiqilm.server.admin.payagent;

import com.google.common.collect.Sets;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.mapper.MemberWithdrawLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentLogMapper;
import com.qiqilm.server.admin.mapper.PayAgentPlatformMapper;
import com.qiqilm.server.admin.service.IPayAgentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Log4j2
public abstract class AbstractPayAgent implements BasePayAgent {
	@Autowired
	protected PayAgentPlatformMapper  payAgentPlatformMapper;
	@Autowired
	protected MemberWithdrawLogMapper withdrawLogMapper;
	@Autowired
	protected PayAgentLogMapper       payAgentLogMapper;
	@Autowired
	protected RestTemplate            restTemplate;
	@Autowired
	protected IPayAgentService        payAgentService;
	@Autowired
	protected SysConfigCacheUtil      sysConfigCacheUtil;

	protected String assemblyUrl( Map<String, ?> bodyMap ) {
		StringBuilder sb = new StringBuilder();
		bodyMap.forEach( ( k, v ) -> sb.append( k ).append( "=" ).append( v ).append( "&" ) );
		return sb.substring( 0, sb.length() - 1 );
	}

	protected String assemblyUrl2( Map<String, ?> bodyMap ) {
		StringBuilder sb = new StringBuilder();
		bodyMap.forEach( ( k, v ) -> sb.append( v ) );
		return sb.substring( 0, sb.length() - 1 );
	}

	protected boolean checkWhiteIp( String platWhiteIpList, String realIp ) {
		if ( StringUtils.hasText( platWhiteIpList ) ) {
			Set<String> whiteIpSet = Sets.newHashSet( platWhiteIpList.split( "," ) );
			return !whiteIpSet.contains( realIp ) && !"0:0:0:0:0:0:0:1".equals( realIp );
		}
		return false;
	}
}
