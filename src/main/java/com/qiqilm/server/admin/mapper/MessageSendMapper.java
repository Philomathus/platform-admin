package com.qiqilm.server.admin.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageSendMapper {
	Integer getLiveCount();

	Integer getPayCount( @Param("beginTime") String beginTime,@Param("endTime") String endTime );

	Integer getCurCount( @Param("beginTime") String beginTime,@Param("endTime") String endTime );

	List<String> smsFailMessage( @Param("beginTime") String beginTime, @Param("endTime") String endTime );
}
