package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.domain.PayChannelNew;
import com.qiqilm.server.admin.domain.PayPlatformNew;
import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.mapper.PayChannelNewMapper;
import com.qiqilm.server.admin.mapper.PayPlatformNewMapper;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * @author qicheng
 */
@Component
@Log4j2
public class PayCacheUtil {
	public static final String TYPE_LIST = "pay:typeList";
	public static final String TYPE      = "pay:type:";
	public static final String PLATFORM  = "pay:platform:";
	public static final String CHANNEL   = "pay:channel:";
	public static final String CHANNELSUCCESSRATE   = "pay:channelSuccessRate:";

	@Autowired
	private RedisUtil            redisUtil;
	@Autowired
	private PayTypeMapper        payTypeMapper;
	@Autowired
	private PayPlatformNewMapper payPlatformNewMapper;
	@Autowired
	private PayChannelNewMapper  payChannelNewMapper;

	public void clearPayTypeList() {
		redisUtil.unlink( TYPE_LIST );
	}

	public void existsPayType( String payTypeId ) {
		if ( !redisUtil.exists( TYPE + payTypeId ) ) {
			PayType payType = payTypeMapper.selectPayTypeById( payTypeId );
			if ( payType != null ) {
				this.setPayType( payType );
			}
		}
	}

	public void setPayType( PayType payType ) {
		redisUtil.unlink( TYPE + payType.getId() );
		redisUtil.strSet( TYPE + payType.getId(), JsonUtil.object2Json( payType ), Duration.ofHours( 6 ) );
	}

	/**
	 * 获取支付类型缓存
	 *
	 * @param payTypeId 支付类型ID
	 * @return 支付类型
	 */
	public PayType getPayType( String payTypeId ) {
		this.existsPayType( payTypeId );
		String value = redisUtil.strGet( TYPE + payTypeId );
		return StringUtils.isNotBlank( value ) ? JsonUtil.json2Object( value, PayType.class ) : null;
	}

	public void clearPayType( String... payTypeIds ) {
		for ( String payTypeId : payTypeIds ) {
			redisUtil.unlink( TYPE + payTypeId );
		}
	}

	public void existsPayPlatform( Long payPlatformId ) {
		if ( !redisUtil.exists( PLATFORM + payPlatformId ) ) {
			PayPlatformNew payPlatformNew = payPlatformNewMapper.selectPayPlatformNewById( payPlatformId );
			if ( payPlatformNew != null ) {
				this.setPayPlatform( payPlatformNew );
			}
		}
	}

	public void setPayPlatform( PayPlatformNew payPlatformNew ) {
		redisUtil.unlink( PLATFORM + payPlatformNew.getId() );
		redisUtil.strSet( PLATFORM + payPlatformNew.getId(), JsonUtil.object2Json( payPlatformNew ), Duration.ofHours( 6 ) );
	}

	/**
	 * 获取支付平台缓存
	 *
	 * @param payPlatformId 支付平台ID
	 * @return 支付平台
	 */
	public PayPlatformNew getPayPlatform( Long payPlatformId ) {
		this.existsPayPlatform( payPlatformId );
		String value = redisUtil.strGet( PLATFORM + payPlatformId );
		return StringUtils.isNotBlank( value ) ? JsonUtil.json2Object( value, PayPlatformNew.class ) : null;
	}

	public void clearPayPlatform( Long... payPlatformIds ) {
		for ( Long payPlatformId : payPlatformIds ) {
			redisUtil.unlink( PLATFORM + payPlatformId );
		}
	}

	public void existsPayChannel( Long payChannelId ) {
		if ( !redisUtil.exists( CHANNEL + payChannelId ) ) {
			PayChannelNew payChannelNew = payChannelNewMapper.selectPayChannelNewById( payChannelId );
			if ( payChannelNew != null ) {
				this.setPayChannel( payChannelNew );
			}
		}
	}

	public void setPayChannel( PayChannelNew payChannelNew ) {
		redisUtil.unlink( CHANNEL + payChannelNew.getId() );
		redisUtil.strSet( CHANNEL + payChannelNew.getId(), JsonUtil.object2Json( payChannelNew ), Duration.ofHours( 6 ) );
	}

	/**
	 * 获取支付渠道缓存
	 *
	 * @param payChannelId 支付渠道ID
	 * @return 支付渠道
	 */
	public PayChannelNew getPayChannel( Long payChannelId ) {
		this.existsPayChannel( payChannelId );
		String value = redisUtil.strGet( CHANNEL + payChannelId );
		return StringUtils.isNotBlank( value ) ? JsonUtil.json2Object( value, PayChannelNew.class ) : null;
	}

	public void clearPayChannel( Long... payChannelIds ) {
		for ( Long payChannelId : payChannelIds ) {
			redisUtil.unlink( CHANNEL + payChannelId );
		}
	}

    public void setPayChannelSuccessRate(Long id,String successRate) {
		redisUtil.strSet( CHANNELSUCCESSRATE + id, successRate, Duration.ofMinutes( 2 ));
    }

	public String getPayChannelSuccessRate(Long id) {
	 return	redisUtil.strGet( CHANNELSUCCESSRATE + id);
	}
}
