package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveBlack;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 拉黑Mapper接口
 *
 * @author 77tv
 * @date 2021-08-24
 */
public interface LiveBlackMapper {


    /**
     * 查询拉黑列表
     *
     * @param liveBlack 拉黑
     * @return 拉黑集合
     */
    List<LiveBlack> selectLiveBlackList(LiveBlack liveBlack, @Param("dbMainLive") String dbMainLive);

    int deleteLiveBlackById(@Param("id") Long id, @Param("dbMainLive") String dbMainLive);

    Set userBlackList(Long host_id);
}