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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
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
    @Autowired
    protected RestTemplate restTemplate;

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
            log.info("游戏会员id后缀"+reqMemberGameData.getAccount()+";表名称"+tableLast);
            //判断是否为数字
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            log.info("游戏会员id后缀"+reqMemberGameData.getAccount()+";pattern"+pattern);
            if (pattern.matcher(tableLast).matches()) {
                reqMemberGameData.setTableLast(tableLast);
                log.info("游戏会员id后缀"+reqMemberGameData.getAccount()+";tableLast"+reqMemberGameData.getTableLast());
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
        //判断是否为数字
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(tableLast).matches()) {
            reqMemberGameData.setTableLast(tableLast);
        } else {
            reqMemberGameData.setTableLast("0");
        }
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
                    if (StringUtils.isEmpty(result)) return AjaxResult.error(EnumGamePlatform.KY_CHESS.getName()+"访问超时，稍后再试!");
                    return RequestParamData.gameBetDataWrapper(result,memberGameData.getAgent()+"_"+memberGameData.getAccount());
                }else if (EnumGamePlatform.KAIXUAN_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKXBetRecord(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KAIXUAN_CHESS.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    if (StringUtils.isEmpty(result)) return AjaxResult.error(EnumGamePlatform.KAIXUAN_CHESS.getName()+"访问超时，稍后再试!");
                    return RequestParamData.gameBetDataWrapper(result,memberGameData.getAgent()+"_"+memberGameData.getAccount());
                }else if (EnumGamePlatform.MEITIAN_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestMTBetRecord(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.MEITIAN_CHESS.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    if (StringUtils.isEmpty(result)) return AjaxResult.error(EnumGamePlatform.MEITIAN_CHESS.getName()+"访问超时，稍后再试!");
                    return RequestParamData.meiTianGameBetDataWrapper(result);
                }else if (EnumGamePlatform.NEWWORLD_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestXSJBetRecord(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.NEWWORLD_CHESS.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    if (StringUtils.isEmpty(result)) return AjaxResult.error(EnumGamePlatform.NEWWORLD_CHESS.getName()+"访问超时，稍后再试!");
                    return RequestParamData.xsjGameBetDataWrapper(result,memberGameData.getAgent()+"_"+memberGameData.getAccount());
                }else if (EnumGamePlatform.AG_LIVE.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestAGPlayBetDetail(memberGameData,"getorders.xml?");
                    log.info(EnumGamePlatform.AG_LIVE.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    return RequestParamData.gameAgPlayBetDataWrapper(result,memberGameData.getAccount());
                }else if (EnumGamePlatform.KY_CHESS_NEW.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKYBetRecord(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KY_CHESS_NEW.getName()+"获取局列表返回结果数据:"+JSON.toJSONString(result));
                    if (StringUtils.isEmpty(result)) return AjaxResult.error(EnumGamePlatform.KY_CHESS_NEW.getName()+"访问超时，稍后再试!");
                    return RequestParamData.gameBetDataWrapper(result,memberGameData.getAgent()+"_"+memberGameData.getAccount());
                }
            }
        }catch (Exception e) {
            log.error( "查询游戏局号明细失败，参数:{},错误信息:",JSON.toJSONString(memberGameData),e);
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
                    return RequestParamData.gameDetailDataWrapper(result);
                }else if (EnumGamePlatform.KAIXUAN_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKXBetDetail(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KAIXUAN_CHESS.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
                    return RequestParamData.gameDetailDataWrapper(result);
                }else if (EnumGamePlatform.NEWWORLD_CHESS.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestXSJBetDetail(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.NEWWORLD_CHESS.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
                    return RequestParamData.gameXSJDetailDataWrapper(result);
                }else if (EnumGamePlatform.AG_LIVE.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestAGPlayBetDetail(memberGameData,"getroundsres.xml?");
                    log.info(EnumGamePlatform.AG_LIVE.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
                    return RequestParamData.gameAgPlayDetailDataWrapper(result);
                }else if (EnumGamePlatform.KY_CHESS_NEW.getType() == memberGameData.getPlatformId()){
                    String result = RequestParamData.requestKYBetDetail(memberGameData,gamePlatform);
                    log.info(EnumGamePlatform.KY_CHESS_NEW.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
                    return RequestParamData.gameDetailDataWrapper(result);
                }
            }
        }catch (Exception e) {
            log.error( "查询游戏局号明细失败，参数:{},错误信息:",JSON.toJSONString(memberGameData),e);
            return AjaxResult.error("查询游戏局号明细失败Account:" + memberGameData.getAccount());
        }
        return AjaxResult.error("游戏未配置，请选择其他游戏!");
    }
}