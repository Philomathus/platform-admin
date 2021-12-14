package com.qiqilm.server.admin.mapper;

import java.util.List;
import java.util.Map;

import com.qiqilm.server.admin.domain.LotteryBet;
import org.apache.ibatis.annotations.Param;

/**
 * 用户投资行为Mapper接口
 *
 * @author 77tv
 * @date 2021-02-03
 */
public interface LotteryBetMapper {
	/**
	 * 查询用户投资行为
	 *
	 * @param id 用户投资行为ID
	 * @return 用户投资行为
	 */
	public LotteryBet selectLotteryBetById(@Param( "id" )String start,@Param( "dbNodes" ) String dbNodes);

	/**
	 * 查询用户投资行为列表
	 * @return 用户投资行为集合
	 */
	public List<LotteryBet> selectLotteryBetList(@Param( "start" )String start,@Param( "end" )String end);


	/**
	 * 删除用户投资行为
	 *
	 * @param id 用户投资行为ID
	 * @return 结果
	 */
	public int deleteLotteryBetById(String id);

	/**
	 * 批量删除用户投资行为
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLotteryBetByIds(String[] ids );

	Map<String, Object> sumBatCostPrize( @Param( "anchor" ) Long anchor, @Param( "beginTime" ) String beginTime,
										 @Param( "endTime" ) String endTime );
}
