package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.domain.LogMoneyLive;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员资金信息Mapper接口
 *
 * @author 77tv
 * @date 2021-01-29
 */
public interface LogMoneyMapper {

	/**
	 * 查询 会员资金信息列表
	 *
	 * @param logMoney 会员资金信息
	 * @return 会员资金信息集合
	 */
	public List<LogMoney> selectLogMoneyList( LogMoney logMoney );

	public List<LogMoney> selectLogMoneyFirstList( LogMoney logMoney );

	public List<LogMoney> selectLogMoneySingleList( LogMoney logMoney );

	/**
	 * 新增会员资金信息
	 *
	 * @return 结果
	 */
	public int insertLogMoney( @Param( "req" ) LogMoney reqLogMoney, @Param( "dbNodes" ) String dbNodes );

	public int insertLogMoneyPop( @Param( "req" ) LogMoneyLive reqLogMoney, @Param( "dbNodes" ) String dbNodes );

	Integer findExist( @Param( "dbNodes" ) String dbNodes, @Param( "keyId" ) String id );

	List<LogMoney> findMark( @Param( "userId" ) String userId, @Param( "mark" ) String mark,
							 @Param( "money" ) BigDecimal money, @Param( "pay" ) BigDecimal pay,
							 @Param( "dbNodes" ) String dbNodes );

	List<LogMoney> findMarkStartTime( @Param( "userId" ) String userId, @Param( "mark" ) String mark,
							 @Param( "money" ) BigDecimal money, @Param( "pay" ) BigDecimal pay,
							 @Param( "dbNodes" ) String dbNodes, @Param( "startTime" ) String startTime );

	Map listCount( LogMoney logMoney );
	Map listCountAll( LogMoney logMoney );

	int updateByIdSelective(@Param("req") LogMoney reqLogMoney, @Param( "dbNodes" ) String dbNodes);

    int findExistActivityCashBack(@Param( "userId" ) String memberId, @Param( "dbNodes" ) String dbNodes);
}
