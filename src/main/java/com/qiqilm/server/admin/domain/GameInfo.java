package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 game_info
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Data
public class GameInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 系统编号 */
    private String id;

    /** 游戏名称 */
    @Excel(name = "游戏名称")
    private String name;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long indexs;

    /** 是否维护(1是0否) */
    @Excel(name = "是否维护(1是0否)")
    private String isWh;

    /** 是否推荐(1是0否) */
    @Excel(name = "是否推荐(1是0否)")
    private Long isRecommend;

    /** 状态(1启用0停用) */
    @Excel(name = "状态(1启用0停用)")
    private String status;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 是否热门(1是0否) */
    @Excel(name = "是否热门(1是0否)")
    private Long isHot;

    /** 新版图标 */
    @Excel(name = "新版图标")
    private String editionIcon;

    /** 游戏码(0大厅620德州扑克720二八杠830抢庄牛牛220炸金花860三公900压庄龙虎600 二十一点 870通比牛牛230极速炸金花730抢庄牌九630十三水610斗地主910百家乐920森林舞会930白人牛牛1950万人炸金花650血流成河890看牌抢庄牛牛740二人麻将1350幸运转盘1940金鲨银鲨1960奔驰宝马 */
    @Excel(name = "游戏码(0大厅620德州扑克720二八杠830抢庄牛牛220炸金花860三公900压庄龙虎600 二十一点 870通比牛牛230极速炸金花730抢庄牌九630十三水610斗地主910百家乐920森林舞会930白人牛牛1950万人炸金花650血流成河890看牌抢庄牛牛740二人麻将1350幸运转盘1940金鲨银鲨1960奔驰宝马")
    private String kindId;

    /** 游戏平台(1开元) */
    @Excel(name = "游戏平台(1开元)")
    private Integer platformId;

    /** 0 =横屏 1=竖屏 */
    @Excel(name = "0 =横屏 1=竖屏")
    private Long screen;

    /** 高宽比 */
    @Excel(name = "高宽比")
    private BigDecimal highWide;

    /** 0 =不填充 1=填充 */
    @Excel(name = "0 =不填充 1=填充")
    private String isFull;

    /** 图标类型（ 0=热门1=捕鱼2=电子3=体育4=真人5=棋牌6=彩票7=电竞） */
    @Excel(name = "图标类型", readConverterExp = "0==热门1=捕鱼2=电子3=体育4=真人5=棋牌6=彩票7=电竞")
    private Long iconType;



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("remark", getRemark())
            .append("indexs", getIndexs())
            .append("isWh", getIsWh())
            .append("isRecommend", getIsRecommend())
            .append("status", getStatus())
            .append("icon", getIcon())
            .append("isHot", getIsHot())
            .append("editionIcon", getEditionIcon())
            .append("createTime", getCreateTime())
            .append("kindId", getKindId())
            .append("platformId", getPlatformId())
            .append("screen", getScreen())
            .append("highWide", getHighWide())
            .append("isFull", getIsFull())
            .append("iconType", getIconType())
            .toString();
    }
}