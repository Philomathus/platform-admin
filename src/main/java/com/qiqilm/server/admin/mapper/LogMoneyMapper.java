package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LogMoney;
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

	/**
	 * 新增会员资金信息
	 *
	 * @param logMoney 会员资金信息
	 * @return 结果
	 */
	public int insertLogMoney( LogMoney logMoney );

	List<LogMoney> findMark( @Param( "userId" ) String userId, @Param( "mark" ) String mark,
							 @Param( "money" ) BigDecimal money, @Param( "pay" ) BigDecimal pay );

    Map totalCount(LogMoney logMoney);
}
