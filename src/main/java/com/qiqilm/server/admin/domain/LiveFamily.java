package com.qiqilm.server.admin.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 家族对象 live_family
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class LiveFamily extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 家族LOGO */
    @Excel(name = "家族LOGO")
    private String logo;

    /** 家族名称 */
    @Excel(name = "家族名称")
    private String name;

    /** 公告 */
    @Excel(name = "公告")
    private String notice;

    /** 家族宣言 */
    @Excel(name = "家族宣言")
    private String manifesto;

    /** 家族长昵称 */
    @Excel(name = "家族长昵称")
    private String nickName;

    /** 家族长ID */
    @Excel(name = "家族长ID")
    private Long userId;

    /** 成员数量 */
    @Excel(name = "成员数量")
    private Long userCount;

    /** 日期字段,按日期归档 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期字段,按日期归档", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 年 */
    @Excel(name = "年")
    private Long createY;

    /** 月 */
    @Excel(name = "月")
    private Long createM;

    /** 日 */
    @Excel(name = "日")
    private Long createD;

    /** 周 */
    @Excel(name = "周")
    private Long createW;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 状态，0未审核，1审核通过，2拒绝通过 4 解散 */
    @Excel(name = "状态，0未审核，1审核通过，2拒绝通过 4 解散")
    private Integer status;

    /** 家族成员的贡献 */
    @Excel(name = "家族成员的贡献")
    private Long contribution;

    /** 家族等级;live_family_level.level */
    @Excel(name = "家族等级;live_family_level.level")
    private Long familyLevel;

    /** 家族总的直播时间，单位为秒 */
    @Excel(name = "家族总的直播时间，单位为秒")
    private Long videoTime;

    /** 积分 */
    @Excel(name = "积分")
    private Long score;

    /** 家族等级;live_family_level.level */
    @Excel(name = "家族等级;live_family_level.level")
    private Long liveLevel;

    /** 家族推荐号 创建家族后随机生成，用于主播审核时填写 */
    @Excel(name = "家族推荐号 创建家族后随机生成，用于主播审核时填写")
    private String familyRecom;

    private Long createTimes;
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return notice;
    }
    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getManifesto() {
        return manifesto;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getUserCount() {
        return userCount;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateY(Long createY) {
        this.createY = createY;
    }

    public Long getCreateY() {
        return createY;
    }
    public void setCreateM(Long createM) {
        this.createM = createM;
    }

    public Long getCreateM() {
        return createM;
    }
    public void setCreateD(Long createD) {
        this.createD = createD;
    }

    public Long getCreateD() {
        return createD;
    }
    public void setCreateW(Long createW) {
        this.createW = createW;
    }

    public Long getCreateW() {
        return createW;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
    public void setContribution(Long contribution) {
        this.contribution = contribution;
    }

    public Long getContribution() {
        return contribution;
    }
    public void setFamilyLevel(Long familyLevel) {
        this.familyLevel = familyLevel;
    }

    public Long getFamilyLevel() {
        return familyLevel;
    }
    public void setVideoTime(Long videoTime) {
        this.videoTime = videoTime;
    }

    public Long getVideoTime() {
        return videoTime;
    }
    public void setScore(Long score) {
        this.score = score;
    }

    public Long getScore() {
        return score;
    }
    public void setLiveLevel(Long liveLevel) {
        this.liveLevel = liveLevel;
    }

    public Long getLiveLevel() {
        return liveLevel;
    }
    public void setFamilyRecom(String familyRecom) {
        this.familyRecom = familyRecom;
    }

    public String getFamilyRecom() {
        return familyRecom;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logo", getLogo())
            .append("name", getName())
            .append("notice", getNotice())
            .append("manifesto", getManifesto())
            .append("nickName", getNickName())
            .append("userId", getUserId())
            .append("userCount", getUserCount())
            .append("createTime", getCreateTimes())
            .append("createDate", getCreateDate())
            .append("createY", getCreateY())
            .append("createM", getCreateM())
            .append("createD", getCreateD())
            .append("createW", getCreateW())
            .append("memo", getMemo())
            .append("status", getStatus())
            .append("contribution", getContribution())
            .append("familyLevel", getFamilyLevel())
            .append("videoTime", getVideoTime())
            .append("score", getScore())
            .append("liveLevel", getLiveLevel())
            .append("familyRecom", getFamilyRecom())
            .toString();
    }

    public Long getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes( Long createTimes ) {
        this.createTimes = createTimes;
    }
}
