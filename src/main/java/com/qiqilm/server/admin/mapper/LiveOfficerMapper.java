package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.LiveOfficer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface LiveOfficerMapper {

    /**
     * 查询房管管理 get life officer by id
     *
     * @param id 房管管理ID
     * @return 房管管理
     */
    public LiveOfficer selectLiveOfficerById(String id);

    /**
     * 查询房管管理列表 list live officer
     *
     * @param liveOfficer 房管管理
     * @return 房管管理集合
     */
    public List<LiveOfficer> selectLiveOfficerList(LiveOfficer liveOfficer);

    /**
     * 新增房管管理 Add live officer management
     *
     * @param liveOfficer 房管管理
     * @return 结果
     */
    public int insertLiveOfficer(LiveOfficer liveOfficer);

    /**
     * 修改房管管理 update live officer
     *
     * @param liveOfficer 房管管理
     * @return 结果
     */
    public int updateLiveOfficer(LiveOfficer liveOfficer);

    /**
     * 删除房管管理 delete live officer
     *
     * @param id 房管管理ID
     * @return 结果
     */
    public int deleteLiveOfficerById(String id);

    /**
     * 批量删除房管管理 delete selected live officers
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLiveOfficerByIds(String[] ids );

    @Select("select p_user_id from ${dbLive}.live_officer where host_id = #{hostId}")
    Set<String> userManage(@Param("hostId") Long hostId);
}
