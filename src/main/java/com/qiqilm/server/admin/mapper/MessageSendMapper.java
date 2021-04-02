package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.SmsFailLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageSendMapper {
	Integer getLiveCount();

	Integer getPayCount( @Param("beginTime") String beginTime,@Param("endTime") String endTime );

	Integer getCurCount( @Param("beginTime") String beginTime,@Param("endTime") String endTime );

	List<SmsFailLog> smsFailMessage(@Param("beginTime") String beginTime, @Param("endTime") String endTime );
}
