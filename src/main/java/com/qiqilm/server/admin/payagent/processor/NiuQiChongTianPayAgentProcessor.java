package com.qiqilm.server.admin.payagent.processor;


import com.qiqilm.server.admin.constant.ConstantsPayAgent;
import com.qiqilm.server.admin.domain.MemberWithdrawLog;
import com.qiqilm.server.admin.domain.PayAgentLog;
import com.qiqilm.server.admin.domain.PayAgentPlatform;
import com.qiqilm.server.admin.domain.req.ReqPayAgent;
import com.qiqilm.server.admin.enums.BankCodeShunWeiType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.payagent.AbstractPayAgent;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.RoundingMode;
import java.util.*;

@Repository( value = ConstantsPayAgent.NIU_QI_CHONG_TIAN + "PayAgentProcessor" )
@Log4j2
public class NiuQiChongTianPayAgentProcessor extends AbstractPayAgent  {
	@Override
	public boolean orderPay(MemberWithdrawLog withdrawLog, PayAgentPlatform payAgentPlatform, ReqPayAgent reqPayAgent ) throws Exception {
		BankCodeShunWeiType bankCodeType = BankCodeShunWeiType.getCodeByDesc( withdrawLog.getBankName() );
		if ( bankCodeType == null ) {
			log.warn( "此代付无法支持的银行类型 - 银行类型:{}", withdrawLog.getBankName() );
			throw new BusinessException( "此代付无法支持的银行类型：" + withdrawLog.getBankName() );
		}
		withdrawLog.setBankCode( bankCodeType.name() );
		List list=new ArrayList();
		Map mapList=new LinkedHashMap();
		mapList.put( "amount", withdrawLog.getWithdrawMoney().setScale( 0, RoundingMode.HALF_UP ) );
		mapList.put( "accountname ", withdrawLog.getBankUserName() );
		mapList.put( "bankname ", withdrawLog.getBankName() );
		mapList.put( "cardnumber", withdrawLog.getBankAccount() );
		mapList.put( "subbranch","" );
		mapList.put( "province", "");
		mapList.put( "city", "");
		mapList.put( "mobile", "" );
		mapList.put("out_trade_no", withdrawLog.getOrderNo() );
		mapList.put( "attach","" );
		mapList.put( "extends","rrr" );
		list.add(mapList);
		Map<String, Object> dataMap = new TreeMap<>();
		dataMap.put( "mchid", payAgentPlatform.getMerId() );
		dataMap.put( "addtime", System.currentTimeMillis()+"");
		dataMap.put( "bankcode", withdrawLog.getBankCode() );
		dataMap.put( "list", JsonUtil.object2Json(list) );
		dataMap.put( "callback_url", sysConfigCacheUtil.getConf( "payAgentNotifyUrl" ) + ConstantsPayAgent.NIU_QI_CHONG_TIAN );
		String tempStr = this.assemblyUrl( dataMap ) +"&key="+payAgentPlatform.getSignMd5();
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderAddr(), httpEntity, Map.class );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
		if ( !CollectionUtils.isEmpty( resultMap ) ) {
			if ( "success".equals( resultMap.getOrDefault( "status", "" ).toString() )) {
				Map<String, Object> result = ( Map) resultMap.get( "data" );
				String status = result.getOrDefault("status", "").toString();
				String success = result.getOrDefault("success", "").toString();
				if ("1".equals(status)&&"1".equals(success)){
					List<Map<String,Object>> listResult= (List<Map<String, Object>>) result.getOrDefault("list", new ArrayList<>());
					log.info( "代付订单提交成功 - listResult:{}", JsonUtil.object2Json( listResult ) );
					for (Map map:listResult) {
						String outTradeNo = (String) map.getOrDefault("out_trade_no", "");
						String statusRsp = map.getOrDefault("status", "").toString();
						if ("1".equals(statusRsp)&&withdrawLog.getOrderNo().equals(outTradeNo)){
							log.info( "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
							return true;
						}

					}
				}
			}
		}
		log.warn( "代付订单提交失败 - result:{}", JsonUtil.object2Json( resultMap ) );
		return false;
	}

