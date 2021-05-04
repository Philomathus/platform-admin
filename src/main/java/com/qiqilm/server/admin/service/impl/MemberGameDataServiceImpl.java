package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.req.ReqMemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBetLog;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import com.qiqilm.server.admin.mapper.GameTypeMapper;
import com.qiqilm.server.admin.mapper.MemberGameDataMapper;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import com.qiqilm.server.admin.utils.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 会员注单数据Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberGameDataServiceImpl implements IMemberGameDataService {
    @Autowired
    private MemberGameDataMapper memberGameDataMapper;

    @Autowired
    private GameTypeMapper gameTypeMapper;

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
}
