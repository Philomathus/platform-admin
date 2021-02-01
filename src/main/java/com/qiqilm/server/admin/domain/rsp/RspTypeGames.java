package com.qiqilm.server.admin.domain.rsp;

import lombok.Data;

import java.util.List;

@Data
public class RspTypeGames {


    List<RspGameInfo> all_games;

    List<String> type_games;


}