	@Override
	public String callbackPay( PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp ) throws Exception {

		String     rspSign = requestMap.remove("sign").toString();
		SortedMap<String, Object> bodyMap          = new TreeMap<>(requestMap);
		String tempStr = this.assemblyUrl( bodyMap ) +"&key="+payAgentPlatform.getSignMd5();
		log.info( "牛气冲天回调待签名字符串:" + requestMap );
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		if ( ( rspSign ).equalsIgnoreCase( sign ) ) {
			String order_num    = ( String ) requestMap.get( "out_trade_no" );
			String remit_result = ( String ) requestMap.get( "status" );

			MemberWithdrawLog withdrawLog = withdrawLogMapper.selectByOrderNo( order_num );
			if ( withdrawLog == null ) {
				log.error( "提现相关记录丢失 - merOrderNo:{}", order_num );
				return "fail";
			}
			if ( withdrawLog.getStatus() == 6 ) {
				log.error( "已有代付记录 - merOrderNo:{}", order_num );
				return "ok";
			}
			PayAgentLog payAgentLog = payAgentLogMapper.selectByWithdrawOrderNo( order_num );
			payAgentService.processOrderPay( withdrawLog, payAgentLog, "", payAgentPlatform, "2".equals( remit_result ) );
			return "ok";
		}
		return "fail";
	}

	@Override
	public Map<String, Object> reverseCheckOrderPay(PayAgentPlatform payAgentPlatform, Map<String, Object> requestMap, String realIp) throws Exception {
		return null;
	}


