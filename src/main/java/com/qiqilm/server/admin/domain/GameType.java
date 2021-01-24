package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 游戏类型对象 game_type
 *
 * @author 77tv
 * @date 2021-01-24
 */
public class GameType extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统编号
	 */
	private String id;

	/**
	 * 名称
	 */
	@Excel( name = "名称" )
	private String name;

	/**
	 * 状态(1启用0停用)
	 */
	@Excel( name = "状态(1启用0停用)" )
	private Integer status;

	/**
	 * 排序号
	 */
	@Excel( name = "排序号" )
	private Integer indexs;

	/**
	 * 图标
	 */
	@Excel( name = "图标" )
	private String icon;

	/**
	 * 0=热门游戏1=游戏2=直播
	 */
	private Integer gameType;

	/**
	 * 图标类型（ 0=热门1=捕鱼2=电子3=体育4=真人5=棋牌6=彩票）
	 */
	@Excel( name = "图标类型", readConverterExp = "0==热门1=捕鱼2=电子3=体育4=真人5=棋牌6=彩票" )
	private Integer iconType;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus( Integer status ) {
		this.status = status;
	}

	public Integer getIndexs() {
		return indexs;
	}

	public void setIndexs( Integer indexs ) {
		this.indexs = indexs;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon( String icon ) {
		this.icon = icon;
	}

	public Integer getGameType() {
		return gameType;
	}

	public void setGameType( Integer gameType ) {
		this.gameType = gameType;
	}

	public Integer getIconType() {
		return iconType;
	}

	public void setIconType( Integer iconType ) {
		this.iconType = iconType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "name", getName() )
				.append( "status", getStatus() )
				.append( "indexs", getIndexs() )
				.append( "icon", getIcon() )
				.append( "gameType", getGameType() )
				.append( "iconType", getIconType() )
				.toString();
	}
}