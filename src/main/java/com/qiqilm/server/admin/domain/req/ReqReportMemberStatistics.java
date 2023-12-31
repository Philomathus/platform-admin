package com.qiqilm.server.admin.domain.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.core.page.PageDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ReqReportMemberStatistics  {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String inclusive_date;
    private String channelCode;
}
