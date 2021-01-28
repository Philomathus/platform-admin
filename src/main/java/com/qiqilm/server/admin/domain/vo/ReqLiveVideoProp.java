package com.qiqilm.server.admin.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author kehai
 * @Date 2020/8/11 11:37
 * @Version 1.0
 */
@Accessors( chain = true )
@Data
public class ReqLiveVideoProp {
	private Integer  toUserId;
	private String   pPserId;
	private String   createDate;
	private String   groupBy;
	private Integer  videoId;
	private String[] select;
	private Integer  propId;
	private Integer  propIdProp;
	private String   propNameLike;
	private String   userUid;
	private String startTime;
	private String endTime;
	private String name_like;
	private String pUserId;

	public ReqLiveVideoProp setSelect( String... select ) {
		this.select = select;
		return this;
	}

}
