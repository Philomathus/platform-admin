package com.qiqilm.server.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.*;
import com.qiqilm.server.admin.domain.vo.PageBO;
import com.qiqilm.server.admin.domain.vo.WithdrawReport;
import com.qiqilm.server.admin.enums.EnumAction;
import com.qiqilm.server.admin.enums.EnumMoney;
import com.qiqilm.server.admin.mapper.*;
import com.qiqilm.server.admin.service.ILogService;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 会员信息 Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class MemberInfoServiceImpl implements IMemberInfoService {
	@Autowired
	private MemberInfoMapper       memberInfoMapper;
	@Autowired
	private MemberActionLogsMapper actionLogsMapper;
	@Autowired
	private LogMoneyMapper         logMoneyMapper;
	@Autowired
	private MemberBcodeMapper      codeFlowMapper;
	@Autowired
	private ILogService            logService;
	@Autowired
    private MemberCardMapper  memberCardMapper;

	/**
	 * 查询会员信息
	 *
	 * @param id 会员信息 ID
	 * @return 会员信息
	 */
	@Override
	public MemberInfo selectMemberInfoById( String id ) {
		return memberInfoMapper.selectMemberInfoById( id );
	}

	/**
	 * 查询会员信息 列表
	 *
	 * @param memberInfo 会员信息
	 * @return 会员信息
	 */
	@Override
	public List<MemberInfo> selectMemberInfoList( MemberInfo memberInfo ) {
		return memberInfoMapper.selectMemberInfoList( memberInfo );
	}

	/**
	 * 新增会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	@Override
	public int insertMemberInfo( MemberInfo memberInfo ) {
		return memberInfoMapper.insertMemberInfo( memberInfo );
	}

	/**
	 * 修改会员信息
	 *
	 * @param memberInfo 会员信息
	 * @return 结果
	 */
	@Override
	public int updateMemberInfo( MemberInfo memberInfo ) {
		return memberInfoMapper.updateMemberInfo( memberInfo );
	}

	/**
	 * 批量删除会员信息
	 *
	 * @param ids 需要删除的会员信息 ID
	 * @return 结果
	 */
	@Override
	public int deleteMemberInfoByIds( String[] ids ) {
		return memberInfoMapper.deleteMemberInfoByIds( ids );
	}

	/**
	 * 删除会员信息 信息
	 *
	 * @param id 会员信息 ID
	 * @return 结果
	 */
	@Override
	public int deleteMemberInfoById( String id ) {
		return memberInfoMapper.deleteMemberInfoById( id );
	}

	@Override
	@Transactional
	public RspBase addMemberMoneyOnly( String ip, String userId, BigDecimal money, BigDecimal beatNum, String Mk,
									   String markorder, String admin_name ) {
		RspBase    rspBase       = new RspBase();
		MemberInfo oldmemberInfo = this.selectMemberInfoById( userId );
		BigDecimal total         = oldmemberInfo.getTotalAccount();

		if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
			if ( money.compareTo( new BigDecimal( 1000000 ) ) > 0 ) {
				rspBase.setMsg( "最大金额为1000000" );
				rspBase.setCode( 2 );
				return rspBase;
			}
		} else if ( money.compareTo( BigDecimal.ZERO ) < 0 ) {
			BigDecimal lat = total.add( money );
			if ( lat.compareTo( BigDecimal.ZERO ) < 0 ) {
				rspBase.setMsg( "余额" + money + "不足扣除" );
				rspBase.setCode( 2 );
				return rspBase;
			}
			beatNum = new BigDecimal( 0 );
		}

		if ( !"0".equals( markorder ) ) {
			List<LogMoney> markList = null;
			if ( money.compareTo( BigDecimal.ZERO ) > 0 ) {
				markList = logMoneyMapper.findMark( userId, markorder, money, null );
			} else {
				BigDecimal negate = money.negate();
				markList = logMoneyMapper.findMark( userId, markorder, null, negate );
			}
			if ( markList.size() > 0 ) {
				rspBase.setMsg( "请查看此笔金额是否已经入款过，如否请输入其他订单备注" );
				rspBase.setCode( 2 );
				return rspBase;
			}
		}

		if (total != null ) {
			BigDecimal now = total.add( money );
			if ( beatNum != null && beatNum.compareTo( BigDecimal.ZERO ) > 0 ) {
				MemberBcode codeFlow = new MemberBcode();
				codeFlow.setId( UuidUtil.getRandomUuidWithoutSeparator() );
				codeFlow.setIncome( money.multiply( beatNum ).setScale( 2 ) );
				codeFlow.setCreateTime( new Date() );
				codeFlow.setStatus( 0 );
				codeFlow.setCur( BigDecimal.ZERO );
				codeFlow.setUserId( userId );
				codeFlow.setDes( "人工入款" );
				codeFlowMapper.insertMemberBcode( codeFlow );
			} else {
				beatNum = new BigDecimal( 0 );
			}
			memberInfoMapper.updateMoneySelect( userId, money, null, money.multiply( beatNum ).setScale( 2 ), null, null );
			MemberActionLogs log = new MemberActionLogs();
			log.setId( UuidUtil.getRandomUuidWithoutSeparator() );
			log.setUserId( userId );
			log.setUserName( oldmemberInfo.getUserName() );
			log.setcTime( new Date() );
			log.setType( EnumAction.gm.getType() );
			log.setDes( EnumAction.gm.getDes() );
			log.setParam1( "人工入款：" + money );
			log.setParam2( "剩余资金：" + now );
			log.setParam3( "操作人：" + admin_name );
			log.setParam4( "备注：" + Mk );
			log.setParamIp( ip );
			actionLogsMapper.insertMemberActionLogs( log );
			logService.logmarkMoney( userId, oldmemberInfo.getUserName(), EnumMoney.gm, now, total, Mk, markorder );
		} else {
			rspBase.setMsg( "该成员redis未初始化金额，或者您输入的金额有误" );
			rspBase.setCode( 2 );
			return rspBase;
		}
		return rspBase;
	}

    @Override
    public PageBO<WithdrawReport> withdrawReport(String memberid, Integer pageNum, Integer pageSize ) {
        memberInfoMapper.call_pro_useranalysis(memberid);
        PageBO<WithdrawReport> pageBO = new PageBO<>();
        pageNum = 1;
        pageSize = 100;
        Page page   = PageHelper.startPage( pageNum, pageSize, true );
        pageBO.setData( memberInfoMapper.userWithdrawReportList());
        pageBO.setCount( page.getTotal() );
        return pageBO;
    }


    @Override
    public PageBO<MemberCard> findMemberCardPage(String memberid, Integer pageNum, Integer pageSize, String orderBy) {
        PageBO<MemberCard> pageBO = new PageBO<>();
        Page               page   = PageHelper.startPage( pageNum, pageSize, orderBy );
        pageBO.setData( memberCardMapper.findList( memberid ) );
        pageBO.setCount( page.getTotal() );
        return pageBO;
    }
    @Override
    public int updateByPrimaryKeySelective(MemberInfo record) {
        return memberInfoMapper.updateMemberInfo(record);
    }

}
