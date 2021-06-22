package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;

@Log4j2
public class RequestParam {

    private static final String RE_KY_DETAIL_RECORD_S1 = "agent=%s&timestamp=%s&param=%s&key=%s";
    private static final String RE_KY_DETAIL_RECORD_S2 = "s=%s&startTime=%s&endTime=%s";
    private static final String RE_KY_DETAIL_RECORD_S3 = "s=%s&kindID=%s&recordID=%s&account=%s";

    //开元棋牌 - 对局详情 返回参数
    public static String requestKYBetRecord(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String agent = memberGameData.getAgent();
        String nowTime = System.currentTimeMillis()+"";
        String startTime = memberGameData.getGameStartTime();
        String endTime = memberGameData.getGameEndTime();
        String md5 = gamePlatform.getMd5();
        String key = DigestUtils.md5Hex(agent + startTime + md5);
        String s1 = String.format(RE_KY_DETAIL_RECORD_S1,agent,nowTime,"{0}",key);
        String s2 = String.format(RE_KY_DETAIL_RECORD_S2,"9",startTime,endTime);
        String s3 = Encrypt.AESEncrypt(s2,gamePlatform.getDes());
        String param = s1.replace("{0}",s3);
        String apiUrl = gamePlatform.getApiUrl();
        String getURL = apiUrl.concat( "?" ).concat(param);
        log.info( "开元棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
    }

    //开元棋牌 - 对局明细 返回参数
    public static String requestKYBetDetail(MemberGameData memberGameData, GamePlatform gamePlatform) throws Exception {
        String agent = gamePlatform.getAgent();
        String nowTime = System.currentTimeMillis()+"";
        String md5 = gamePlatform.getMd5();
        String key = DigestUtils.md5Hex(agent + nowTime + md5);
        String s1 = String.format(RE_KY_DETAIL_RECORD_S1,agent,nowTime,"{0}",key);
        String s2 = String.format(RE_KY_DETAIL_RECORD_S3,"10",memberGameData.getKindId(),memberGameData.getRecordId(),memberGameData.getAccount());
        String s3 = Encrypt.AESEncrypt(s2,gamePlatform.getDes());
        String param = s1.replace("{0}",s3);
        String apiUrl = gamePlatform.getApiUrl();
        String getURL = apiUrl.concat( "?" ).concat(param);
        log.info( "开元棋牌-对局详情-请求参数：{}",getURL );
        return PostData.get(getURL);
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
