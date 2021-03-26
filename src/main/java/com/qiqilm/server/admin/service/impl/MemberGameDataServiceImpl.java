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
        if ( StringUtils.isNotBlank( reqMemberGameData.getAccount() ) ) {
            String tableLast =  reqMemberGameData.getAccount().substring( reqMemberGameData.getAccount().length() - 1 );
            reqMemberGameData.setTableLast( tableLast );
            return memberGameDataMapper.selectMemberGameDataSingleList( reqMemberGameData );
        }
        return memberGameDataMapper.selectMemberGameDataList(reqMemberGameData);
    }

    @Override
    public AjaxResult getCount(ReqMemberGameData reqMemberGameData) {
        if (reqMemberGameData.getSelectDate() != null) {
            reqMemberGameData.setStartTime(reqMemberGameData.getSelectDate()[0] + " 00:00:00");
            reqMemberGameData.setEndTime(reqMemberGameData.getSelectDate()[1] + " 23:59:59");
        }
        if ( StringUtils.isNotBlank( reqMemberGameData.getAccount() ) ) {
            String tableLast =  reqMemberGameData.getAccount().substring( reqMemberGameData.getAccount().length() - 1 );
            reqMemberGameData.setTableLast( tableLast );
//            return memberGameDataMapper.getCountMemberGameDataSingleList( reqMemberGameData );
        }
//        return memberGameDataMapper.getCountMemberGameDataList(reqMemberGameData);
        return null;
    }

    @Override
    public AjaxResult getBetData(MemberGameData memberGameData) {
        String agent = memberGameData.getAgent();
        String gameId = memberGameData.getGameId();
        RspLotteryBetLog rspLotteryBetLog = new RspLotteryBetLog();
        if (Strings.isNotBlank(agent)&&agent.equals("10000")){
            rspLotteryBetLog = memberGameDataMapper.findBetList( gameId );

        }
        if (Strings.isNotBlank(agent)&&agent.equals("80000")){
            rspLotteryBetLog=   memberGameDataMapper.findBetLists( gameId );
        }
        return AjaxResult.success(rspLotteryBetLog);
    }
}
