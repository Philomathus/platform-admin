package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBetLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import com.qiqilm.server.admin.domain.vo.GameKYRes;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.PostData;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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

    @Override
    public AjaxResult GameKYResult(MemberGameData memberGameData) {
        //开元游戏根据局号查询结果
        GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
        if (gamePlatform==null){
            return AjaxResult.error("游戏未配置，请选择其他游戏");
        }
        String       resAll;
        try {
            resAll = PostData.getKYBalance( memberGameData.getAgent(), memberGameData.getAccount(),memberGameData.getKindId(), memberGameData.getGameId(),gamePlatform.getDes(), gamePlatform.getMd5(),
                    gamePlatform.getRecordUrl() );
        } catch ( Exception e ) {
            log.error( "查询游戏局号日志失败，memId=" + memberGameData.getAccount() );
            return AjaxResult.error("查询游戏局号日志失败，memId=" + memberGameData.getAccount());
        }
        log.info("查询游戏局号日志"+resAll);
        GameKYRes gameApiResAll = JsonUtil.json2Object( resAll, GameKYRes.class );
        if ( gameApiResAll.getD().getCode() != 0 ) {
            log.error( "查询游戏局号日志失败code：" + gameApiResAll.getD().getCode() );
            return AjaxResult.error( "查询游戏局号日志失败code：" + gameApiResAll.getD().getCode());
        }
        return	AjaxResult.success( gameApiResAll.getD());

    }

}

