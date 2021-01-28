package com.qiqilm.server.admin.mapper;

import java.util.List;

import com.qiqilm.server.admin.domain.LiveLimitMsg;
import org.apache.ibatis.annotations.Param;

/**
 * //昵称限制Mapper接口
 *
 * @author 77tv
 * @date 2021-01-27
 */
public interface LiveLimitMsgMapper {


	public List<LiveLimitMsg> selectLiveLimitMsgList();


	int deleteAll();

	int insertBatch(@Param("list")List<String> strings);
}