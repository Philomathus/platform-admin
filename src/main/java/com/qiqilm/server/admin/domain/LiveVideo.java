package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 直播对象 live_video
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
@Accessors( chain = true )
public class LiveVideo extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** id,也是房间room_id */
	private Long id;

	/** 直播标题 */
	@Excel( name = "直播标题" )
	private String title;

	/** 项目id */
	@Excel( name = "项目id" )
	private Integer userId;

	/** 是否直播中 1-直播中 0-已停止;2:正在创建直播;3:历史数据 */
	@Excel( name = "是否直播中 1-直播中 0-已停止;2:正在创建直播;3:历史数据" )
	private Integer liveIn;

	/** 当前实时观看人数（实际,不含虚拟人数,不包含机器人) */
	@Excel( name = "当前实时观看人数", suffix = "当前实时观看人数（实际,不含虚拟人数,不包含机器人)" )
	private Long watchNumber;

	/** 当前虚拟观看人数 */
	@Excel( name = "当前虚拟观看人数" )
	private Long virtualWatchNumber;

	/** 获得票数 */
	@Excel( name = "获得票数" )
	private BigDecimal voteNumber;

	/** 主题id（2最新3颜值4收费5游戏） */
	@Excel( name = "主题id", suffix = "2=最新3颜值4收费5游戏" )
	private Integer cateId;

	/** 开始时间 */
	@JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
	@Excel( name = "开始时间", width = 30, exportFormat = "yyyy-MM-dd HH:mm:ss" )
	private Date beginTime;

	/** 结束时间 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "结束时间", width = 30, exportFormat = "yyyy-MM-dd" )
	private Date endTime;

	/** 结束日期 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "结束日期", width = 30, exportFormat = "yyyy-MM-dd" )
	private Date endDate;

	/** 群组ID,通过create_group后返回的值;直播结束后解散群 */
	@Excel( name = "群组ID,通过create_group后返回的值;直播结束后解散群" )
	private String groupId;

	/** 最大观看人数(每进来一人次加1）实际,不含虚拟人数,不包含机器人 */
	@Excel( name = "最大观看人数(每进来一人次加1）实际,不含虚拟人数,不包含机器人" )
	private Long maxWatchNumber;

	/** 最后心跳监听时间；如果超过监听时间，则说明主播已经掉线了 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "最后心跳监听时间；如果超过监听时间，则说明主播已经掉线了", width = 30, exportFormat = "yyyy-MM-dd" )
	private Date monitorTime;

	/** 聊天群中机器人数量 */
	@Excel( name = "聊天群中机器人数量" )
	private Long robotNum;

	/** 是否被服务器异常终止结束 */
	@Excel( name = "是否被服务器异常终止结束" )
	private Boolean isAborted;

	/** 主播在线状态;1:在线(默认); 0:离开 */
	@Excel( name = "主播在线状态;1:在线(默认); 0:离开" )
	private Boolean onlineStatus;

	/** 热门排序 */
	@Excel( name = "热门排序" )
	private Long sort;

	private Integer sortInit;

	/** 线路id */
	@Excel( name = "线路id" )
	private Long paiId;

	/** 默认最大机器人头像数 */
	@Excel( name = "默认最大机器人头像数" )
	private Long maxRobotNum;

	/** 本场直播净添加的粉丝数即：被关注数，关注加1，取消减1 */
	@Excel( name = "本场直播净添加的粉丝数即：被关注数，关注加1，取消减1" )
	private Long fansCount;

	/** 推流地址 */
	@Excel( name = "推流地址" )
	private String pushRtmp;

	/** 播放地址；当video_type=0时，记录：傍路直播地址 */
	@Excel( name = "播放地址；当video_type=0时，记录：傍路直播地址" )
	private String playFlv;

	/** 播放地址；当video_type=0时，记录：傍路直播地址 */
	@Excel( name = "播放地址；当video_type=0时，记录：傍路直播地址" )
	private String playRtmp;

	/** 播放地址；当video_type=0时，记录：傍路直播地址 */
	@Excel( name = "播放地址；当video_type=0时，记录：傍路直播地址" )
	private String playMp4;

	/** 播放地址；当video_type=0时，记录：傍路直播地址 */
	@Excel( name = "播放地址；当video_type=0时，记录：傍路直播地址" )
	private String playHls;

	/** 直播时，可自定义封面图; 如果不存在,则取会员头像 */
	@Excel( name = "直播时，可自定义封面图; 如果不存在,则取会员头像" )
	private String headImage;

	/** 播放地址 */
	@Excel( name = "播放地址" )
	private String playUrl;

	/** 推荐视频 0不推荐、1推荐 */
	@Excel( name = "推荐视频 0不推荐、1推荐" )
	private Long isRecommend;

	/** 视频封面 */
	@Excel( name = "视频封面" )
	private String liveImage;

	/** 最大虚拟人数 */
	@Excel( name = "最大虚拟人数" )
	private Long virtualNumber;

	/** 开始收费时间 */
	@Excel( name = "开始收费时间" )
	private int livePayTime;

	/** 是否收费模式  1是 0否 */
	@Excel( name = "是否收费模式  1是 0否" )
	private Boolean isLivePay;

	/** 付费直播 收取多少费用； 每分钟收取多少钻石，主播端设置 */
	@Excel( name = "付费直播 收取多少费用； 每分钟收取多少钻石，主播端设置" )
	private Integer liveFee;

	/** 是否已经提档 1是、0否 */
	@Excel( name = "是否已经提档 1是、0否" )
	private Boolean liveIsMention;

	/** 收费类型 0按时收费，1按场次收费 (is_live_pay 是1时候有效) */
	@Excel( name = "收费类型 0按时收费，1按场次收费 (is_live_pay 是1时候有效)" )
	private Integer livePayType;

	/** 付费人数 */
	@Excel( name = "付费人数" )
	private Long livePayCount;

	@ApiModelProperty(value = "0:APP端创建的直播;1:PC端创建的直播")
	private Boolean createType;

	/** 直播的时长 */
	@Excel( name = "直播的时长" )
	private Long lenTime;

	/** 是否置底 0 不置底 1 置底 */
	@Excel( name = "是否置底" )
	private Boolean stick;

	/** 分类id */
	@Excel( name = "分类id" )
	private Integer classifiedId;

	/** 绑定彩票ID */
	@Excel( name = "绑定彩票ID" )
	private Integer lotteryId;

	/** MD5加密推流地址(弃用) */
	@Excel( name = "MD5加密推流地址(弃用)" )
	private String newPlayFlv;

	/** MD5加密推流地址 */
	@Excel( name = "MD5加密推流地址" )
	private String nPlayFlv;

	/** 主播昵称 */
	@Excel( name = "主播昵称" )
	private String hostName;

	/** 彩票名称 */
	@Excel( name = "彩票名称" )
	private String lotteryName;

	@ApiModelProperty(value = "视频是否连麦 0 未连麦，> 0 连麦主播")
	private Integer concatVideo;

	private String  liveStatus = "";
	private String  lineName;
	private Integer lineStatus;
	private String  openPay;
	private String  info;
	private String  effect;

	@JsonIgnore
	private String[] types;
}
