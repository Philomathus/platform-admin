package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.domain.ReportIncomeDay;
import com.qiqilm.server.admin.domain.vo.ReportPlamHome;
import com.qiqilm.server.admin.mapper.ReportAgentcountMapper;
import com.qiqilm.server.admin.service.IReportAgentcountService;
import com.qiqilm.server.admin.utils.RedisUtil;
import org.apache.hadoop.mapred.IFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * 代理统计，主要用于代理渠道的统计Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ReportAgentcountServiceImpl implements IReportAgentcountService {
    @Autowired
    private ReportAgentcountMapper reportAgentcountMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 查询代理统计，主要用于代理渠道的统计列表
     *
     * @param reportAgentcount 代理统计，主要用于代理渠道的统计
     * @return 代理统计，主要用于代理渠道的统计
     */
    @Override
    public Object selectReportAgentcountList(ReportAgentcount reportAgentcount) throws ParseException {
        List<ReportAgentcount> allList = new ArrayList<>();
        String dateNowStr = dateNowStr();//获取当天时间字符串
        setSelectTime(dateNowStr, reportAgentcount);//首次进入查询7天的数据

        String beginTime = (String) reportAgentcount.getParams().get("beginTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(beginTime);
        boolean flag = date.before(new Date());
        Map<String, Object> resultMap = new HashMap<>();
        if (flag) {//判断时间是否是正确时间
            allList = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
            if ((reportAgentcount.getAgentcode() != null && allList.size() == 0)
            ||reportAgentcount.getParams().get("endTime").equals(dateNowStr)) {//判断代理号是否为空，代理号不为空，并且没有查询到数据，
                reportAgentcountMapper.calldataProrepPlamcom(dateNowStr, reportAgentcount.getAgentcode());//调用存储过程
                List<ReportAgentcount>   allList1 = reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
                resultMap.put("rows", allList1);
                return resultMap;
            }
        }
        resultMap.put("rows", allList);
        return resultMap;
    }


    public void storage(ReportAgentcount reportAgentcount) {
        String dateNowStr = dateNowStr();//获取当天时间字符串
        if (reportAgentcount.getAgentcode() == null) {
            reportAgentcount.setAgentcode("");
        }
        if (!redisUtil.exists("admin-reportAgentcount") &&
                redisUtil.strSetIfAbsent("admin-reportAgentcount", "0", Duration.ofMinutes(5))) {
            redisUtil.strSet("admin-reportAgentcount", "0", Duration.ofMinutes(5));
            threadPoolTaskExecutor.execute(() -> {
                String result = reportAgentcountMapper.calldataProrepPlamcom(dateNowStr, reportAgentcount.getAgentcode());
                if (StringUtils.hasText(result) && redisUtil.exists("admin-reportAgentcount")) {
                    redisUtil.strIncrement("admin-reportAgentcount");
                }
            });
        }
    }

    @Override
    public List<ReportPlamHome> findChartsOne(String classTwo, String time) {
        return reportAgentcountMapper.findChartsOne(classTwo, time);
    }

    @Override
    public int existsPromotionCode(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.existsPromotionCode(reportAgentcount);
    }

    @Override
    public void addPromotionCode(ReportAgentcount reportAgentcount) {
        reportAgentcountMapper.addPromotionCode(reportAgentcount);
    }

    @Override
    public void delPromotionCode(ReportAgentcount reportAgentcount) {
        reportAgentcountMapper.delPromotionCode(reportAgentcount);
    }

    @Override
    public List<ReportAgentcount> exportAgentcountList(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
    }

    private void setSelectTime(String dateNowStr, ReportAgentcount reportAgentcount) {
        if (null == reportAgentcount.getParams() || reportAgentcount.getParams().size() == 0 ||
                reportAgentcount.getParams().get("beginTime") == "") {
            HashMap m = new HashMap<>();
            m.put("beginTime", getPastDate(7));
            m.put("endTime", dateNowStr);
            reportAgentcount.setParams(m);
        }
    }

    private String dateNowStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    private String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }
}
