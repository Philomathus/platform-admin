package com.qiqilm.server.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

@Log4j2
public class RequestParamData {

    private static final String RE_KY_DETAIL_RECORD_S1 = "agent=%s&timestamp=%s&param=%s&key=%s";
    private static final String RE_KY_DETAIL_RECORD_S2 = "s=%s&startTime=%s&endTime=%s";
    private static final String RE_KY_DETAIL_RECORD_S3 = "s=%s&kindID=%s&recordID=%s&account=%s";
    private static final String RE_MT_DETAIL_RECORD_S1 = "/%s/%s/%s";
    private static final String RE_XSJ_DETAIL_RECORD_S1 = "agent=%s&timestamp=%s&param=%s&key=%s";
    private static final String RE_XSJ_DETAIL_RECORD_S2 = "s=%s&gameuserno=%s&id=%s&account=%s&serverID=%s";

    //开元棋牌 - 对局详情 返回参数
    public static String requestKYBetRecord(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String getURL = getBetURLByKXOrKY(memberGameData, gamePlatform);
        log.info( "开元棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }

    //开元棋牌 - 对局明细 返回参数
    public static String requestKYBetDetail(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String getURL = getBetDetailURLByKXOrKY(memberGameData,gamePlatform);
        log.info( "开元棋牌-对局详情-请求参数：{}",getURL);
        return PostData.get(getURL);
    }
    //凯旋棋牌 - 对局详情 返回参数
    public static String requestKXBetRecord(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String getURL = getBetURLByKXOrKY(memberGameData, gamePlatform);
        log.info( "凯旋棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }

    //凯旋棋牌 - 对局明细 返回参数
    public static String requestKXBetDetail(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String getURL = getBetDetailURLByKXOrKY(memberGameData, gamePlatform);
        log.info( "凯旋棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }
    //新世界棋牌 - 对局明细 返回参数
    public static String requestXSJBetDetail(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String getURL = getBetDetailURLByXSJ(memberGameData, gamePlatform);
        log.info( "新世界棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }

    //美天棋牌 - 投注详情 返回参数
    public static String requestMTBetRecord(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        Map<String,String> data = new LinkedHashMap<>();
        data.put("rowID",memberGameData.getGameId());
        data.put("lang","ZH-CN");
        String merchantId = gamePlatform.getAgent();
        String code = DigestUtils.md5Hex(gamePlatform.getMd5()+JSON.toJSONString(data));
        String s1 = String.format(RE_MT_DETAIL_RECORD_S1,merchantId,code,Base64.getEncoder().encodeToString(JsonUtil.object2Json(data).getBytes()));
        String apiUrl = gamePlatform.getRecordUrl();
        String getURL = apiUrl+s1;
        log.info( "美天棋牌-对局详情-请求参数：{}",getURL);
        return PostData.post(getURL);
    }

    //凯旋棋牌|开元棋牌|新世界 对局列表 暂时共享
    public static String getBetURLByKXOrKY(MemberGameData memberGameData, GamePlatform gamePlatform)throws Exception {
        String agent = memberGameData.getAgent();
        Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS,memberGameData.getGameEndTime());
        Date nowTime = new Date();
        String md5 = gamePlatform.getMd5();
        String key = DigestUtils.md5Hex(agent + nowTime.getTime() + md5);
        String s1 = String.format(RE_KY_DETAIL_RECORD_S1,agent,nowTime.getTime(),"{0}",key);
        String s2 = String.format(RE_KY_DETAIL_RECORD_S2,"9",endTime.getTime(),endTime.getTime());
        String s3 = Encrypt.AESEncrypt(s2,gamePlatform.getDes());
        String param = s1.replace("{0}",s3);
        String apiUrl = gamePlatform.getRecordUrl();
        String getURL = apiUrl.concat( "?" ).concat(param);
        return getURL;
    }
    //凯旋棋牌|开元棋牌 对局详情 暂时共享
    public static String getBetDetailURLByKXOrKY(MemberGameData memberGameData, GamePlatform gamePlatform)throws Exception {
        String agent = gamePlatform.getAgent();
        Date nowTime = new Date();
        String md5 = gamePlatform.getMd5();
        String key = DigestUtils.md5Hex(agent + nowTime.getTime() + md5);
        String s1 = String.format(RE_KY_DETAIL_RECORD_S1,agent,nowTime.getTime(),"{0}",key);
        String s2 = String.format(RE_KY_DETAIL_RECORD_S3,"10",memberGameData.getKindId(),memberGameData.getRecordId(),memberGameData.getAccount());
        String s3 = Encrypt.AESEncrypt(s2,gamePlatform.getDes());
        String param = s1.replace("{0}",s3);
        String apiUrl = gamePlatform.getRecordUrl();
        String getURL = apiUrl.concat( "?" ).concat(param);
        return getURL;
    }

    //新世界 对局详情 暂时共享
    public static String getBetDetailURLByXSJ(MemberGameData memberGameData, GamePlatform gamePlatform)throws Exception {
        String agent = gamePlatform.getAgent();
        Date nowTime = new Date();
        String md5 = gamePlatform.getMd5();
        String key = DigestUtils.md5Hex(agent + nowTime.getTime() + md5);
        String s1 = String.format(RE_XSJ_DETAIL_RECORD_S1,agent,nowTime.getTime(),"{0}",key);
        String s2 = String.format(RE_XSJ_DETAIL_RECORD_S2,"19",memberGameData.getGameUserNo(),
                memberGameData.getRecordId(),memberGameData.getServerId(),memberGameData.getAccount());
        String param = s1.replace("{0}",s2);
        String apiUrl = gamePlatform.getRecordUrl();
        String getURL = apiUrl.concat( "?" ).concat(param);
        return getURL;
    }

    //封装数据
    public static AjaxResult gameBetDataWrapper(String result,String account){
        JSONObject object = JSON.parseObject(result);
        JSONObject d = object.getJSONObject("d");
        if (d != null) {
            Integer code = d.getInteger("code");
            if (code != 0) {
                code = code + 10000;
                return AjaxResult.error(code, "查询游戏局号日志失败[未知错误]");
            }
            JSONObject objects = d.getJSONObject("list");
            JSONArray gameID = objects.getJSONArray("GameID");
            JSONArray accounts = objects.getJSONArray("Accounts");
            JSONArray serverID = objects.getJSONArray("ServerID");
            JSONArray kindID = objects.getJSONArray("KindID");
            JSONArray tableID = objects.getJSONArray("TableID");
            JSONArray chairID = objects.getJSONArray("ChairID");
            JSONArray userCount = objects.getJSONArray("UserCount");
            JSONArray cellScore = objects.getJSONArray("CellScore");
            JSONArray allBet = objects.getJSONArray("AllBet");
            JSONArray profit = objects.getJSONArray("Profit");
            JSONArray revenue = objects.getJSONArray("Revenue");
            JSONArray gameStartTime = objects.getJSONArray("GameStartTime");
            JSONArray gameEndTime = objects.getJSONArray("GameEndTime");
            JSONArray cardValue = objects.getJSONArray("CardValue");
            JSONArray channelID = objects.getJSONArray("ChannelID");
            JSONArray lineCode = objects.getJSONArray("LineCode");
            JSONArray recordID = objects.getJSONArray("RecordID");
            List list = new ArrayList();
            for (int i = 0; i < gameID.size(); i++) {
                Map map = new HashMap();
                map.put("gameID", gameID.get(i));
                if (!accounts.get(i).equals(account)) continue;
                map.put("accounts", accounts.get(i));
                map.put("serverID", serverID.get(i));
                map.put("kindID", kindID.get(i));
                map.put("tableID", tableID.get(i));
                map.put("chairID", chairID.get(i));
                map.put("userCount", userCount.get(i));
                map.put("cellScore", cellScore.get(i));
                map.put("allBet", allBet.get(i));
                map.put("profit", profit.get(i));
                map.put("revenue", revenue.get(i));
                map.put("gameStartTime", gameStartTime.get(i));
                map.put("gameEndTime", gameEndTime.get(i));
                map.put("cardValue", cardValue.get(i));
                map.put("channelID", channelID.get(i));
                map.put("lineCode", lineCode.get(i));
                map.put("recordID", recordID.get(i));
                list.add(map);
            }
            d.put("list", list);
            return AjaxResult.success(d);
        }
        return AjaxResult.error("999", "查询游戏局号,数据不存在");
    }

    //封装数据
    public static AjaxResult meiTianGameBetDataWrapper(String result){
        JSONObject object = JSON.parseObject(result);
        if (object != null) {
            Integer code = Integer.valueOf(object.getString("resultCode"));
            if (code != 1) {
                code = code + 20000;
                return AjaxResult.error(code, "查询游戏局号日志失败[未知错误]");
            }
            return AjaxResult.success(object);
        }
        return AjaxResult.error("999", "查询游戏局号,数据不存在");
    }

    public static AjaxResult gameDetailDataWrapper(String result){
        JSONObject object = JSON.parseObject(result);
        JSONObject d = object.getJSONObject("d");
        if (d != null) {
            Integer code = d.getInteger("code");
            if (code != 0) {
                code = code + 10000;
                log.error("查询游戏局号日志失败:{}", JSON.toJSONString(object));
                return AjaxResult.error(code, "查询游戏局号日志失败[未知错误]");
            }
            return AjaxResult.success(d.get("data"));
        }
        return AjaxResult.error("999", "查询游戏局号,数据不存在");
    }
    public static String test ="{\n" +
            "\t\"resultCode\": \"1\",\n" +
            "\t\"url\": \"https://admin.zpsunkaisuo.com/static/CG/html/playCheck.html?EnStr=eyJkYXRhU3RyIjoie1wicm93SURcIjpcIjE0MTk1MjQzOTFcIixcImxhbmdcIjpcIlpILUNOXCJ9IiwiY29kZSI6ImE3MWMzZTFjZjFhNTQ1YTU3YjgxOGY1MTQ0NWUzZWNkIiwibWVyY2hhbnRJZCI6IjIwMTcxNDAwIiwiY3VycmVuY3kiOiJDTlkifQ==&lang=ZH_CN\",\n" +
            "\t\"date\": \"2021-06-25 20:35:02\",\n" +
            "\t\"timeZone\": \"GMT+8\",\n" +
            "\t\"currency\": \"CNY\"\n" +
            "}";
}
