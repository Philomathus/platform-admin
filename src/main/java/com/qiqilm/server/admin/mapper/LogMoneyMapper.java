package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LogMoney;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * LogMoneyMapper接口
 *
 * @author 77tv
 * @date 2021-01-26
 */
public interface LogMoneyMapper {
	/**
	 * 查询LogMoney
	 *
	 * @param id LogMoneyID
	 * @return LogMoney
	 */
	public LogMoney selectLogMoneyById( String id );

	/**
	 * 查询LogMoney列表
	 *
	 * @param logMoney LogMoney
	 * @return LogMoney集合
	 */
	public List<LogMoney> selectLogMoneyList( LogMoney logMoney );

	/**
	 * 新增LogMoney
	 *
	 * @param logMoney LogMoney
	 * @return 结果
	 */
	public int insertLogMoney( LogMoney logMoney );

	/**
	 * 修改LogMoney
	 *
	 * @param logMoney LogMoney
	 * @return 结果
	 */
	public int updateLogMoney( LogMoney logMoney );

	/**
	 * 删除LogMoney
	 *
	 * @param id LogMoneyID
	 * @return 结果
	 */
	public int deleteLogMoneyById( String id );

	/**
	 * 批量删除LogMoney
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteLogMoneyByIds( String[] ids );

	List<LogMoney> findMark( @Param( "userId" ) String userId, @Param( "mark" ) String mark,
							 @Param( "money" ) BigDecimal money, @Param( "pay" ) BigDecimal pay );
}