package com.qiqilm.server.admin.task;

import com.qiqilm.server.admin.domain.ConfigDomain;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.service.IConfigDomainService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.RobotMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Log4j2
public class DomainTask {
	@Autowired
	private RestTemplate         restTemplate;
	@Autowired
	private IConfigDomainService configDomainService;
	@Autowired
	private RobotMessage         robotMessage;

	@Autowired
	private RedisUtil redisUtil;

	@Value( "${spring.profiles.active}" )
	private String profile;

	@Scheduled( cron = "0 */5 * * * ?" )
	public void checkDomain() {

		if(!redisUtil.adminLock(EnumLock.adminTask,getClass().getSimpleName(),120)){
			return;
		}
		if ( "7700".equals( profile ) || "dev".equals( profile ) ) {
			return;
		}
		log.info( "轮询检测域名" + DateUtils.getTime() );
		List<ConfigDomain> list = configDomainService.selectConfigDomainList( null );
		for ( ConfigDomain li : list ) {
			String url;
			if ( li.getDgroup() == 4 ) {
				url = li.getDomain() + "/77ym/7700.txt";
			} else {
				url = li.getDomain() + "/verif/ping";
			}
			boolean a = doGet( url, 1 );
			if ( !a ) {
				String warnText = "域名 " + li.getDomain() + " 检测异常";
				log.error( warnText );
				try {
					robotMessage.sendByChatId( warnText, "-456729891" );
				} catch ( Exception e ) {
					log.error( e.getMessage(), e );
				}
			} else {
				log.info( "域名 " + li.getDomain() + " 检测正常" );
			}
		}
	}

	public boolean doGet( String url, int retryNum ) {
		if ( retryNum > 3 ) {
			log.error( "url:{}访问三次失败，退出重试", url );
			return false;
		}
		try {
			ResponseEntity<String> resultEntity = restTemplate.getForEntity( url, String.class );
			if ( resultEntity.getStatusCode() == HttpStatus.OK ) {
				return true;
			}
		} catch ( Exception e ) {
			log.warn( e.getMessage(), e );
			retryNum++;
			return doGet( url, retryNum );
		}
		return false;
	}
}
