package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LiveVideoProp;
import com.qiqilm.server.admin.domain.vo.LiveVideoPropVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 送礼物Mapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LiveVideoPropMapper {

	/**
	 * 查询送礼物列表
	 *
	 * @param liveVideoProp 送礼物
	 * @return 送礼物集合
	 */
	public List<LiveVideoProp> selectLiveVideoPropList( LiveVideoProp liveVideoProp );

	public List<LiveVideoPropVo> findVideoPropList( @Param( "start" ) long start, @Param( "end" ) long end );

	LiveVideoProp getCount( LiveVideoProp liveVideoProp );

	BigDecimal sumHostProp( @Param( "userId" ) Long userId, @Param( "beginTime" ) String beginTime );

	BigDecimal sumHostPropDay( @Param( "userId" ) Integer userId, @Param( "dayTime" ) String dayTime );

}
