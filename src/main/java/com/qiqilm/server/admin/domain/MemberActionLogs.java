package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import com.qiqilm.server.admin.enums.EnumAction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 会员行为日志对象 log_member_action
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class MemberActionLogs extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	/**
	 * 会员编号
	 */
	@Excel( name = "会员编号" )
	private String userId;

	/**
	 * 账号
	 */
	@Excel( name = "账号" )
	private String userName;

	/**
	 * 行为类型
	 */
	@Excel( name = "行为类型" )
	private Integer type;

	/**
	 * 描述
	 */
	@Excel( name = "描述" )
	private String des;

	/**
	 * 创建时间
	 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "创建时间", width = 30, exportFormat = "yyyy-MM-dd" )
	private Date cTime;

	private String param1;

	private String param2;

	private String param3;

	private String param4;

	/**
	 * IP参数
	 */
	@Excel( name = "IP参数" )
	private String paramIp;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId( String userId ) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName( String userName ) {
		this.userName = userName;
	}

	public Integer getType() {
		return type;
	}

	public void setType( Integer type ) {
		this.type = type;
	}

	public String getDes() {
		return des;
	}

	public void setDes( String des ) {
		this.des = des;
	}

	public Date getcTime() {
		return cTime;
	}

	public void setcTime( Date cTime ) {
		this.cTime = cTime;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1( String param1 ) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2( String param2 ) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3( String param3 ) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4( String param4 ) {
		this.param4 = param4;
	}

	public String getParamIp() {
		return paramIp;
	}

	public void setParamIp( String paramIp ) {
		this.paramIp = paramIp;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "userId", getUserId() )
				.append( "userName", getUserName() )
				.append( "type", getType() )
				.append( "des", getDes() )
				.append( "cTime", getcTime() )
				.append( "param1", getParam1() )
				.append( "param2", getParam2() )
				.append( "param3", getParam3() )
				.append( "param4", getParam4() )
				.append( "paramIp", getParamIp() )
				.toString();
	}
}