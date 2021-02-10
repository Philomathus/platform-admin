package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 report_plam_games
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ReportPlamGames extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private String gameUuid;

    /** 平台编号 */
    @Excel(name = "平台编号")
    private String gameagent;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String gameplame;

    /** 投注人数 */
    @Excel(name = "投注人数")
    private BigDecimal gamepepole;

    /** 投注比数 */
    @Excel(name = "投注比数")
    private BigDecimal gametouzhu;

    /** 总投注金额 */
    @Excel(name = "总投注金额")
    private BigDecimal gamecell;

    /** 有效投注金额 */
    @Excel(name = "有效投注金额")
    private BigDecimal gamebet;

    /** 总反水 */
    @Excel(name = "总反水")
    private BigDecimal gamerevenve;

    /** 会员盈利 */
    @Excel(name = "会员盈利")
    private BigDecimal gameprofit;

    /** 平台盈利 */
    @Excel(name = "平台盈利")
    private BigDecimal plampfit;

    /** 比例 */
    @Excel(name = "比例")
    private String bili;

    /** 日期 */

    @Excel(name = "日期")
    private String begindate;
    private Integer countBetPeople;
    private BigDecimal countBetMoney;

    public Integer getCountBetPeople() {
        return countBetPeople;
    }

    public void setCountBetPeople(Integer countBetPeople) {
        this.countBetPeople = countBetPeople;
    }

    public BigDecimal getCountBetMoney() {
        return countBetMoney;
    }

    public void setCountBetMoney(BigDecimal countBetMoney) {
        this.countBetMoney = countBetMoney;
    }

    public void setGameUuid(String gameUuid) {
        this.gameUuid = gameUuid;
    }

    public String getGameUuid() {
        return gameUuid;
    }
    public void setGameagent(String gameagent) {
        this.gameagent = gameagent;
    }

    public String getGameagent() {
        return gameagent;
    }
    public void setGameplame(String gameplame) {
        this.gameplame = gameplame;
    }

    public String getGameplame() {
        return gameplame;
    }
    public void setGamepepole(BigDecimal gamepepole) {
        this.gamepepole = gamepepole;
    }

    public BigDecimal getGamepepole() {
        return gamepepole;
    }
    public void setGametouzhu(BigDecimal gametouzhu) {
        this.gametouzhu = gametouzhu;
    }

    public BigDecimal getGametouzhu() {
        return gametouzhu;
    }
    public void setGamecell(BigDecimal gamecell) {
        this.gamecell = gamecell;
    }

    public BigDecimal getGamecell() {
        return gamecell;
    }
    public void setGamebet(BigDecimal gamebet) {
        this.gamebet = gamebet;
    }

    public BigDecimal getGamebet() {
        return gamebet;
    }
    public void setGamerevenve(BigDecimal gamerevenve) {
        this.gamerevenve = gamerevenve;
    }

    public BigDecimal getGamerevenve() {
        return gamerevenve;
    }
    public void setGameprofit(BigDecimal gameprofit) {
        this.gameprofit = gameprofit;
    }

    public BigDecimal getGameprofit() {
        return gameprofit;
    }
    public void setPlampfit(BigDecimal plampfit) {
        this.plampfit = plampfit;
    }

    public BigDecimal getPlampfit() {
        return plampfit;
    }
    public void setBili(String bili) {
        this.bili = bili;
    }

    public String getBili() {
        return bili;
    }
    public void setBegindate(String begindate) {
        this.begindate = begindate;
    }

    public String getBegindate() {
        return begindate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("gameUuid", getGameUuid())
            .append("gameagent", getGameagent())
            .append("gameplame", getGameplame())
            .append("gamepepole", getGamepepole())
            .append("gametouzhu", getGametouzhu())
            .append("gamecell", getGamecell())
            .append("gamebet", getGamebet())
            .append("gamerevenve", getGamerevenve())
            .append("gameprofit", getGameprofit())
            .append("plampfit", getPlampfit())
            .append("bili", getBili())
            .append("begindate", getBegindate())
            .toString();
    }
}