package com.qiqilm.server.admin.domain;

import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 礼物列对象 live_prop
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class LiveProp extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 道具名
	 */
	@Excel( name = "道具名" )
	private String name;

	/**
	 * 积分
	 */
	@Excel( name = "积分" )
	private Long score;

	/**
	 * 消费钻石
	 */
	@Excel( name = "消费钻石" )
	private BigDecimal diamonds;

	/**
	 * 图标
	 */
	@Excel( name = "图标" )
	private String icon;

	/**
	 * 印票或钻石，当为红包时是钻石，非红包时是印票; is_red_envelope=1时主播可以单独获得的钻石数量;观众可抢钻石=diamonds-ticket-robot_diamods;
	 * 如果当直播结束时,钻石未包抢光时,剩余钻石也自动分配给机器人
	 */
	@Excel( name = "" )
	private BigDecimal ticket;

	/**
	 * 1:可以连续发送多个;用于小金额礼物
	 */
	@Excel( name = "1:可以连续发送多个;用于小金额礼物" )
	private String isMuch;

	/**
	 * 排序，从大到小;越大越靠前
	 */
	@Excel( name = "排序，从大到小;越大越靠前" )
	private Long sort;

	/**
	 * 1:红包
	 */
	@Excel( name = "1:红包" )
	private Integer isRedEnvelope;

	/**
	 * 0:普通礼物 1:gif礼物 2:大型动画礼物
	 */
	@Excel( name = "0:普通礼物 1:gif礼物 2:大型动画礼物" )
	private String isAnimated;

	/**
	 * 0:正常礼物；1:特殊礼物
	 */
	@Excel( name = "0:正常礼物；1:特殊礼物" )
	private String isSpecial;

	/**
	 * 0:禁用;1:启用;默认启用
	 */
	@Excel( name = "0:禁用;1:启用;默认启用" )
	private String isEffect;

	/**
	 * 大型道具类型 如："plane1","plane2","rocket1"
	 */
	@Excel( name = "大型道具类型" )
	private String animType;

	/**
	 * is_red_envelope=1时有效;分红包时,自动分配一些的机器人；剩下的给观众抢；观众可抢钻石=diamonds-ticket-robot_diamods; 如果当直播结束时,钻石未包抢光时,剩余钻石也自动分配给机器人
	 */
	@Excel( name = "is_red_envelope=1时有效;分红包时,自动分配一些的机器人；剩下的给观众抢；观众可抢钻石=diamonds-ticket-robot_diamods; 如果当直播结束时,钻石未包抢光时," +
			"剩余钻石也自动分配给机器人" )
	private Long robotDiamonds;

	/**
	 * PC端图标
	 */
	@Excel( name = "PC端图标" )
	private String pcIcon;

	/**
	 * PC端动态图标
	 */
	@Excel( name = "PC端动态图标" )
	private String pcGif;

	/**
	 * GIF礼物模式 0:按像素显示模式 1:全屏显示模式 2:至少两条边贴边模式
	 */
	@Excel( name = "GIF礼物模式 0:按像素显示模式 1:全屏显示模式 2:至少两条边贴边模式" )
	private Integer gifGiftShowStyle;

	/**
	 * svga动画路径
	 */
	@Excel( name = "svga动画路径" )
	private String animatedUrl;

	/**
	 * 0:礼物；1:打赏
	 */
	@Excel( name = "0:礼物；1:打赏;2守护" )
	private String type;

	public void setId( Long id ) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setScore( Long score ) {
		this.score = score;
	}

	public Long getScore() {
		return score;
	}

	public void setDiamonds( BigDecimal diamonds ) {
		this.diamonds = diamonds;
	}

	public BigDecimal getDiamonds() {
		return diamonds;
	}

	public void setIcon( String icon ) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public void setTicket( BigDecimal ticket ) {
		this.ticket = ticket;
	}

	public BigDecimal getTicket() {
		return ticket;
	}

	public void setIsMuch( String isMuch ) {
		this.isMuch = isMuch;
	}

	public String getIsMuch() {
		return isMuch;
	}

	public void setSort( Long sort ) {
		this.sort = sort;
	}

	public Long getSort() {
		return sort;
	}

	public void setIsRedEnvelope( Integer isRedEnvelope ) {
		this.isRedEnvelope = isRedEnvelope;
	}

	public Integer getIsRedEnvelope() {
		return isRedEnvelope;
	}

	public void setIsAnimated( String isAnimated ) {
		this.isAnimated = isAnimated;
	}

	public String getIsAnimated() {
		return isAnimated;
	}

	public void setIsEffect( String isEffect ) {
		this.isEffect = isEffect;
	}

	public String getIsEffect() {
		return isEffect;
	}

	public void setAnimType( String animType ) {
		this.animType = animType;
	}

	public String getAnimType() {
		return animType;
	}

	public void setRobotDiamonds( Long robotDiamonds ) {
		this.robotDiamonds = robotDiamonds;
	}

	public Long getRobotDiamonds() {
		return robotDiamonds;
	}

	public void setPcIcon( String pcIcon ) {
		this.pcIcon = pcIcon;
	}

	public String getPcIcon() {
		return pcIcon;
	}

	public void setPcGif( String pcGif ) {
		this.pcGif = pcGif;
	}

	public String getPcGif() {
		return pcGif;
	}

	public void setGifGiftShowStyle( Integer gifGiftShowStyle ) {
		this.gifGiftShowStyle = gifGiftShowStyle;
	}

	public Integer getGifGiftShowStyle() {
		return gifGiftShowStyle;
	}

	public void setAnimatedUrl( String animatedUrl ) {
		this.animatedUrl = animatedUrl;
	}

	public String getAnimatedUrl() {
		return animatedUrl;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "name", getName() )
				.append( "score", getScore() )
				.append( "diamonds", getDiamonds() )
				.append( "icon", getIcon() )
				.append( "ticket", getTicket() )
				.append( "isMuch", getIsMuch() )
				.append( "sort", getSort() )
				.append( "isRedEnvelope", getIsRedEnvelope() )
				.append( "isAnimated", getIsAnimated() )
				.append( "isEffect", getIsEffect() )
				.append( "animType", getAnimType() )
				.append( "robotDiamonds", getRobotDiamonds() )
				.append( "pcIcon", getPcIcon() )
				.append( "pcGif", getPcGif() )
				.append( "gifGiftShowStyle", getGifGiftShowStyle() )
				.append( "animatedUrl", getAnimatedUrl() )
				.append( "type", getType() )
				.toString();
	}
}
