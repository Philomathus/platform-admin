package com.qiqilm.server.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.LogGameOrder;
import com.qiqilm.server.admin.domain.LogMoney;
import com.qiqilm.server.admin.domain.MemberGameTransfer;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogGameOrderService;
import com.qiqilm.server.admin.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 会员上下分Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Slf4j
@Service
public class LogGameOrderServiceImpl implements ILogGameOrderService {
	@Autowired
	private LogGameOrderMapper logGameOrderMapper;
	@Autowired
	private GamePlatformMapper gamePlatformMapper;
	@Autowired
	private MemberInfoMapper memberInfoMapper;
	@Autowired
	private LogMoneyMapper logMoneyMapper;
	@Autowired
	private MemberGameTransferMapper memberGameTransferMapper;


	/**
	 * 查询会员上下分
	 *
	 * @param id 会员上下分ID
	 * @return 会员上下分
	 */
	@Override
	public LogGameOrder selectLogGameOrderById( String id ) {
		return logGameOrderMapper.selectLogGameOrderById( id );
	}

	/**
	 * 查询会员上下分列表
	 *
	 * @param logGameOrder 会员上下分
	 * @return 会员上下分
	 */
	@Override
	public List<LogGameOrder> selectLogGameOrderList( LogGameOrder logGameOrder ) {
		if ( logGameOrder.getSelectDate() != null ) {
			logGameOrder.setStartTime( logGameOrder.getSelectDate()[ 0 ] + " 00:00:00" );
			logGameOrder.setEndTime( logGameOrder.getSelectDate()[ 1 ] + " 23:59:59" );
		}
		List<LogGameOrder> logGameOrders = logGameOrderMapper.selectLogGameOrderList( logGameOrder );
		if ( !CollectionUtils.isEmpty( logGameOrders ) ) {
			List<GamePlatform> gamePlatformList = gamePlatformMapper.selectGamePlatformList( null );
			for ( LogGameOrder datum : logGameOrders ) {
				for ( GamePlatform gamePlatform : gamePlatformList ) {
					if ( datum.getPlatformId() == gamePlatform.getId().intValue() ) {
						datum.setPlatformName( gamePlatform.getName() );
					}
				}
			}
		}
		return logGameOrders;
	}

	/**
	 * 查询会员上下分列表
	 *
	 * @param logGameOrder 会员上下分
	 * @return 会员上下分
	 */
	@Override
	public List<LogGameOrder> selectLogGameScoreList( LogGameOrder logGameOrder ) {
		if ( logGameOrder.getSelectDate() != null ) {
			logGameOrder.setStartTime( logGameOrder.getSelectDate()[ 0 ] + " 00:00:00" );
			logGameOrder.setEndTime( logGameOrder.getSelectDate()[ 1 ] + " 23:59:59" );
		}
		//默认查询上分
		List<LogGameOrder> logGameOrders = new ArrayList<>();
		if (logGameOrder.getType() == null || logGameOrder.getType() == 1){
			logGameOrder.setType(1);
			logGameOrders = logGameOrderMapper.selectUpLogGameScoreList( logGameOrder );
		}
		if (logGameOrder.getType() == 2){
			logGameOrders = logGameOrderMapper.selectDownLogGameScoreList( logGameOrder );
		}
		if ( !CollectionUtils.isEmpty( logGameOrders ) ) {
			List<GamePlatform> gamePlatformList = gamePlatformMapper.selectGamePlatformList( null );
			for ( LogGameOrder datum : logGameOrders ) {
				for ( GamePlatform gamePlatform : gamePlatformList ) {
					if ( datum.getPlatformId() == gamePlatform.getId().intValue() ) {
						datum.setPlatformName( gamePlatform.getName() );
					}
				}
			}
		}
		return logGameOrders;
	}


	/**
	 * 新增会员上下分
	 *
	 * @param logGameOrder 会员上下分
	 * @return 结果
	 */
	@Override
	public int insertLogGameOrder( LogGameOrder logGameOrder ) {
		return logGameOrderMapper.insertLogGameOrder( logGameOrder );
	}

	/**
	 * 修改会员上下分
	 *
	 * @param logGameOrder 会员上下分
	 * @return 结果
	 */
	@Override
	public int updateLogGameOrder( LogGameOrder logGameOrder ) {
		return logGameOrderMapper.updateLogGameOrder( logGameOrder );
	}

