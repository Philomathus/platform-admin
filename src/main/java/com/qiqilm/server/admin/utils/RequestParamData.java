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
        String getURL = getBetURLByKXOrKY(memberGameData, gamePlatform);
        log.info( "凯旋棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }

    //凯旋棋牌|开元棋牌 对局列表 暂时共享
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
                map.put("recordID", recordID.get(0));
                list.add(map);
            }
            d.put("list", list);
            return AjaxResult.success(d);
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
    //测试数据
    private static final String test = "{\n" +
            "\t\"m\": \"/getRecordHandle\",\n" +
            "\t\"s\": 106,\n" +
            "\t\"d\": {\n" +
            "\t\t\"code\": 0,\n" +
            "\t\t\"start\": 1624279815000,\n" +
            "\t\t\"end\": 1624280115983,\n" +
            "\t\t\"count\": 2,\n" +
            "\t\t\"list\": {\n" +
            "\t\t\t\"GameID\": [\"50-1624280072-5381238-4\", \"50-1624279854-5380812-1\"],\n" +
            "\t\t\t\"Accounts\": [\"71916_7700_50318\", \"71916_7700_50322\"],\n" +
            "\t\t\t\"ServerID\": [2201, 9301],\n" +
            "\t\t\t\"KindID\": [220, 930],\n" +
            "\t\t\t\"TableID\": [44020003, 186020001],\n" +
            "\t\t\t\"ChairID\": [4, 0],\n" +
            "\t\t\t\"UserCount\": [3, 1],\n" +
            "\t\t\t\"CellScore\": [\"2.00\", \"60.00\"],\n" +
            "\t\t\t\"AllBet\": [\"5.00\", \"60.00\"],\n" +
            "\t\t\t\"Profit\": [\"1.90\", \"-60.00\"],\n" +
            "\t\t\t\"Revenue\": [\"0.10\", \"0.00\"],\n" +
            "\t\t\t\"GameStartTime\": [\"2021-06-21 20:54:32\", \"2021-06-21 20:50:54\"],\n" +
            "\t\t\t\"GameEndTime\": [\"2021-06-21 20:54:46\", \"2021-06-21 20:51:30\"],\n" +
            "\t\t\t\"CardValue\": [\"32022c3d08270000000109170000004\", \"13c1b0b37242012d2b35223141d063d03408163626155072a232119\"],\n" +
            "\t\t\t\"ChannelID\": [71916, 71916],\n" +
            "\t\t\t\"LineCode\": [\"71916_无\", \"71916_无\"],\n" +
            "\t\t\t\"RecordID\": [1]\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";
    private static final String test1 ="{\n" +
            "\t\"m\": \"/getRecordHandle\",\n" +
            "\t\"s\": 110,\n" +
            "\t\"d\": {\n" +
            "\t\t\"code\": 0,\n" +
            "\t\t\"data\": [{\n" +
            "\t\t\t\t\"bet\": 0,\n" +
            "\t\t\t\t\"ty\": 2,\n" +
            "\t\t\t\t\"time\": 6,\n" +
            "\t\t\t\t\"pos\": 1\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 45,\n" +
            "\t\t\t\t\"ty\": 2,\n" +
            "\t\t\t\t\"time\": 6,\n" +
            "\t\t\t\t\"pos\": 3\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 0,\n" +
            "\t\t\t\t\"ty\": 2,\n" +
            "\t\t\t\t\"time\": 6,\n" +
            "\t\t\t\t\"pos\": 4\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 50,\n" +
            "\t\t\t\t\"ty\": 2,\n" +
            "\t\t\t\t\"time\": 7,\n" +
            "\t\t\t\t\"pos\": 2\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 32000,\n" +
            "\t\t\t\t\"ty\": 4,\n" +
            "\t\t\t\t\"time\": 14,\n" +
            "\t\t\t\t\"pos\": 4\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 24000,\n" +
            "\t\t\t\t\"ty\": 4,\n" +
            "\t\t\t\t\"time\": 15,\n" +
            "\t\t\t\t\"pos\": 3\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"bet\": 16000,\n" +
            "\t\t\t\t\"ty\": 4,\n" +
            "\t\t\t\t\"time\": 15,\n" +
            "\t\t\t\t\"pos\": 1\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t}\n" +
            "}";
}
