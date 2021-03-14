package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * //用户信息对象 live_user
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LiveUser extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** ID */
	private Long id;

	/** 用户昵称 */
	@Excel( name = "用户昵称" )
	private String nickName;

	/** 是否认证 0指未认证  1指待审核 2指认证 3指审核不通过 */
	@Excel( name = "是否认证 0指未认证  1指待审核 2指认证 3指审核不通过" )
	private Long isAuthentication;

	/** 登陆IP */
	@Excel( name = "登陆IP" )
	private String loginIp;

	/** 关注的人数 */
	@Excel( name = "关注的人数" )
	private Long focusCount;

	/** 手机号 */
	@Excel( name = "手机号" )
	private String mobile;

	/** 认证类型 */
	@Excel( name = "认证类型" )
	private String authenticationType;

	/** 认证名称 */
	@Excel( name = "认证名称" )
	private String authenticationName;

	/** 手持身份证照片 */
	@Excel( name = "手持身份证照片" )
	private String identifyHoldImage;

	/** 身份证正面 */
	@Excel( name = "身份证正面" )
	private String identifyPositiveImage;

	/** 身份证反面 */
	@Excel( name = "身份证反面" )
	private String identifyNagativeImage;

	/** 审核信息 */
	@Excel( name = "审核信息" )
	private String investorSendInfo;

	/** 粉丝数 */
	@Excel( name = "粉丝数" )
	private Long fansCount;

	/** 印票数 */
	@Excel( name = "印票数" )
	private BigDecimal ticket;

	/** 已提现的印票 */
	@Excel( name = "已提现的印票" )
	private BigDecimal refundTicket;

	/** 禁播状态 0-正常；1-禁播 */
	@Excel( name = "禁播状态 0-正常；1-禁播" )
	private String isBan;

	/** 加v认证说明 */
	@Excel(name = "加v认证说明")
	private String vExplain;

	/** 身份证号码 */
	@Excel( name = "身份证号码" )
	private String identifyNumber;

	/** 家族ID */
	private Long familyId;

	@Excel( name = "家族名称" )
	private String familyName;

	private String familyUserId;
	private String familyNickName;

	/** 时薪 */
	@Excel( name = "时薪" )
	private BigDecimal coin;

	/** 设置主播提现比例,如果为空,则使用后台通用比例 */
	@Excel( name = "设置主播提现比例,如果为空,则使用后台通用比例" )
	private String aloneTicketRatio;

	/** 是否开启付费 0为 禁用 1 为不禁用 */
	@Excel( name = "是否开启付费 0为 禁用 1 为不禁用" )
	private Integer openPay;

	/** 主播禁播原因 */
	@Excel( name = "主播禁播原因" )
	private String banRemark;

	private String loginTime;
	private String logoutTime;
}
