package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

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
    @Excel(name = "用户昵称")
    private String nickName;

    /** 用户密码 */
    @Excel(name = "用户密码")
    private String userPwd;

    /** 个性签名 */
    @Excel(name = "个性签名")
    private String signature;

    /** 是否认证 0指未认证  1指待审核 2指认证 3指审核不通过 */
    @Excel(name = "是否认证 0指未认证  1指待审核 2指认证 3指审核不通过")
    private Long isAuthentication;

    /** 0：微信；1：QQ；2：手机；3：微博 ;4 : 游客登录 */
    @Excel(name = "0：微信；1：QQ；2：手机；3：微博 ;4 : 游客登录")
    private Long loginType;

    /** 有效性标识 */
    @Excel(name = "有效性标识")
    private Integer isEffect;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal money;

    /** 登陆IP */
    @Excel(name = "登陆IP")
    private String loginIp;

    /** 省 */
    @Excel(name = "省")
    private String province;

    /** 市 */
    @Excel(name = "市")
    private String city;

    /** 能否修改性别 1为可修改 0为不可修改 */
    @Excel(name = "能否修改性别 1为可修改 0为不可修改")
    private Integer isEditSex;

    /** 性别 0:未知, 1-男，2-女 */
    @Excel(name = "性别 0:未知, 1-男，2-女")
    private Long sex;

    /** 出生日期 */
    @Excel(name = "出生日期")
    private Long birthday;

    /** 接受推送消息 0-不接收，1-接收 */
    @Excel(name = "接受推送消息 0-不接收，1-接收")
    private Integer isRemind;

    /** 关注的人数 */
    @Excel(name = "关注的人数")
    private Long focusCount;

    /** 个人简介 */
    @Excel(name = "个人简介")
    private String intro;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 新浪ID */
    @Excel(name = "新浪ID")
    private String sinaId;

    /** 新浪令牌 */
    @Excel(name = "新浪令牌")
    private String sinaToken;

    /** 新浪密码 */
    @Excel(name = "新浪密码")
    private String sinaSecret;

    /** 新浪跳转地址 */
    @Excel(name = "新浪跳转地址")
    private String sinaUrl;

    /** 腾讯ID */
    @Excel(name = "腾讯ID")
    private String tencentId;

    /** 腾讯令牌 */
    @Excel(name = "腾讯令牌")
    private String tencentToken;

    /** 腾讯密码 */
    @Excel(name = "腾讯密码")
    private String tencentSecret;

    /** 腾讯跳转地址 */
    @Excel(name = "腾讯跳转地址")
    private String tencentUrl;

    /** 验证 */
    @Excel(name = "验证")
    private String verify;

    /** 用户等级;live_user_level.level */
    @Excel(name = "用户等级;live_user_level.level")
    private Long userLevel;

    /** 手机号 */
    @Excel(name = "手机号")
    private String mobile;

    /** 用户类型 0指 普通用户 ，1指企业会员 */
    @Excel(name = "用户类型 0指 普通用户 ，1指企业会员")
    private Long userType;

    /** 是否发送成功 */
    @Excel(name = "是否发送成功")
    private Integer isHasSendSuccess;

    /** 设置时间 */
    @Excel(name = "设置时间")
    private Long verifySettingTime;

    /** 认证类型 */
    @Excel(name = "认证类型")
    private String authenticationType;

    /** 认证名称 */
    @Excel(name = "认证名称")
    private String authenticationName;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String contact;

    /** 来自平台 */
    @Excel(name = "来自平台")
    private String fromPlatform;

    /** 百度百科 */
    @Excel(name = "百度百科")
    private String wiki;

    /** 手持身份证照片 */
    @Excel(name = "手持身份证照片")
    private String identifyHoldImage;

    /** 身份证正面 */
    @Excel(name = "身份证正面")
    private String identifyPositiveImage;

    /** 身份证反面 */
    @Excel(name = "身份证反面")
    private String identifyNagativeImage;

    /** 微信openid */
    @Excel(name = "微信openid")
    private String wxOpenid;

    /** 公众号的微信openid */
    @Excel(name = "公众号的微信openid")
    private String gzOpenid;

    /** QQopenid */
    @Excel(name = "QQopenid")
    private String qqOpenid;

    /** 审核信息 */
    @Excel(name = "审核信息")
    private String investorSendInfo;

    /** 提现和支付密码 */
    @Excel(name = "提现和支付密码")
    private String paypassword;

    /** 来源url */
    @Excel(name = "来源url")
    private String sourceUrl;

    /** 推荐人id */
    @Excel(name = "推荐人id")
    private Long pid;

    /** 积分 */
    @Excel(name = "积分")
    private Long score;

    /** 信用值 */
    @Excel(name = "信用值")
    private Long point;

    /** 情感状态 */
    @Excel(name = "情感状态")
    private String emotionalState;

    /** 职位 */
    @Excel(name = "职位")
    private String job;

    /** 用户头像 */
    @Excel(name = "用户头像")
    private String headImage;

    /** 头像缩略图 */
    @Excel(name = "头像缩略图")
    private String thumbHeadImage;

    /** QQID */
    @Excel(name = "QQID")
    private String qqId;

    /** QQ令牌 */
    @Excel(name = "QQ令牌")
    private String qqToken;

    /** 认证类型:0: 未认证;1:普通认证;2:企业认证;3:支付宝认证 */
    @Excel(name = "认证类型:0: 未认证;1:普通认证;2:企业认证;3:支付宝认证")
    private Long vType;

    /** 加v认证说明 */
    @Excel(name = "加v认证说明")
    private String vExplain;

    /** 认证图标 */
    @Excel(name = "认证图标")
    private String vIcon;

    /** 粉丝数 */
    @Excel(name = "粉丝数")
    private Long fansCount;

    /** 印票数 */
    @Excel(name = "印票数")
    private BigDecimal ticket;

    /** 已提现的印票 */
    @Excel(name = "已提现的印票")
    private BigDecimal refundTicket;

    /** 当前还剩钻石数 */
    @Excel(name = "当前还剩钻石数")
    private BigDecimal diamonds;

    /** 累计消费的钻石数 */
    @Excel(name = "累计消费的钻石数")
    private BigDecimal useDiamonds;

    /** 用户签名 */
    @Excel(name = "用户签名")
    private String usersig;

    /** 用户签名过期时间 */
    @Excel(name = "用户签名过期时间")
    private Long expiryAfter;

    /** 1:用户在线;0:不在线;通过服务端监听更新 */
    @Excel(name = "1:用户在线;0:不在线;通过服务端监听更新")
    private Integer isOnline;

    /** 微信unionid */
    @Excel(name = "微信unionid")
    private String wxUnionid;

    /** 微信昵称 */
    @Excel(name = "微信昵称")
    private String userName;

    /** 是否同步成功 : 0指同步失败 ， 1指同步成功 */
    @Excel(name = "是否同步成功 : 0指同步失败 ， 1指同步成功")
    private Integer synchronize;

    /** 最近上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最近上线时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date loginTime;

    /** 最近下线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最近下线时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date logoutTime;

    /** 是否同意直播协议 0 表示不同意 1表示同意 */
    @Excel(name = "是否同意直播协议 0 表示不同意 1表示同意")
    private Integer isAgree;

    /** 用户总的观看时间，单位为秒 */
    @Excel(name = "用户总的观看时间，单位为秒")
    private Long onlineTime;

    /** 是否关注公众号 0未关注 1已关注 */
    @Excel(name = "是否关注公众号 0未关注 1已关注")
    private Integer subscribe;

    /** 1:机器人 */
    @Excel(name = "1:机器人")
    private Integer isRobot;

    /** 友盟消息推送服务对设备的唯一标识。Android的device_token是44位字符串, iOS的device-token是64位。 */
    @Excel(name = "友盟消息推送服务对设备的唯一标识。Android的device_token是44位字符串, iOS的device-token是64位。")
    private String apnsCode;

    /** 1:android; 2:ios */
    @Excel(name = "1:android; 2:ios")
    private Long deviceType;

    /** 可回看的直播数量 */
    @Excel(name = "可回看的直播数量")
    private Long videoCount;

    /** 禁播状态 0-正常；1-禁播 */
    @Excel(name = "禁播状态 0-正常；1-禁播")
    private Integer isBan;

    public String getIsBanStr() {
        return isBanStr;
    }

    public void setIsBanStr(String isBanStr) {
        this.isBanStr = isBanStr;
    }

    /** 禁播状态 0-正常；1-禁播 */
    @Excel(name = "禁播状态 0-正常；1-禁播")
    private String isBanStr;

    /** 禁播结束时间 */
    @Excel(name = "禁播结束时间")
    private Long banTime;

    /** 身份证号码 */
    @Excel(name = "身份证号码")
    private String identifyNumber;

    /** live_authent_list.id 认证ID */
    @Excel(name = "live_authent_list.id 认证ID")
    private Long authentListId;

    /** 家族ID */
    @Excel(name = "家族ID")
    private Long familyId;

    /** 是否家族长；0：否、1：是 */
    @Excel(name = "是否家族长；0：否、1：是")
    private Integer familyChieftain;

    /** 是否系统管理员 1-是 ；0-否 */
    @Excel(name = "是否系统管理员 1-是 ；0-否")
    private Integer isAdmin;

    /** 上一次登录时的用户等级 */
    @Excel(name = "上一次登录时的用户等级")
    private Long lastLoginLevel;

    /** $column.columnComment */
    @Excel(name = "上一次登录时的用户等级")
    private Integer bindingAlipay;

    /** $column.columnComment */
    @Excel(name = "上一次登录时的用户等级")
    private String alipayName;

    /** $column.columnComment */
    @Excel(name = "上一次登录时的用户等级")
    private String alipayAccount;

    /** 直播间名称 */
    @Excel(name = "直播间名称")
    private String roomTitle;

    /** 靓号号码 */
    @Excel(name = "靓号号码")
    private Long luckNum;

    /** 支付宝认证的支付宝用户号 */
    @Excel(name = "支付宝认证的支付宝用户号")
    private String alipayUserId;

    /** 支付宝认证 token */
    @Excel(name = "支付宝认证 token")
    private String alipayAuthentToken;

    /** 分销功能 上级用户ID */
    @Excel(name = "分销功能 上级用户ID")
    private Long pUserId;

    /** 禁播类型  0 禁止单账号直播, 1 禁止同IP直播，2 禁止同设备直播 */
    @Excel(name = "禁播类型  0 禁止单账号直播, 1 禁止同IP直播，2 禁止同设备直播")
    private Long banType;

    /** 公会id */
    @Excel(name = "公会id")
    private Long societyId;

    /** 是否公会长；0：否、1：是 */
    @Excel(name = "是否公会长；0：否、1：是")
    private Integer societyChieftain;

    /** 结算方式；0：对私结算、1：对公结算 */
    @Excel(name = "结算方式；0：对私结算、1：对公结算")
    private Integer societySettlementType;

    /** 是否VIP会员；0：否、1：是 */
    @Excel(name = "是否VIP会员；0：否、1：是")
    private Integer isVip;

    /** 会员到期时间 */
    @Excel(name = "会员到期时间")
    private Long vipExpireTime;

    /** 时薪 */
    @Excel(name = "时薪")
    private BigDecimal coin;

    /** 是否全局永久禁言 1-是，0-否 */
    @Excel(name = "是否全局永久禁言 1-是，0-否")
    private Integer isNospeaking;

    /** 公会主播上缴的印票 */
    @Excel(name = "公会主播上缴的印票")
    private Long societyTicket;

    /** 累计观看次数;累计观看次数明显大于其它用户观看次数时，及有可能是盗连接用户，需要禁用它 */
    @Excel(name = "累计观看次数;累计观看次数明显大于其它用户观看次数时，及有可能是盗连接用户，需要禁用它")
    private Long viewCount;

    /** 禁热门 0-正常；1-禁止 */
    @Excel(name = "禁热门 0-正常；1-禁止")
    private Integer isHotOn;

    /** 干预系数，0-100,0表示无干预，全部随机结果、100表示完全干预，收益最大 */
    @Excel(name = "干预系数，0-100,0表示无干预，全部随机结果、100表示完全干预，收益最大")
    private Long rate;

    /** 上级分销者ID */
    @Excel(name = "上级分销者ID")
    private Long gameDistributionId;

    /** 游戏一级分销抽成比例 */
    @Excel(name = "游戏一级分销抽成比例")
    private Long gameDistribution1;

    /** 游戏二级分销抽成比例 */
    @Excel(name = "游戏二级分销抽成比例")
    private Long gameDistribution2;

    /** 邀请码 */
    @Excel(name = "邀请码")
    private String invitationCode;

    /** 邀请人 */
    @Excel(name = "邀请人")
    private Long invitationId;

    /** $column.columnComment */
    @Excel(name = "邀请人")
    private Long gameDistributionTopId;

    /** 审核失败时间 */
    @Excel(name = "审核失败时间")
    private Long investorTime;

    /** 设置主播提现比例,如果为空,则使用后台通用比例 */
    @Excel(name = "设置主播提现比例,如果为空,则使用后台通用比例")
    private String aloneTicketRatio;

    /** 是否开启游戏  0为 禁用 1 为不禁用 */
    @Excel(name = "是否开启游戏  0为 禁用 1 为不禁用")
    private Integer openGame;

    /** 是否开启付费 0为 禁用 1 为不禁用 */
    @Excel(name = "是否开启付费 0为 禁用 1 为不禁用")
    private Integer openPay;

    /** 是否开启竞拍  0为 禁用 1 为不禁用 */
    @Excel(name = "是否开启竞拍  0为 禁用 1 为不禁用")
    private Integer openAuction;

    /** 家族推荐号 填写后审核通过自动加入相对应的家族 */
    @Excel(name = "家族推荐号 填写后审核通过自动加入相对应的家族")
    private String familyRecom;

    /** 分类id */
    @Excel(name = "分类id")
    private Long classifiedId;

    /** 通联支付的用户ID(在通联网站的注册的userID) */
    @Excel(name = "通联支付的用户ID(在通联网站的注册的userID)")
    private String allinpayUserId;

    /** 特权机器人送出的亲贝（不可提现） */
    @Excel(name = "特权机器人送出的亲贝", readConverterExp = "不=可提现")
    private Long noTicket;

    /** 0未开启机器人礼物账号特权，1开启机器人礼物账号特权 */
    @Excel(name = "0未开启机器人礼物账号特权，1开启机器人礼物账号特权")
    private Integer roboter;

    /** 是否修改过昵称（1=是，0否） */
    @Excel(name = "是否修改过昵称", readConverterExp = "1==是，0否")
    private Long isChangeName;

    /** 是否过更换QQ头像 ，0 否；1是 */
    @Excel(name = "是否过更换QQ头像 ，0 否；1是")
    private Integer isReplaceQq;

    /** 是否过更换微信头像 ，0 否；1是 */
    @Excel(name = "是否过更换微信头像 ，0 否；1是")
    private Integer isReplaceWx;

    /** 动态数 */
    @Excel(name = "动态数")
    private Long weiboCount;

    /** 动态权重 */
    @Excel(name = "动态权重")
    private Long weiboSortNum;

    /** 推荐权重 */
    @Excel(name = "推荐权重")
    private Long weiboRecommendWeight;

    /** 微信账号 */
    @Excel(name = "微信账号")
    private String weixinAccount;

    /** 微信价格 */
    @Excel(name = "微信价格")
    private BigDecimal weixinPrice;

    /** 经度 */
    @Excel(name = "经度")
    private String xpoint;

    /** 纬度 */
    @Excel(name = "纬度")
    private String ypoint;

    /** 展示图片列表 */
    @Excel(name = "展示图片列表")
    private String showImage;

    /** 已提现金额 */
    @Excel(name = "已提现金额")
    private BigDecimal weiboRefundMoney;

    /** 主播获得的金额 */
    @Excel(name = "主播获得的金额")
    private BigDecimal weiboMoney;

    /** 被举报的次数 */
    @Excel(name = "被举报的次数")
    private Long tipoffCount;

    /** 会员中心海报 */
    @Excel(name = "会员中心海报")
    private String weiboPhotoImg;

    /** 聊天价格 */
    @Excel(name = "聊天价格")
    private BigDecimal weiboChatPrice;

    /** 微信更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "微信更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date weixinAccountTime;

    /** 分成计划中的推广人id */
    @Excel(name = "分成计划中的推广人id")
    private Long autoInvitationSid;

    /** 代理有后台的id */
    @Excel(name = "代理有后台的id")
    private Long agentId;

    /** 座驾id */
    @Excel(name = "座驾id")
    private Long nobleCarId;

    /** 是否开启送礼物主播没收益0否1是 */
    @Excel(name = "是否开启送礼物主播没收益0否1是")
    private Long isGiftGiving;

    /** 分销（好像没有用到这个分销） */
    @Excel(name = "分销", readConverterExp = "好=像没有用到这个分销")
    private String qrCode;

    /** $column.columnComment */
    @Excel(name = "分销", readConverterExp = "$column.readConverterExp()")
    private String totalTime;

    /** 邀请审核 */
    @Excel(name = "邀请审核")
    private Long pUserShenhe;

    /** 主播提现比例 */
    @Excel(name = "主播提现比例")
    private BigDecimal ticketCattyRatio;
    /**
     * 发送开始时间
     */
    private String sendStartTime;

    /**
     * 发送结束时间
     */
    private String sendEndTime;
    /** 主播禁播原因 */
    @Excel(name = "主播禁播原因")
    private String banRemark;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("nickName", getNickName())
            .append("userPwd", getUserPwd())
            .append("signature", getSignature())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("isAuthentication", getIsAuthentication())
            .append("loginType", getLoginType())
            .append("isEffect", getIsEffect())
            .append("money", getMoney())
            .append("loginIp", getLoginIp())
            .append("province", getProvince())
            .append("city", getCity())
            .append("isEditSex", getIsEditSex())
            .append("sex", getSex())
            .append("birthday", getBirthday())
            .append("isRemind", getIsRemind())
            .append("focusCount", getFocusCount())
            .append("intro", getIntro())
            .append("code", getCode())
            .append("sinaId", getSinaId())
            .append("sinaToken", getSinaToken())
            .append("sinaSecret", getSinaSecret())
            .append("sinaUrl", getSinaUrl())
            .append("tencentId", getTencentId())
            .append("tencentToken", getTencentToken())
            .append("tencentSecret", getTencentSecret())
            .append("tencentUrl", getTencentUrl())
            .append("verify", getVerify())
            .append("userLevel", getUserLevel())
            .append("mobile", getMobile())
            .append("userType", getUserType())
            .append("isHasSendSuccess", getIsHasSendSuccess())
            .append("verifySettingTime", getVerifySettingTime())
            .append("authenticationType", getAuthenticationType())
            .append("authenticationName", getAuthenticationName())
            .append("contact", getContact())
            .append("fromPlatform", getFromPlatform())
            .append("wiki", getWiki())
            .append("identifyHoldImage", getIdentifyHoldImage())
            .append("identifyPositiveImage", getIdentifyPositiveImage())
            .append("identifyNagativeImage", getIdentifyNagativeImage())
            .append("wxOpenid", getWxOpenid())
            .append("gzOpenid", getGzOpenid())
            .append("qqOpenid", getQqOpenid())
            .append("investorSendInfo", getInvestorSendInfo())
            .append("paypassword", getPaypassword())
            .append("sourceUrl", getSourceUrl())
            .append("pid", getPid())
            .append("score", getScore())
            .append("point", getPoint())
            .append("emotionalState", getEmotionalState())
            .append("job", getJob())
            .append("headImage", getHeadImage())
            .append("thumbHeadImage", getThumbHeadImage())
            .append("qqId", getQqId())
            .append("qqToken", getQqToken())
            .append("vType", getVType())
            .append("vExplain", getVExplain())
            .append("vIcon", getVIcon())
            .append("fansCount", getFansCount())
            .append("ticket", getTicket())
            .append("refundTicket", getRefundTicket())
            .append("diamonds", getDiamonds())
            .append("useDiamonds", getUseDiamonds())
            .append("usersig", getUsersig())
            .append("expiryAfter", getExpiryAfter())
            .append("isOnline", getIsOnline())
            .append("wxUnionid", getWxUnionid())
            .append("userName", getUserName())
            .append("synchronize", getSynchronize())
            .append("loginTime", getLoginTime())
            .append("logoutTime", getLogoutTime())
            .append("isAgree", getIsAgree())
            .append("onlineTime", getOnlineTime())
            .append("subscribe", getSubscribe())
            .append("isRobot", getIsRobot())
            .append("apnsCode", getApnsCode())
            .append("deviceType", getDeviceType())
            .append("videoCount", getVideoCount())
            .append("isBan", getIsBan())
            .append("banTime", getBanTime())
            .append("identifyNumber", getIdentifyNumber())
            .append("authentListId", getAuthentListId())
            .append("familyId", getFamilyId())
            .append("familyChieftain", getFamilyChieftain())
            .append("isAdmin", getIsAdmin())
            .append("lastLoginLevel", getLastLoginLevel())
            .append("bindingAlipay", getBindingAlipay())
            .append("alipayName", getAlipayName())
            .append("alipayAccount", getAlipayAccount())
            .append("roomTitle", getRoomTitle())
            .append("luckNum", getLuckNum())
            .append("alipayUserId", getAlipayUserId())
            .append("alipayAuthentToken", getAlipayAuthentToken())
            .append("pUserId", getPUserId())
            .append("banType", getBanType())
            .append("societyId", getSocietyId())
            .append("societyChieftain", getSocietyChieftain())
            .append("societySettlementType", getSocietySettlementType())
            .append("isVip", getIsVip())
            .append("vipExpireTime", getVipExpireTime())
            .append("coin", getCoin())
            .append("isNospeaking", getIsNospeaking())
            .append("societyTicket", getSocietyTicket())
            .append("viewCount", getViewCount())
            .append("isHotOn", getIsHotOn())
            .append("rate", getRate())
            .append("gameDistributionId", getGameDistributionId())
            .append("gameDistribution1", getGameDistribution1())
            .append("gameDistribution2", getGameDistribution2())
            .append("invitationCode", getInvitationCode())
            .append("invitationId", getInvitationId())
            .append("gameDistributionTopId", getGameDistributionTopId())
            .append("investorTime", getInvestorTime())
            .append("aloneTicketRatio", getAloneTicketRatio())
            .append("openGame", getOpenGame())
            .append("openPay", getOpenPay())
            .append("openAuction", getOpenAuction())
            .append("familyRecom", getFamilyRecom())
            .append("classifiedId", getClassifiedId())
            .append("allinpayUserId", getAllinpayUserId())
            .append("noTicket", getNoTicket())
            .append("roboter", getRoboter())
            .append("isChangeName", getIsChangeName())
            .append("isReplaceQq", getIsReplaceQq())
            .append("isReplaceWx", getIsReplaceWx())
            .append("weiboCount", getWeiboCount())
            .append("weiboSortNum", getWeiboSortNum())
            .append("weiboRecommendWeight", getWeiboRecommendWeight())
            .append("weixinAccount", getWeixinAccount())
            .append("weixinPrice", getWeixinPrice())
            .append("xpoint", getXpoint())
            .append("ypoint", getYpoint())
            .append("showImage", getShowImage())
            .append("weiboRefundMoney", getWeiboRefundMoney())
            .append("weiboMoney", getWeiboMoney())
            .append("tipoffCount", getTipoffCount())
            .append("weiboPhotoImg", getWeiboPhotoImg())
            .append("weiboChatPrice", getWeiboChatPrice())
            .append("weixinAccountTime", getWeixinAccountTime())
            .append("autoInvitationSid", getAutoInvitationSid())
            .append("agentId", getAgentId())
            .append("nobleCarId", getNobleCarId())
            .append("isGiftGiving", getIsGiftGiving())
            .append("qrCode", getQrCode())
            .append("totalTime", getTotalTime())
            .append("pUserShenhe", getPUserShenhe())
            .append("ticketCattyRatio", getTicketCattyRatio())
            .append("banRemark", getBanRemark())
            .toString();
    }
}
