package com.qiqilm.server.admin.domain.req;

import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import lombok.Data;

import java.util.List;

@Data
public class ReqTypeGame {

    List<ReqTypeGameInfo> all_games;

    List<String> type_games;


}
