package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ConfigDomainCacheUtil;
import com.qiqilm.server.admin.cache.PayCacheUtil;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.PayType;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.PayTypeMapper;
import com.qiqilm.server.admin.service.IPayTypeService;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import com.qiqilm.server.admin.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 支付类型Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Service
public class PayTypeServiceImpl implements IPayTypeService {
	@Autowired
	private PayTypeMapper         payTypeMapper;
	@Autowired
	private TokenService          tokenService;
	@Autowired
	private ConfigDomainCacheUtil configDomainCacheUtil;
	@Autowired
	private PayCacheUtil          payCacheUtil;

	/**
	 * 查询支付类型
	 *
	 * @param id 支付类型ID
	 * @return 支付类型
	 */
	@Override
	public PayType selectPayTypeById( String id ) {
		return payTypeMapper.selectPayTypeById( id );
	}

	/**
	 * 查询支付类型列表
	 *
	 * @param payType 支付类型
	 * @return 支付类型
	 */
	@Override
	public List<PayType> selectPayTypeList( PayType payType ) {
		List<PayType> payTypes = payTypeMapper.selectPayTypeList( payType );
		if ( !CollectionUtils.isEmpty( payTypes ) ) {
			String domainValue = configDomainCacheUtil.getValue( "domain.oss" );
			for ( PayType type : payTypes ) {
				if ( StringUtils.isNotBlank( type.getIconUrl() ) && !type.getIconUrl().startsWith( "http" ) ) {
					type.setIconUrl( domainValue + type.getIconUrl() );
				}
			}
		}
		return payTypes;
	}

	@Override
	public Integer existCode(Integer code) {
		return payTypeMapper.existCode(code);
	}

	/**
	 * 新增支付类型
	 *
	 * @param payType 支付类型
	 * @return 结果
	 */
	@Override
	public int insertPayType( PayType payType ) {
		if ( payType.getCode() == null ) {
			throw new BusinessException( "支付类型编码不能为空" );
		}
		if ( payType.getCode() > 0 ) {
			throw new BusinessException( "支付类型编码必须为负数" );
		}
		if ( payTypeMapper.countByCode( payType.getCode() ) > 0 ) {
			throw new BusinessException( "支付类型编码已存在，请更换" );
		}
		payType.setId( UuidUtil.getRandomUuid() );
		payType.setCreateTime( DateUtils.getNowDate() );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payType.setCreateBy( username );
		payType.setStatus( "0" );

		setPayTypeCache( payType.getId() );
		return payTypeMapper.insertPayType( payType );
	}

	/**
	 * 修改支付类型
	 *
	 * @param payType 支付类型
	 * @return 结果
	 */
	@Override
	public int updatePayType( PayType payType ) {
		payType.setUpdateTime( DateUtils.getNowDate() );
		LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
		String    username  = loginUser.getUsername();
		payType.setUpdator( username );
		payTypeMapper.updatePayType( payType );
		setPayTypeCache( payType.getId() );
		return 1;
	}

	private void setPayTypeCache( String payTypeId ) {
		payCacheUtil.clearPayTypeList();
		payCacheUtil.clearPayType( payTypeId );
	}

	/**
	 * 删除支付类型信息
	 *
	 * @param id 支付类型ID
	 * @return 结果
	 */
	@Override
	public int deletePayTypeById( String id ) {
		int i = payTypeMapper.deletePayTypeById( id );
		if ( i > 0 ) {
			payCacheUtil.clearPayTypeList();
		}
		return i;
	}

}
