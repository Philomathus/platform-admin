package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员信息对象 member_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class MemberInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberCode;

    /** 会员ID/账号/手机号 */
    private String code;

    /** 会员编号 */
    @Excel(name = "银行卡号")
    private String bankAccount;

    /** 代理编号 */
    @Excel(name = "代理编号")
    private String cxAgent;

    /** 账号 */
    @Excel(name = "账号")
    private String userName;

    private Integer googleAuthCode;

    /** 姓名 */
    @Excel(name = "姓名")
    private String realName;

    /** 状态(0= 禁用 1=正常 2=测试号3=超管号) */
    @Excel(name = "状态")
    private Integer status;

    /** 会员vip */
    @Excel(name = "会员vip")
    private Integer vip;

    /** 余额 */
    @Excel(name = "余额")
    private BigDecimal totalAccount;

    /** 手机 */
    @Excel(name = "手机")
    private String phone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 是否在线(1是0否) */
    @Excel(name = "是否在线")
    private Integer isOnline;

    /** 性别(1男0女) */
    @Excel(name = "性别")
    private Integer sex;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date regTime;

    /** 注册ip */
    @Excel(name = "注册ip")
    private String registIp;

    /** 登录ip */
    @Excel(name = "登录ip")
    private String loginIp;

    /** 登录地址 */
    @Excel(name = "登录地址")
    private String loginAddress;

    /** 登录设备(1 ios 2 android) */
    @Excel(name = "登录设备")
    private Integer loginDev;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickName;

    /** 生日 */
    @Excel(name = "生日")
    private String birthDay;

    /** qq */
    @Excel(name = "qq")
    private String qq;

    /** 微信 */
    @Excel(name = "微信")
    private String wechat;

    /** 头像 */
    @Excel(name = "头像")
    private String headImg;

    /** 在线时长 */
    @Excel(name = "在线时长")
    private Integer onlineTime;

    /** 股东网址 */
    @Excel(name = "股东网址")
    private String linkUrl;

    /** 登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "登录时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /** 保险箱余额 */
    @Excel(name = "保险箱余额")
    private BigDecimal boxAccount;

    /** 保险箱密码 */
    @Excel(name = "保险箱密码")
    private String boxPass;

    /** 上次洗码时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上次洗码时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date cleanTime;

    /** 总的充值金额 */
    @Excel(name = "总的充值金额")
    private BigDecimal levelIntegral;

    /** 邀请码 */
    @Excel(name = "邀请码")
    private String inviterCode;

    /** 佣金 */
    @Excel(name = "佣金")
    private BigDecimal inviteMoney;

    /** 打码账户 */
    @Excel(name = "打码账户")
    private BigDecimal codeAccount;

    /** 玩家所在游戏平台 */
    @Excel(name = "玩家所在游戏平台")
    private Long pid;

    /** 累计有效投注 */
    @Excel(name = "累计有效投注")
    private BigDecimal codeTotal;

    /** 渠道号 */
    @Excel(name = "渠道号")
    private String channelcode;

    /** 0=正常 1 =禁言 */
    @Excel(name = "是否禁言")
    private String speak;

    /** 登陆次数 */
    @Excel(name = "登陆次数")
    private Integer loginNum;

    /** 客户端版本号 */
    @Excel(name = "客户端版本号")
    private String version;

    /** 提现密码(md5加密) */
    @Excel(name = "提现密码")
    private String withdrawalPass;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String deviceId;
    @Excel(name = "im禁言时间")
    private int banSpeakTime;
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberCode", getMemberCode())
            .append("cxAgent", getCxAgent())
            .append("userName", getUserName())
            .append("realName", getRealName())
            .append("status", getStatus())
            .append("vip", getVip())
            .append("totalAccount", getTotalAccount())
            .append("phone", getPhone())
            .append("email", getEmail())
            .append("password", getPassword())
            .append("isOnline", getIsOnline())
            .append("sex", getSex())
            .append("regTime", getRegTime())
            .append("registIp", getRegistIp())
            .append("loginIp", getLoginIp())
            .append("loginAddress", getLoginAddress())
            .append("loginDev", getLoginDev())
            .append("nickName", getNickName())
            .append("birthDay", getBirthDay())
            .append("qq", getQq())
            .append("wechat", getWechat())
            .append("headImg", getHeadImg())
            .append("onlineTime", getOnlineTime())
            .append("linkUrl", getLinkUrl())
            .append("loginTime", getLoginTime())
            .append("boxAccount", getBoxAccount())
            .append("boxPass", getBoxPass())
            .append("cleanTime", getCleanTime())
            .append("levelIntegral", getLevelIntegral())
            .append("inviterCode", getInviterCode())
            .append("inviteMoney", getInviteMoney())
            .append("codeAccount", getCodeAccount())
            .append("pid", getPid())
            .append("codeTotal", getCodeTotal())
            .append("channelcode", getChannelcode())
            .append("speak", getSpeak())
            .append("loginNum", getLoginNum())
            .append("version", getVersion())
            .append("withdrawalPass", getWithdrawalPass())
            .append("deviceId", getDeviceId())
            .append("banSpeakTime", getBanSpeakTime())
            .toString();
    }
}
