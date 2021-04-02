package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.LiveMount;
import com.qiqilm.server.admin.domain.LiveMsgEngage;
import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.domain.WheelLottery;
import com.qiqilm.server.admin.domain.vo.H5PluginVo;
import com.qiqilm.server.admin.mapper.LiveMountMapper;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class PayCacheListUtil {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private PayTypeMapper payTypeMapper;
	//缓存支付类型list集合
	public void setPayTypeList(List<PayType> payTypes) {
		if(payTypes.isEmpty()){
			redisUtil.unlink("pay:typeList");
		}
		redisUtil.strSet( "pay:typeList",JsonUtil.object2Json(payTypes),Duration.ofHours(6));
	}
	//获取支付类型list集合
	public List<PayType> getTypes() {
		if (!redisUtil.exists("pay:typeList")){
			List<PayType> payTypes = payTypeMapper.selectPayTypeList( null );
			redisUtil.strSet( "pay:typeList",JsonUtil.object2Json(payTypes),Duration.ofHours(6));
		}
		String value = redisUtil.strGet("pay:typeList");
		return StringUtils.isNotBlank(value) ? JsonUtil.json2Array(value, new TypeReference<List<PayType>>() {}) : null;
	}
	//设置新增单个支付类型集合
	public void setPayType(PayType payType) {
		if (redisUtil.exists("pay:payType:"+payType.getId())){
			redisUtil.unlink("pay:payType:"+payType.getId());
		}
		redisUtil.strSet( "pay:payType:"+payType.getId(),String.valueOf(payType),Duration.ofHours(6));
	}
	//获取单个支付类型
	private String getPayType( String payTypeId ) {
		return redisUtil.strGet("pay:payType:"+payTypeId);
	}
	//删除单个支付类型
	public void delPayType( String payTypeId ) {
		redisUtil.unlink("pay:payType:"+payTypeId);
	}
}