	@Override
	public void queryOrderPay( PayAgentLog payAgentLog ) throws Exception {
		MemberWithdrawLog   withdrawLog      = withdrawLogMapper.selectByOrderNo( payAgentLog.getWithdrawOrderNo() );
		PayAgentPlatform    payAgentPlatform = payAgentPlatformMapper.selectPayAgentPlatformById( payAgentLog.getPayAgentPlatId() );
		Map<String, Object> dataMap          = new TreeMap<>();
		dataMap.put( "mchid", payAgentPlatform.getMerId() );
		dataMap.put( "out_trade_no", withdrawLog.getOrderNo() );
		dataMap.put( "applytime", System.currentTimeMillis()+"" );

		String tempStr = this.assemblyUrl( dataMap ) +"&key="+payAgentPlatform.getSignMd5();
		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
		dataMap.put( "sign", sign );

		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
		requestMap.setAll( dataMap );
		log.warn( JsonUtil.object2Json( requestMap ) );
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
		Map<String, Object> resultMap = null;
		try {
			resultMap = restTemplate.postForObject( payAgentPlatform.getPayOrderQueryAddr(), httpEntity, Map.class );
			if (!CollectionUtils.isEmpty(resultMap)&&"1".equals(resultMap.getOrDefault( "status", "" ).toString())){
				String statusCode = String.valueOf( resultMap.getOrDefault( "status", "" ).toString());
				int    status     = 4;
				int    orderState = 0;
				if ( "1".equals( statusCode ) ) {
					status = 6;
					orderState = 1;
				} else {
					status = 5;
					orderState = 2;
				}
				payAgentService.processOrder( payAgentPlatform, withdrawLog, withdrawLog.getUpdateTime(), status, orderState );
				return;
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}

	}

	public static void main(String[] args) {
//		List list=new ArrayList();
//		Map mapList=new LinkedHashMap();
//		mapList.put( "amount", "100.00" );
//		mapList.put( "accountname", "张三" );
//		mapList.put( "bankname", "中国建设银行" );
//		mapList.put( "cardnumber", "623668422000056502" );
//		mapList.put( "subbranch","" );
//		mapList.put( "province", "");
//		mapList.put( "city", "");
//		mapList.put( "mobile", "18092780735" );
//		mapList.put("out_trade_no", "gfjhgddgljfsdflkj3dfg" );
//		mapList.put( "attach","" );
//		mapList.put( "extends","rrr" );
//		list.add(mapList);
//		Map<String, Object> dataMap = new TreeMap<>();
//		dataMap.put( "mchid", "11188" );
//		dataMap.put( "addtime", System.currentTimeMillis()+"");
//		dataMap.put( "bankcode","unionpay" );
//		dataMap.put( "list",JsonUtil.object2Json(list));
//		dataMap.put( "callback_url", "http://df.wuhuifangshinad.com/Payment_index.html" );
//		String tempStr = assemblyUrl( dataMap ) +"&key="+"p5u9xj6d8tkl9m0ctryf1c8oor46k2ix";
//		System.out.println("代签名字符串:"+tempStr);
//		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
//		dataMap.put( "sign", sign );
//		System.out.println("请求参数:"+dataMap);
//
//		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
//		requestMap.setAll( dataMap );
//
//		log.warn( JsonUtil.object2Json( requestMap ) );
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
//		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
//		Map resultMap = null;
//		try {
//			RestTemplate restTemplate=new RestTemplate();
//			resultMap = restTemplate.postForObject( "http://dfapi.wuhuifangshinad.com/Payment_index.html", httpEntity, Map.class );
//			System.out.println("返回参数:"+JsonUtil.object2Json(resultMap));
//		} catch ( Exception e ) {
//			log.error( e.getMessage(), e );
//		}
//		if ( !CollectionUtils.isEmpty( resultMap ) ) {
//			if ( "success".equals( resultMap.getOrDefault( "status", "" ).toString() )) {
//				Map<String, Object> result = ( Map) resultMap.get( "data" );
//				String status = result.getOrDefault("status", "").toString();
//				String success = result.getOrDefault("success", "").toString();
//				if ("1".equals(status)&&"1".equals(success)){
//					log.info( "代付订单提交成功 - result:{}", JsonUtil.object2Json( resultMap ) );
//					System.out.println("1111111111111111111111");
//					List<Map<String,Object>> listResult= (List<Map<String, Object>>) result.getOrDefault("list", new ArrayList<>());
//					for (Map map:listResult) {
//						String outTradeNo = (String) map.getOrDefault("out_trade_no", "");
//						String statusRsp = map.getOrDefault("status", "").toString();
//						if ("1".equals(statusRsp)&&"gfjhgddgljfsdflkj3dfg".equals(outTradeNo)){
//							System.out.println("3333333333333333");
//						}
//
//					}
//					if (listResult.contains("status=1")){
//						System.out.println("22222222222222222222");
//					}
//					System.out.println(listResult);
//
//				}
//			} else {
//				System.out.println("222222222222222222222222");
//			}
//		}
//		Map<String, Object> dataMap          = new TreeMap<>();
//		dataMap.put( "mchid",  "11188"  );
//		dataMap.put( "out_trade_no","gfsrdfddg3dfg" );
//		dataMap.put( "applytime", System.currentTimeMillis()+"" );
//
//		String tempStr =assemblyUrl( dataMap ) +"&key="+"p5u9xj6d8tkl9m0ctryf1c8oor46k2ix";
//		String sign = DigestUtils.md5Hex( tempStr).toUpperCase();
//		dataMap.put( "sign", sign );
//
//		MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
//		requestMap.setAll( dataMap );
//		log.warn( JsonUtil.object2Json( requestMap ) );
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
//		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity( requestMap, httpHeaders );
//		Map<String, Object> resultMap = null;
//		try {
//			RestTemplate restTemplate=new RestTemplate();
//			resultMap = restTemplate.postForObject( "http://dfapi.wuhuifangshinad.com/Payment_dfpay_query.html", httpEntity, Map.class );
//			if (!CollectionUtils.isEmpty(resultMap)&&"1".equals(resultMap.getOrDefault( "status", "" ).toString())){
//				System.out.println("333333333333333");
//			}
//			System.out.println(resultMap);
//		} catch ( Exception e ) {
//			log.error( e.getMessage(), e );
//		}
	}
}
