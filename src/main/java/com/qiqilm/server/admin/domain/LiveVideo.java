package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qiqilm.server.admin.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 直播对象 live_video
 *
 * @author 77tv
 * @date 2021-01-25
 */
@Data
public class LiveVideo extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/** id,也是房间room_id */
	private Long id;

	/** 直播标题 */
	@Excel( name = "直播标题" )
	private String title;

	/** 项目id */
	@Excel( name = "项目id" )
	private Long userId;

	/** 是否直播中 1-直播中 0-已停止;2:正在创建直播;3:历史数据 */
	@Excel( name = "是否直播中 1-直播中 0-已停止;2:正在创建直播;3:历史数据" )
	private Integer liveIn;

	/** 当前实时观看人数（实际,不含虚拟人数,不包含机器人) */
	@Excel( name = "当前实时观看人数", readConverterExp = "当前实时观看人数（实际,不含虚拟人数,不包含机器人)" )
	private Long watchNumber;

	/** 当前虚拟观看人数 */
	@Excel( name = "当前虚拟观看人数" )
	private Long virtualWatchNumber;

	/** 获得票数 */
	@Excel( name = "获得票数" )
	private BigDecimal voteNumber;

	/** 主题id（2最新3颜值4收费5游戏） */
	@Excel( name = "主题id", readConverterExp = "2=最新3颜值4收费5游戏" )
	private Long cateId;

	/** 省份 */
	@Excel( name = "省份" )
	private String province;

	/** 城市 */
	@Excel( name = "城市" )
	private String city;

	/** 开始时间 */
	@JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
	@Excel( name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss" )
	private Date beginTime;

	/** 结束时间 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd" )
	private Date endTime;

	/** 结束日期 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd" )
	private Date endDate;

	/** 群组ID,通过create_group后返回的值;直播结束后解散群 */
	@Excel( name = "群组ID,通过create_group后返回的值;直播结束后解散群" )
	private String groupId;

	/** 1：未解散;0:已解散;其它为ErrorCode错码 */
	@Excel( name = "1：未解散;0:已解散;其它为ErrorCode错码" )
	private Integer destroyGroupStatus;

	/** 通过create_group后返回的LongPollingKey值 */
	@Excel( name = "通过create_group后返回的LongPollingKey值" )
	private String longPollingKey;

	/** 最大观看人数(每进来一人次加1）实际,不含虚拟人数,不包含机器人 */
	@Excel( name = "最大观看人数(每进来一人次加1）实际,不含虚拟人数,不包含机器人" )
	private Long maxWatchNumber;

	/** 房间类型 : 1私有群（Private）,0公开群（Public）,2聊天室（ChatRoom）,3互动直播聊天室（AVChatRoom） */
	@Excel( name = "房间类型 : 1私有群", readConverterExp = "P=rivate" )
	private Long roomType;

	/** 是否可回放 0-否 ；1-是 */
	@Excel( name = "是否可回放 0-否 ；1-是" )
	private Integer isPlayback;

	/** 视频地址 */
	@Excel( name = "视频地址" )
	private String videoVid;

	/** 最后心跳监听时间；如果超过监听时间，则说明主播已经掉线了 */
	@JsonFormat( pattern = "yyyy-MM-dd" )
	@Excel( name = "最后心跳监听时间；如果超过监听时间，则说明主播已经掉线了", width = 30, dateFormat = "yyyy-MM-dd" )
	private Date monitorTime;

	/** 1:删除;0:未删除;私有聊天或小于5分钟的视频，不保存 */
	@Excel( name = "1:删除;0:未删除;私有聊天或小于5分钟的视频，不保存" )
	private Boolean isDelete;

	/** 聊天群中机器人数量 */
	@Excel( name = "聊天群中机器人数量" )
	private Long robotNum;

	/** 添加机器人时间（每隔20秒左右加几个人） */
	@Excel( name = "添加机器人时间", readConverterExp = "每=隔20秒左右加几个人" )
	private Long robotTime;

	/** 旁路直播,频道ID */
	@Excel( name = "旁路直播,频道ID" )
	private String channelid;

	/** 1:被服务器异常终止结束(主要是心跳超时) */
	@Excel( name = "1:被服务器异常终止结束(主要是心跳超时)" )
	private Boolean isAborted;

	/** 1:表示已经清空了,录制视频;0:未做清空操作 */
	@Excel( name = "1:表示已经清空了,录制视频;0:未做清空操作" )
	private Boolean isDelVod;

	/** 主播在线状态;1:在线(默认); 0:离开 */
	@Excel( name = "主播在线状态;1:在线(默认); 0:离开" )
	private Integer onlineStatus;

	/** 举报次数 */
	@Excel( name = "举报次数" )
	private Long tipoffCount;

	/** 私密直播key */
	@Excel( name = "私密直播key" )
	private String privateKey;

	/** 分享类型WEIXIN,WEIXIN_CIRCLE,QQ,QZONE,SINA */
	@Excel( name = "分享类型WEIXIN,WEIXIN_CIRCLE,QQ,QZONE,SINA" )
	private String shareType;

	/** 热门排序 */
	@Excel( name = "热门排序" )
	private Long sort;

	/** 竞拍id */
	@Excel( name = "竞拍id" )
	private Long paiId;

	private String  name;
	/** 性别 0:未知, 1-男，2-女 */
	@Excel( name = "性别 0:未知, 1-男，2-女" )
	private Integer sex;

	/** 0:腾讯云互动直播;1:腾讯云直播 */
	@Excel( name = "0:腾讯云互动直播;1:腾讯云直播" )
	private Integer videoType;

	/** sort_init + share_count * 分享权重 + like_count * 点赞权重 + fans_count * 关注权重 + sort * 排序权重 + ticket(本场收到的印票) * 印票权重 */
	@Excel( name = "sort_init + share_count * 分享权重 + like_count * 点赞权重 + fans_count * 关注权重 + sort * 排序权重 + ticket(本场收到的印票) * " +
			"印票权重" )
	private Long sortNum;

	/** 0:APP端创建的直播;1:PC端创建的直播 */
	@Excel( name = "0:APP端创建的直播;1:PC端创建的直播" )
	private Integer createType;

	/** 默认最大机器人头像数 */
	@Excel( name = "默认最大机器人头像数" )
	private Long maxRobotNum;

	/** 分享数,每个用户只记录一次 */
	@Excel( name = "分享数,每个用户只记录一次" )
	private Long shareCount;

	/** 点赞数,每个用户只记录一次 */
	@Excel( name = "点赞数,每个用户只记录一次" )
	private Long likeCount;

	/** 本场直播净添加的粉丝数即：被关注数，关注加1，取消减1 */
	@Excel( name = "本场直播净添加的粉丝数即：被关注数，关注加1，取消减1" )
	private Long fansCount;

	/**
	 * sort_init(初始排序权重) = (用户可提现印票：fanwe_user.ticket - fanwe_user.refund_ticket) * 保留印票权重+ 直播/回看[回看是：0; 直播：9000000000 直播,需要排在最上面
	 * ]+ fanwe_user.user_level * 等级权重+ fanwe_user.fans_count * 当前有的关注数权重
	 */
	@Excel( name = "sort_init(初始排序权重) = (用户可提现印票：fanwe_user.ticket - fanwe_user.refund_ticket) * 保留印票权重+ 直播/回看[回看是：0; " +
			"直播：9000000000 直播,需要排在最上面 ]+ fanwe_user.user_level * 等级权重+ fanwe_user.fans_count * 当前有的关注数权重" )
	private Long sortInit;

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

	/** x座标(用来计算：附近) */
	@Excel( name = "x座标(用来计算：附近)" )
	private BigDecimal xpoint;

	/** y座标(用来计算：附近) */
	@Excel( name = "y座标(用来计算：附近)" )
	private BigDecimal ypoint;

	/** 直播时，可自定义封面图; 如果不存在,则取会员头像 */
	@Excel( name = "直播时，可自定义封面图; 如果不存在,则取会员头像" )
	private String headImage;

	/** 模糊图片 */
	@Excel( name = "模糊图片" )
	private String thumbHeadImage;

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
	private Integer liveIsMention;

	/** 收费类型 0按时收费，1按场次收费 (is_live_pay 是1时候有效) */
	@Excel( name = "收费类型 0按时收费，1按场次收费 (is_live_pay 是1时候有效)" )
	private Integer livePayType;

	/** 付费人数 */
	@Excel( name = "付费人数" )
	private Long livePayCount;

	/** 直播礼物表 */
	@Excel( name = "直播礼物表" )
	private String propTable;

	/** 直播间名称 */
	@Excel( name = "直播间名称" )
	private String roomTitle;

	/** 付费直播的ID , 用于标示直播间付费 模式 */
	@Excel( name = "付费直播的ID , 用于标示直播间付费 模式 " )
	private Long payRoomId;

	/** 直播的时长 */
	@Excel( name = "直播的时长" )
	private Long lenTime;

	/** 视频是否合并 0 未合并，1 已合并 */
	@Excel( name = "视频是否合并 0 未合并，1 已合并" )
	private Integer isConcatvideo;

	/** 是否置顶 0 不置顶 1 置顶 */
	@Excel( name = "是否置顶 0 不置顶 1 置顶" )
	private Integer stick;

	/** 分类id */
	@Excel( name = "分类id" )
	private Long classifiedId;

	/** 游戏记录id */
	@Excel( name = "游戏记录id" )
	private Long gameLogId;

	/** 上庄状态 */
	@Excel( name = "上庄状态" )
	private Integer bankerStatus;

	/** 上庄id */
	@Excel( name = "上庄id" )
	private Long bankerId;

	/** 是否自动开启游戏 */
	@Excel( name = "是否自动开启游戏" )
	private Integer autoStart;

	/** 自动开始游戏id */
	@Excel( name = "自动开始游戏id" )
	private Long autoGameId;

	/** 绑定彩票ID */
	@Excel( name = "绑定彩票ID" )
	private Long lotteryId;

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

	private String  liveStatus = "";
	private String  lineName;
	private Integer lineStatus;

	private String cpcost;
	private String cpprize;

	@JsonIgnore
	private String[] types;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "title", getTitle() )
				.append( "userId", getUserId() )
				.append( "liveIn", getLiveIn() )
				.append( "watchNumber", getWatchNumber() )
				.append( "virtualWatchNumber", getVirtualWatchNumber() )
				.append( "voteNumber", getVoteNumber() )
				.append( "cateId", getCateId() )
				.append( "province", getProvince() )
				.append( "city", getCity() )
				.append( "createTime", getCreateTime() )
				.append( "beginTime", getBeginTime() )
				.append( "endTime", getEndTime() )
				.append( "endDate", getEndDate() )
				.append( "groupId", getGroupId() )
				.append( "destroyGroupStatus", getDestroyGroupStatus() )
				.append( "longPollingKey", getLongPollingKey() )
				.append( "maxWatchNumber", getMaxWatchNumber() )
				.append( "roomType", getRoomType() )
				.append( "isPlayback", getIsPlayback() )
				.append( "videoVid", getVideoVid() )
				.append( "monitorTime", getMonitorTime() )
				.append( "isDelete", getIsDelete() )
				.append( "robotNum", getRobotNum() )
				.append( "robotTime", getRobotTime() )
				.append( "channelid", getChannelid() )
				.append( "isAborted", getIsAborted() )
				.append( "isDelVod", getIsDelVod() )
				.append( "onlineStatus", getOnlineStatus() )
				.append( "tipoffCount", getTipoffCount() )
				.append( "privateKey", getPrivateKey() )
				.append( "shareType", getShareType() )
				.append( "sort", getSort() )
				.append( "paiId", getPaiId() )
				.append( "sex", getSex() )
				.append( "videoType", getVideoType() )
				.append( "sortNum", getSortNum() )
				.append( "createType", getCreateType() )
				.append( "maxRobotNum", getMaxRobotNum() )
				.append( "shareCount", getShareCount() )
				.append( "likeCount", getLikeCount() )
				.append( "fansCount", getFansCount() )
				.append( "sortInit", getSortInit() )
				.append( "pushRtmp", getPushRtmp() )
				.append( "playFlv", getPlayFlv() )
				.append( "playRtmp", getPlayRtmp() )
				.append( "playMp4", getPlayMp4() )
				.append( "playHls", getPlayHls() )
				.append( "xpoint", getXpoint() )
				.append( "ypoint", getYpoint() )
				.append( "headImage", getHeadImage() )
				.append( "thumbHeadImage", getThumbHeadImage() )
				.append( "playUrl", getPlayUrl() )
				.append( "isRecommend", getIsRecommend() )
				.append( "liveImage", getLiveImage() )
				.append( "virtualNumber", getVirtualNumber() )
				.append( "livePayTime", getLivePayTime() )
				.append( "isLivePay", getIsLivePay() )
				.append( "liveFee", getLiveFee() )
				.append( "liveIsMention", getLiveIsMention() )
				.append( "livePayType", getLivePayType() )
				.append( "livePayCount", getLivePayCount() )
				.append( "propTable", getPropTable() )
				.append( "roomTitle", getRoomTitle() )
				.append( "payRoomId", getPayRoomId() )
				.append( "lenTime", getLenTime() )
				.append( "isConcatvideo", getIsConcatvideo() )
				.append( "stick", getStick() )
				.append( "classifiedId", getClassifiedId() )
				.append( "gameLogId", getGameLogId() )
				.append( "bankerStatus", getBankerStatus() )
				.append( "bankerId", getBankerId() )
				.append( "autoStart", getAutoStart() )
				.append( "autoGameId", getAutoGameId() )
				.append( "lotteryId", getLotteryId() )
				.append( "newPlayFlv", getNewPlayFlv() )
				.append( "nPlayFlv", getNPlayFlv() )
				.append( "hostName", getHostName() )
				.append( "lotteryName", getLotteryName() )
				.toString();
	}
}
