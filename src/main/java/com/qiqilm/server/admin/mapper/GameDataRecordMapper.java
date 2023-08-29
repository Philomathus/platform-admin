package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.annotation.DataSource;
import com.qiqilm.server.admin.domain.GameDataRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏注单数据
 *
 * @author MengJun
 */
public interface GameDataRecordMapper {
    @DataSource( value = "secondaryDataSource" )
    List<GameDataRecord> selectGameDataRecordAgentList( @Param( "tableName" ) String tableNode, @Param( "start" ) String start,
                                                        @Param( "end" ) String end, @Param( "agent" ) String agent, @Param(
                                                                "account" ) String account,
                                                        @Param( "platformId" ) Integer platformId );
}