package com.qiqilm.server.admin.service.game;


import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BGService {
    private final String REGISTER = "open.user.create";
    private final String BG = "BG";
    //视讯
    private final String LOGIN = "open.video.game.url";
    //捕鱼
    private final String LOGIN_FISH = "open.game.bg.url";
    //电游
    private final String LOGIN_DIANZI="open.game.bg.egame.url";
    private final String TRANSFER = "open.balance.transfer";
    private final String BALANCE = "open.balance.get";
    private final String RANDOM="659c24ba";
    @Autowired
    private RestTemplate restTemplate;

    public boolean transfer(GamePlatform gamePlatform ,String userId,BigDecimal changeMoney,String sn){
        Map<String,Object> map = new HashMap<>();
        map.put("jsonrpc","2.0");
        map.put("method",TRANSFER);
        map.put("id","2342msn");
        Map<String,Object> params = new HashMap<>();
        params.put("random",RANDOM);
        String secretCode = Base64Utils.encodeToString(DigestUtils.sha1(gamePlatform.getDes())) ;
        String digest=RANDOM+sn+userId+changeMoney+secretCode;
        params.put("digest", DigestUtils.md5Hex(digest));
        params.put("sn",sn);
        params.put("loginId",userId);
        //转账金额(负数表示从BG 转出，正数转入)，支持2位小数
        params.put("amount",changeMoney);
        params.put("checkBizId","1");
        map.put("params",params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(map,httpHeaders);
        String resultMap;
        Map<String, Object> response = null;
        try {
            resultMap = restTemplate.postForObject(gamePlatform.getApiUrl(),httpEntity,String.class);
        }catch (Exception e){
            log.error("BG transfer ->{} {}",e.getMessage(),userId);
            return false;
        }
        response = JsonUtil.json2Map(resultMap);
        if (!CollectionUtils.isEmpty(response)) {
            Object error = response.getOrDefault("error", "");
            if (error==null) {
                return true;
            }
        }
        log.error("BG transfer ->{} {}",userId,JsonUtil.object2Json(resultMap));
        return false;
    }
    public XiaFenResult transfer(GamePlatform gamePlatform, String userId, String sn){
        XiaFenResult result = new XiaFenResult();
        result.setOk(true);
        BigDecimal balance = queryCoin(gamePlatform,userId,sn);
        if ( balance.compareTo(BigDecimal.ZERO) > 0){
            result.setBackMoney(balance);
            //转账金额(负数表示从BG 转出，正数转入)，支持2位小数
            if (transfer(gamePlatform,userId,balance.negate(),sn)) return result;
            result.setOk(false);
            return result;
        }
        result.setBackMoney(BigDecimal.ZERO);
        return result;
    }
    public BigDecimal queryCoin(GamePlatform gamePlatform, String userId,String sn) {
        Map<String,Object> map = new HashMap<>();
        map.put("jsonrpc","2.0");
        map.put("method",BALANCE);
        map.put("id","2342msn");
        Map<String,Object> params = new HashMap<>();
        params.put("random",RANDOM);
        String secretCode = Base64Utils.encodeToString(DigestUtils.sha1(gamePlatform.getDes())) ;
        String digest=RANDOM+sn+userId+secretCode;
        params.put("digest",DigestUtils.md5Hex(digest));
        params.put("sn",sn);
        params.put("loginId",userId);
        map.put("params",params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(map,httpHeaders);
        String resultMap;
        Map<String, Object> response = null;
        try {
            resultMap = restTemplate.postForObject(gamePlatform.getApiUrl(),httpEntity,String.class);
        }catch (Exception e){
            log.error("BG queryCoin ->{} {}",e.getMessage(),userId);
            return BigDecimal.ZERO;
        }
        //查询账户余额
        response = JsonUtil.json2Map(resultMap);
        if (!CollectionUtils.isEmpty(response)) {
            Object error = response.getOrDefault("error", "");
            if (error==null) {
                return new BigDecimal(response.getOrDefault("result", "").toString());
            }
        }
        log.error("BG queryCoin ->{} {}",userId,JsonUtil.object2Json(response));
        return BigDecimal.ZERO;
    }


}
