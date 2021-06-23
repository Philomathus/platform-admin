package com.qiqilm.server.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBetLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import com.qiqilm.server.admin.utils.RequestParamData;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 会员注单数据Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Log4j2
@Service
public class MemberGameDataServiceImpl implements IMemberGameDataService {
    @Resource
    private MemberGameDataMapper memberGameDataMapper;
    @Resource
    private GamePlatformMapper gamePlatformMapper;

    /**
     * 查询会员注单数据列表
     *
     * @param reqMemberGameData 会员注单数据
     * @return 会员注单数据
     */
    @Override
    public List<RspMemberGameData> selectMemberGameDataList(ReqMemberGameData reqMemberGameData) {
        if (reqMemberGameData.getSelectDate() != null) {
            reqMemberGameData.setStartTime(reqMemberGameData.getSelectDate()[0] + " 00:00:00");
            reqMemberGameData.setEndTime(reqMemberGameData.getSelectDate()[1] + " 23:59:59");
        }
        if (StringUtils.isNotBlank(reqMemberGameData.getAccount())) {
            String tableLast = reqMemberGameData.getAccount().substring(reqMemberGameData.getAccount().length() - 1);
            //判断是否为数字
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            if (pattern.matcher(tableLast).matches()) {
                reqMemberGameData.setTableLast(tableLast);
            } else {
                reqMemberGameData.setTableLast("0");
            }
            return memberGameDataMapper.selectMemberGameDataList(reqMemberGameData);
        } else {
            reqMemberGameData.setTableLast("0");
            return memberGameDataMapper.selectMemberGameDataList(reqMemberGameData);
        }
    }

    @Override
    public RspMemberGameData getCount(ReqMemberGameData reqMemberGameData) {
        if (reqMemberGameData.getSelectDate() != null) {
            reqMemberGameData.setStartTime(reqMemberGameData.getSelectDate()[0] + " 00:00:00");
            reqMemberGameData.setEndTime(reqMemberGameData.getSelectDate()[1] + " 23:59:59");
        }
        String tableLast = reqMemberGameData.getAccount().substring(reqMemberGameData.getAccount().length() - 1);
        reqMemberGameData.setTableLast(tableLast);
        return memberGameDataMapper.getCountMemberGameDataList(reqMemberGameData);
    }

    @Override
    public AjaxResult getBetData(MemberGameData memberGameData) {
        String agent = memberGameData.getAgent();
        String gameId = memberGameData.getGameId();
        RspLotteryBetLog rspLotteryBetLog = new RspLotteryBetLog();
        if (Strings.isNotBlank(agent) && agent.equals("10000")) {
            rspLotteryBetLog = memberGameDataMapper.findBetList(gameId);

        }
        if (Strings.isNotBlank(agent) && agent.equals("80000")) {
            rspLotteryBetLog = memberGameDataMapper.findBetLists(gameId);
        }
        return AjaxResult.success(rspLotteryBetLog);
    }

    /**
     * 目前仅支持开元棋牌查询明细列表
     * @param memberGameData
     * @return
     */
    @Override
    public AjaxResult getGameBetRecordData(MemberGameData memberGameData) {
        //校验游戏种类
        if (memberGameData == null || memberGameData.getPlatformId() == 0){
            return AjaxResult.error("查询游戏局号失败，请输入正确的查询参数");
        }
        try {
            GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
            if (gamePlatform != null){
                if (EnumGamePlatform.KY_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKYBetRecord(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KY_CHESS.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    JSONObject object = JSON.parseObject(result);
                    JSONObject d = object.getJSONObject("d");
                    if (d != null){
                        Integer code = d.getInteger("code");
                        if (code != 0){
                            log.error( "查询游戏局号日志失败:{}",JSON.toJSONString(object));
                            return AjaxResult.error( "查询游戏局号日志失败code：" + code);
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
                            map.put("gameID",gameID.get(i));
                            map.put("accounts",accounts.get(i));
                            map.put("serverID",serverID.get(i));
                            map.put("kindID",kindID.get(i));
                            map.put("tableID",tableID.get(i));
                            map.put("chairID",chairID.get(i));
                            map.put("userCount",userCount.get(i));
                            map.put("cellScore",cellScore.get(i));
                            map.put("allBet",allBet.get(i));
                            map.put("profit",profit.get(i));
                            map.put("revenue",revenue.get(i));
                            map.put("gameStartTime",gameStartTime.get(i));
                            map.put("gameEndTime",gameEndTime.get(i));
                            map.put("cardValue",cardValue.get(i));
                            map.put("channelID",channelID.get(i));
                            map.put("lineCode",lineCode.get(i));
                            map.put("recordID",recordID.get(0));
                            list.add(map);
                        }
                        d.put("list",list);
                        return AjaxResult.success(d);
                    }
                }
            }
        }catch (Exception e) {
            log.error( "查询游戏局号明细失败，参数:{},错误信息:{}",JSON.toJSONString(memberGameData),e);
            return AjaxResult.error("查询游戏局号明细失败Account:" + memberGameData.getAccount());
        }
        return AjaxResult.error("游戏未配置，请选择其他游戏!");
    }

    /**
     * 目前仅支持开元棋牌查询明细列表
     * @param memberGameData
     * @return
     */
    @Override
    public AjaxResult getGameBetDetailData(MemberGameData memberGameData) {
        //校验游戏种类
        if (memberGameData == null || memberGameData.getPlatformId() == 0){
            return AjaxResult.error("查询游戏局号失败，请输入正确的查询参数");
        }
        try {
            GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
            if (gamePlatform != null){
                if (EnumGamePlatform.KY_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKYBetDetail(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KY_CHESS.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
                    JSONObject object = JSON.parseObject(result);
                    JSONObject d = object.getJSONObject("d");
                    if (d != null) {
                        Integer code = d.getInteger("code");
                        if (code != 0) {
                            log.error("查询游戏局号日志失败:{}", JSON.toJSONString(object));
                            return AjaxResult.error("查询游戏局号日志失败code：" + code);
                        }
                        return AjaxResult.success(d.get("data"));
                    }
                }
            }
        }catch (Exception e) {
            log.error( "查询游戏局号明细失败，参数:{},错误信息:{}",JSON.toJSONString(memberGameData),e);
            return AjaxResult.error("查询游戏局号明细失败Account:" + memberGameData.getAccount());
        }
        return AjaxResult.error("游戏未配置，请选择其他游戏!");
    }

}

