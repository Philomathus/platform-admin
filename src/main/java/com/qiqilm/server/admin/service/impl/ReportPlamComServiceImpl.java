package com.qiqilm.server.admin.service.impl;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.qiqilm.server.admin.domain.ReportPlamCom;
import com.qiqilm.server.admin.mapper.ReportPlamComMapper;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.service.IReportPlamComService;
import org.springframework.util.StringUtils;

/**
 * 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class ReportPlamComServiceImpl implements IReportPlamComService {
    @Autowired
    private ReportPlamComMapper reportPlamComMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 查询综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间列表
     *
     * @param reportPlamCom 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
     * @return 综合数据报会每天进行前一天数据的生成，如果需要查当天的数据则需手动调用prorep_plamcom报存储过程，传入当天时间
     */
    @Override
    public List<ReportPlamCom> selectReportPlamComList(ReportPlamCom reportPlamCom) {
        List<ReportPlamCom> allList = reportPlamComMapper.selectReportPlamComList(reportPlamCom);
        return allList;
    }

    @Override
    public void storage(ReportPlamCom reportPlamCom) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        if (Strings.isBlank(reportPlamCom.getReporttime())) {
            reportPlamCom.setReporttime(dateNowStr);
        }

        if (reportPlamCom.getReporttime() == null || reportPlamCom.getReporttime().equals(dateNowStr)) {
            if (!redisUtil.strSetIfAbsent("admin-reportPlamCom", "0", Duration.ofMinutes(10))) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> {
                String result = reportPlamComMapper.calldataProrepPlamcom(dateNowStr);
                if (StringUtils.hasText(result) && redisUtil.exists("admin-reportPlamCom")) {
                    redisUtil.strIncrement("admin-reportPlamCom");
                }
            });

        }
    }

}