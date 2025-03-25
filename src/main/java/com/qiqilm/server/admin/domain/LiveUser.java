package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * //用户信息对象 live_user
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Data
public class LiveUser extends DownLoadTime {
	private static final long serialVersionUID = 1L;

	/** ID */
	@Excel( name = "主播ID" )
	private Long id;

	/** 用户昵称 */
	@Excel( name = "主播昵称", orderNum = "1")
	private String nickName;

	/** 是否认证 0指未认证  1指待审核 2指认证 3指审核不通过 */
	private Integer isAuthentication;

	@Excel( name = "认证状态", orderNum = "4" )
	private String authStr;

	/** 登陆IP */
	@Excel( name = "登陆IP", orderNum = "5" )
	private String loginIp;

    /** 头像地址 */
    private String headImage;

	/** 关注的人数 */
	@Excel( name = "关注的人数", orderNum = "9" )
	private Long focusCount;

    private Date createTime;
	/** 手机号 */
	@Excel( name = "手机号", orderNum = "6" )
	private String mobile;

	/** 认证类型 */
	@Excel( name = "认证类型", orderNum = "7" )
	private String authenticationType;

	/** 认证名称 */
	@Excel( name = "认证名称", orderNum = "8" )
	private String authenticationName;

	/** 手持身份证照片 */
	private String identifyHoldImage;

	/** 身份证正面 */
	private String identifyPositiveImage;

	/** 身份证反面 */
	private String identifyNagativeImage;

	/** 审核信息 */
	private String investorSendInfo;

	/** 粉丝数 */
	@Excel( name = "粉丝数", orderNum = "10" )
	private Long fansCount;

	/** 印票数 */
	@Excel( name = "印票数", orderNum = "11" )
	private BigDecimal ticket;

	/** 已提现的印票 */
	@Excel( name = "已提现的印票", orderNum = "12" )
	private BigDecimal refundTicket;

	/** 禁播状态 0-正常；1-禁播 */
	private Integer isBan;

	@Excel( name = "禁播状态", orderNum = "13" )
	private String banStr;

	/** 加v认证说明 */
	private String vExplain;

	/** 身份证号码 */
	private String identifyNumber;

	/** 家族ID */
	@Excel( name = "家族ID", orderNum = "2" )
	private Long familyId;

	private Long expiryAfter;

	@Excel( name = "家族名称", orderNum = "3" )
	private String familyName;

	private String familyUserId;
	private String familyNickName;

	/** 时薪 */
	@Excel( name = "时薪", orderNum = "16" )
	private BigDecimal coin;
	/** 时薪 */
	@Excel( name = "彩票抽成", orderNum = "17" )
	private BigDecimal xpoint;
	/** 时薪 */
	@Excel( name = "礼物抽成", orderNum = "18" )
	private BigDecimal ypoint;

	/** 设置主播提现比例,如果为空,则使用后台通用比例 */
	@Excel( name = "主播提现比例", orderNum = "19" )
	private BigDecimal ticketCattyRatio;

	/** 是否开启付费 0为 禁用 1 为不禁用 */
	private Integer openPay;

	@Excel( name = "付费状态", orderNum = "15" )
	private String openPayStr;

	/** 主播禁播原因 */
	@Excel( name = "主播禁播原因", orderNum = "15" )
	private String banRemark;

	/** 主播禁播原因 */
	@Excel( name = "礼物任务", orderNum = "15" )
	private BigDecimal weiboMoney;

	/** 主播禁播原因 */
	@Excel( name = "时薪任务", orderNum = "15" )
	private BigDecimal weixinPrice;

	/** password */
	@Excel( name = "userPass", orderNum = "15" )
	private String userPass;

	private String loginTime;
	private String logoutTime;

	private Date updateTime;
	private Integer roboter;
    private Integer familyChieftain;
    private Integer liveIn;

    private String realName;
	private String bankAccount;
    private String bankName;
    private Long bankTypeId;
    private String weixinAccount;
	private Integer googleAuthCode;
	private String paypassword;
	private String qqId;
	private String qqToken;

	private String userIds;
	private Set<String> userIdSet;

	private Integer virtualAnchor;

	public Set<String> getUserIdSet() {
		if(StringUtils.isNotBlank(userIds)){
			String[] strings = userIds.split(",");
			Set<String> userIdsSet = new HashSet<>();
			for (String s : strings) {
				if(StringUtils.isNotBlank(s)){
					userIdsSet.add(s.trim());
				}
			}
			return userIdsSet;
		}
		return userIdSet;
	}

	public String getAuthStr() {
		if ( isAuthentication != null ) {
			switch ( isAuthentication ) {
			case 0:
				return "未认证";
			case 1:
				return "待审核";
			case 2:
				return "已认证";
			case 3:
				return "审核不通过";
			default:
			}
		}
		return "";
	}

	public String getBanStr() {
		if ( isBan != null ) {
			switch ( isBan ) {
			case 0:
				return "正常";
			case 1:
				return "禁播";
			default:
			}
		}
		return "";
	}

	public String getOpenPayStr() {
		if ( openPay != null ) {
			switch ( openPay ) {
			case 0:
				return "禁用";
			case 1:
				return "开启";
			default:
			}
		}
		return "";
	}
}
