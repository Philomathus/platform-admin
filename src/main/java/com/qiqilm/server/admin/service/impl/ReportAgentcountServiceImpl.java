package com.qiqilm.server.admin.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.ReportAgentcountMapper;
import com.qiqilm.server.admin.domain.ReportAgentcount;
import com.qiqilm.server.admin.service.IReportAgentcountService;

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



    /**
     * 查询代理统计，主要用于代理渠道的统计列表
     *
     * @param reportAgentcount 代理统计，主要用于代理渠道的统计
     * @return 代理统计，主要用于代理渠道的统计
     */
    @Override
    public List<ReportAgentcount> selectReportAgentcountList(ReportAgentcount reportAgentcount) {
        return reportAgentcountMapper.selectReportAgentcountList(reportAgentcount);
    }


}