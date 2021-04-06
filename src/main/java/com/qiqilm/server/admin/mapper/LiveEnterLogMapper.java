package com.qiqilm.server.admin.mapper;

import org.apache.ibatis.annotations.Param;





public interface LiveEnterLogMapper {
    int addTimes( @Param( "id" ) String id ,@Param( "c" ) int c);
    int addEnterLog( @Param( "id" ) String id , @Param( "p_user_id" ) String p_user_id , @Param( "dtime" ) String dtime ,@Param( "times" ) int times);



}
