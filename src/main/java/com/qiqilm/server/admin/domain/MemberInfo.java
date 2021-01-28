package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【请填写功能名称】对象 member_info
 *
 * @author 77tv
 * @date 2021-01-25
 */
public class MemberInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberCode;

    /** 会员ID/账号/手机号 */
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

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

    public Integer getGoogleAuthCode() {
        return googleAuthCode;
    }

    public void setGoogleAuthCode(Integer googleAuthCode) {
        this.googleAuthCode = googleAuthCode;
    }

    /** 状态(0= 禁用 1=正常 2=测试号3=超管号) */
    @Excel(name = "状态(0= 禁用 1=正常 2=测试号3=超管号)")
    private Long status;

    /** 会员vip */
    @Excel(name = "会员vip")
    private Long vip;

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
    @Excel(name = "是否在线(1是0否)")
    private Long isOnline;

    /** 性别(1男0女) */
    @Excel(name = "性别(1男0女)")
    private Long sex;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd")
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
    @Excel(name = "登录设备(1 ios 2 android)")
    private Long loginDev;

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
    private Long onlineTime;

    /** 股东网址 */
    @Excel(name = "股东网址")
    private String linkUrl;

    /** 登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date loginTime;

    /** 保险箱余额 */
    @Excel(name = "保险箱余额")
    private BigDecimal boxAccount;

    /** 保险箱密码 */
    @Excel(name = "保险箱密码")
    private String boxPass;

    /** 上次洗码时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上次洗码时间", width = 30, dateFormat = "yyyy-MM-dd")
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
    @Excel(name = "0=正常 1 =禁言")
    private Integer speak;

    /** 登陆次数 */
    @Excel(name = "登陆次数")
    private Long loginNum;

    /** 客户端版本号 */
    @Excel(name = "客户端版本号")
    private String version;

    /** 提现密码(md5加密) */
    @Excel(name = "提现密码(md5加密)")
    private String withdrawalPass;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String deviceId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberCode() {
        return memberCode;
    }
    public void setCxAgent(String cxAgent) {
        this.cxAgent = cxAgent;
    }

    public String getCxAgent() {
        return cxAgent;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }
    public void setVip(Long vip) {
        this.vip = vip;
    }

    public Long getVip() {
        return vip;
    }
    public void setTotalAccount(BigDecimal totalAccount) {
        this.totalAccount = totalAccount;
    }

    public BigDecimal getTotalAccount() {
        return totalAccount;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    public void setIsOnline(Long isOnline) {
        this.isOnline = isOnline;
    }

    public Long getIsOnline() {
        return isOnline;
    }
    public void setSex(Long sex) {
        this.sex = sex;
    }

    public Long getSex() {
        return sex;
    }
    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Date getRegTime() {
        return regTime;
    }
    public void setRegistIp(String registIp) {
        this.registIp = registIp;
    }

    public String getRegistIp() {
        return registIp;
    }
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginIp() {
        return loginIp;
    }
    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    public String getLoginAddress() {
        return loginAddress;
    }
    public void setLoginDev(Long loginDev) {
        this.loginDev = loginDev;
    }

    public Long getLoginDev() {
        return loginDev;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthDay() {
        return birthDay;
    }
    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQq() {
        return qq;
    }
    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWechat() {
        return wechat;
    }
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getHeadImg() {
        return headImg;
    }
    public void setOnlineTime(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Long getOnlineTime() {
        return onlineTime;
    }
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }
    public void setBoxAccount(BigDecimal boxAccount) {
        this.boxAccount = boxAccount;
    }

    public BigDecimal getBoxAccount() {
        return boxAccount;
    }
    public void setBoxPass(String boxPass) {
        this.boxPass = boxPass;
    }

    public String getBoxPass() {
        return boxPass;
    }
    public void setCleanTime(Date cleanTime) {
        this.cleanTime = cleanTime;
    }

    public Date getCleanTime() {
        return cleanTime;
    }
    public void setLevelIntegral(BigDecimal levelIntegral) {
        this.levelIntegral = levelIntegral;
    }

    public BigDecimal getLevelIntegral() {
        return levelIntegral;
    }
    public void setInviterCode(String inviterCode) {
        this.inviterCode = inviterCode;
    }

    public String getInviterCode() {
        return inviterCode;
    }
    public void setInviteMoney(BigDecimal inviteMoney) {
        this.inviteMoney = inviteMoney;
    }

    public BigDecimal getInviteMoney() {
        return inviteMoney;
    }
    public void setCodeAccount(BigDecimal codeAccount) {
        this.codeAccount = codeAccount;
    }

    public BigDecimal getCodeAccount() {
        return codeAccount;
    }
    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getPid() {
        return pid;
    }
    public void setCodeTotal(BigDecimal codeTotal) {
        this.codeTotal = codeTotal;
    }

    public BigDecimal getCodeTotal() {
        return codeTotal;
    }
    public void setChannelcode(String channelcode) {
        this.channelcode = channelcode;
    }

    public String getChannelcode() {
        return channelcode;
    }
    public void setSpeak(Integer speak) {
        this.speak = speak;
    }

    public Integer getSpeak() {
        return speak;
    }
    public void setLoginNum(Long loginNum) {
        this.loginNum = loginNum;
    }

    public Long getLoginNum() {
        return loginNum;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
    public void setWithdrawalPass(String withdrawalPass) {
        this.withdrawalPass = withdrawalPass;
    }

    public String getWithdrawalPass() {
        return withdrawalPass;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

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
            .toString();
    }
}