	/**
	 * 批量删除会员上下分
	 *
	 * @param ids 需要删除的会员上下分ID
	 * @return 结果
	 */
	@Override
	public int deleteLogGameOrderByIds( String[] ids ) {
		return logGameOrderMapper.deleteLogGameOrderByIds( ids );
	}

	/**
	 * 删除会员上下分信息
	 *
	 * @param id 会员上下分ID
	 * @return 结果
	 */
	@Override
	public int deleteLogGameOrderById( String id ) {
		return logGameOrderMapper.deleteLogGameOrderById( id );
	}


	/**
	 * 回退资金
	 * 1.更新资金冻结表
	 * 2.更新投注记录表
	 * 3.新增余额
	 * @param list
	 * @return
	 */
	@Override
	public int executeBackScore(List<LogGameOrder> list) {
		int count = 0;
		//下分
		for (LogGameOrder logGameOrder: list){
			LogGameOrderServiceImpl logGameOrderService = SpringUtils.getBean(this.getClass());
			logGameOrderService.saveOrUpdateScore(logGameOrder);
			count ++;
		}
		return count;
	}

	@Transactional( rollbackFor = Exception.class )
	public void saveOrUpdateScore(LogGameOrder logGameOrder){
		//因为是补分，或者加分，所以不能处理会员资金冻结表
		String name = "下分补分";
		if (logGameOrder.getType() == 1){
			name = "上分回退";
		}
		LogGameOrder logOrder = new LogGameOrder();
		logOrder.setId( logGameOrder.getId() );
		logOrder.setETime( new Date() );
		logOrder.setMemberId(logGameOrder.getMemberId() );
		if (logGameOrder.getType() == 1){
			logOrder.setStatus(13); //成功
		}else {
			logOrder.setStatus( 2 ); //成功
		}
		logOrder.setType( logGameOrder.getType() );
		logOrder.setMoney( logGameOrder.getMoney() );
		logOrder.setPlatformId(logGameOrder.getPlatformId() );
		logGameOrderMapper.updateLogGameOrder(logOrder);
		BigDecimal now = memberInfoMapper.getMemberMoney( logGameOrder.getMemberId() );
		MemberGameTransfer memberGameTransfer = new MemberGameTransfer();
		memberGameTransfer.setPlatformId(logGameOrder.getPlatformId()+"");
		memberGameTransfer.setTransferId(logGameOrder.getId());
		//没有上分成功，回退资金
		BigDecimal change = logGameOrder.getMoney();
		if (logGameOrder.getType() == 2){
			List<MemberGameTransfer> list = memberGameTransferMapper.selectMemberGameTransferList(memberGameTransfer);
			if (list != null && list.size() >0){
				if (list.size() > 1){
					log.error("额度记录表出现多条记录，请排查信息:{}", JSON.toJSONString(list));
				}
				change = list.get(0).getTransferAmount();//真实资金
			}
		}
		int i = change.compareTo( BigDecimal.ZERO );
		memberInfoMapper.updateMoneySelect( logGameOrder.getMemberId(), change, null, null, null, null );
		logGameOrder = null;//测试
		LogMoney logMoney = new LogMoney();
		logMoney.setId( logGameOrder.getId() );
		logMoney.setUserId( logGameOrder.getMemberId() );
		logMoney.setUserName( logGameOrder.getUserName() );
		if (logGameOrder.getType() == 2){
			logMoney.setUpdateTime( new Date() );
		}
		logMoney.setIncome( BigDecimal.ZERO );
		logMoney.setPay( BigDecimal.ZERO );
		if ( i > 0 ) {
			logMoney.setIncome( change );
		} else {
			logMoney.setPay( change);
		}
		GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById(logGameOrder.getPlatformId());
		logMoney.setTotal( change.add(now) );
		logMoney.setTotalBefore( now );
		logMoney.setType( EnumMoney.platform.getType());
		logMoney.setDes( EnumMoney.platform.getDes());
		logMoney.setAgent(gamePlatform.getAgent());
		logMoney.setMark( logGameOrder.getPlatformName()+name );
		logMoney.setMarkorder( logGameOrder.getId() );
		String db = logMoney.getUserId().substring( logMoney.getUserId().length() - 1 );
		if (logMoneyMapper.findExist(db,logMoney.getId()) != null){
			logMoneyMapper.updateByIdSelective(logMoney,db);
		}else {
			logMoneyMapper.insertLogMoney( logMoney, db);
		}
		log.info( name+"成功：会员ID:{},"+name+"平台:{},金额:{},result:{}", logGameOrder.getMemberId(), logGameOrder.getPlatformName(), logGameOrder.getMoney(),"0");
	}
}
