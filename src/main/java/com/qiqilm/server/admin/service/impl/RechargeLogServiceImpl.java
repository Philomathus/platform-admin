package com.qiqilm.server.admin.service.impl;


import com.qiqilm.server.admin.domain.RechargeLog;
import com.qiqilm.server.admin.mapper.RechargeLogMapper;
import com.qiqilm.server.admin.service.RechargeLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 充值日志服务 interface impl
 *
 * @author Rajesh
 * @date 2023-05-20
 */

@Log4j2
@Service
public class RechargeLogServiceImpl implements RechargeLogService {

    @Resource
    private RechargeLogMapper rechargeLogMapper;

    @Override
    public List<RechargeLog> selectAllRechargeLog( RechargeLog rechargeLog ) {
        String[] selectDate = rechargeLog.getSelectDate();
        if ( selectDate != null && selectDate.length > 0 ) {
            rechargeLog.setSelectStartDate( selectDate[ 0 ] );
            rechargeLog.setSelectEndDate( selectDate[ 1 ] );
        }
        return rechargeLogMapper.selectRechargeLogList( rechargeLog );
    }
}
