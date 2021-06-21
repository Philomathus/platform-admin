package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import com.qiqilm.server.admin.utils.ValidatorUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 主播用户信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/liveUser" )
public class LiveUserController extends BaseController {
	@Autowired
	private ILiveUserService liveUserService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService userService;
	@Autowired
	private ILiveVideoService liveVideoService;
	@Autowired
	private SysConfigCacheUtil sysConfigCacheUtil;
	/**
	 * 查询主播用户信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( LiveUser liveUser ) {
		startPage();
		List<LiveUser> list = liveUserService.selectLiveUserList( liveUser );
		return getDataTable( list );
	}

	/**
	 * 导出主播用户信息列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:export')" )
	@Log( title = "导出主播用户信息列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export( LiveUser liveUser, HttpServletResponse response ) {
		List<LiveUser> list = liveUserService.selectLiveUserList( liveUser );
		ExportExcelUtil.exportExcel( list, "主播列表", "主播列表", LiveUser.class, response );
	}

	/**
	 * 获取主播用户信息详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id ) {
		return AjaxResult.success( liveUserService.selectLiveUserById( id ) );
	}

	/**
	 * 查询主播银行卡
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:query')" )
	@GetMapping( value = "/liveBank/{userId}" )
	public TableDataInfo getInfoBank( @PathVariable( "userId" ) Integer userId ) {
		startPage();
		List<LiveUser> list = liveUserService.selectLiveUserBankById( userId );
		return getDataTable( list );
	}

	/**
	 * 修改主播银行卡
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:query')" )
	@PutMapping( value = "/updateBank" )
	public AjaxResult updateLiveUserBank( @RequestBody LiveUser liveUser ) {
		return AjaxResult.success(liveUserService.updateLiveUserBank( liveUser ));
	}

	/**
	 * 删除主播银行卡
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:query')" )
	@DeleteMapping( value = "/liveBank/{bankAccount}" )
	public AjaxResult delInfoBank( @PathVariable( "bankAccount" ) String bankAccount ) {
		liveUserService.delLiveUserBankById( bankAccount );
		return AjaxResult.success();
	}

    /**
     * 新增用户信息
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
    @Log( title = "//用户信息", businessType = BusinessType.INSERT )
    @PostMapping
    public AjaxResult add(@RequestBody LiveUser liveUser) {
        return liveUserService.insertLiveUser(liveUser) ;
    }
    /**
     * 发送短信
     *
     * @return
     */
    @ApiOperation(value = "修改手机号", notes = "修改手机号")
    @RequestMapping(value = "/updateMobile", method = RequestMethod.POST)
    @Log(title = "会员发送短信", businessType = BusinessType.UPDATE)
    public AjaxResult updateMobile(@RequestBody Map map) throws Exception {
        String id = (String)map.get("userId");
        String newMobile = (String)map.get("newMobile");
        String oldMobile = (String)map.get("oldMobile");
        String googleAuthCode = (String)map.get("googleAuthCode");
        if (!ValidatorUtil.isNumber11(newMobile)) {
            return AjaxResult.error("新手机号格式错误: 11位数字");
        }
/*        if (!ValidatorUtil.isNumber11(oldMobile)) {
            return AjaxResult.error("旧手机号格式错误: 11位数字");
        }*/
        //校验谷歌验证码
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = userService.selectGoogleAuthKeyByUserName(loginUser.getUsername());
        AjaxResult x = userService.checkGoogleAuthCode(Integer.parseInt(googleAuthCode), googleAuthSecret);
        if (x != null) return x;
        return liveUserService.updateMobile(newMobile,oldMobile,id);
    }
    /**
     * 开播
     */
//    @PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
    @Log( title = "开播", businessType = BusinessType.INSERT )
    @PostMapping("openLive")
    public AjaxResult openLive(@RequestBody Map map) throws Exception {
        return liveUserService.openLive(map) ;
    }

    /**
     * 关播
     */
//    @PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
    @Log( title = "关播", businessType = BusinessType.INSERT )
    @PostMapping("closeLive")
    public AjaxResult closeLive(@RequestBody Map map) {
        return liveUserService.closeLive(map) ;
    }

	/**
	 * 认证主播用户信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
	@Log( title = "认证主播用户信息", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody LiveUser liveUser ) {
		LiveUser newLiveUser = new LiveUser();
		newLiveUser.setId( liveUser.getId() );
		newLiveUser.setIsAuthentication( liveUser.getIsAuthentication() );
		newLiveUser.setVExplain( liveUser.getVExplain() );
		newLiveUser.setInvestorSendInfo( liveUser.getInvestorSendInfo() );
		newLiveUser.setOpenPay( liveUser.getOpenPay() );
		newLiveUser.setCoin( liveUser.getCoin() );
		BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio");
		BigDecimal gifCattyRatio = sysConfigCacheUtil.getConfBd( "gif_tcatty_ratio");
		if(liveUser.getXpoint().compareTo(ticketCattyRatio) == 1){
			return AjaxResult.error( "彩票抽成比例不能大于上限"+ ticketCattyRatio);
		}
		if(liveUser.getYpoint().compareTo(gifCattyRatio) == 1){
			return AjaxResult.error( "礼物抽成比例不能大于上限" +gifCattyRatio);
		}
		newLiveUser.setXpoint( liveUser.getXpoint() );
		newLiveUser.setYpoint( liveUser.getYpoint() );
		newLiveUser.setWeiboMoney(liveUser.getWeiboMoney());
		newLiveUser.setWeixinPrice(liveUser.getWeixinPrice());
		return toAjax( liveUserService.updateLiveUser( newLiveUser ) );
	}

	/**
	 * 禁播
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
	@Log( title = "修改用户禁播状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/banDetail" )
	public AjaxResult banDetail( LiveUser liveUser ) {
		return toAjax( liveUserService.updateLiveUser( liveUser ) );
	}


	@ApiOperation( "加入家族" )
	@Log( title = "加入家族", businessType = BusinessType.UPDATE )
	@PutMapping( "/gofamiily" )
	public AjaxResult gofamiily( LiveUser user ) {
		return liveUserService.updateFamilyID( user.getFamilyId(), user.getId() );
	}

	@ApiOperation( "修改印票" )
	@Log( title = "修改印票", businessType = BusinessType.UPDATE )
	@PostMapping( "/updateTicket" )
	public AjaxResult updateTicket( LiveUser user ) {
		//判断主播当前是否在关播状态
		LiveVideo liveVideo = liveVideoService.liveInStatus(user.getId());
		if(liveVideo != null && liveVideo.getLiveIn() != 0){
			return AjaxResult.error(100,"该主播不在关播状态,修改印票失败");
		}
		return liveUserService.updateTicket( user.getTicket(), user.getId() );
	}

	@PreAuthorize( "@ss.hasPermi('live:anchorAward:list')" )
	@GetMapping( "/anchorAward" )
	public TableDataInfo anchorAward( ReqLotteryBat req ) throws ParseException {
		if ( StringUtils.isNotBlank( req.getUpdateTime() ) ) {
			req.setStartTime( req.getUpdateTime() + " 00:00:00" );
			req.setEndTime( req.getUpdateTime() + " 23:59:59" );
		}
		startPage();
		List<RspLotteryBet> list = liveUserService.selectAnchorAward( req );
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('live:anchorAward:export')" )
	@Log( title = "导出家族", businessType = BusinessType.EXPORT )
	@GetMapping( "/anchorAward/export" )
	public void anchorAwardExport( ReqLotteryBat req , HttpServletResponse response) {
		if ( StringUtils.isNotBlank( req.getUpdateTime() ) ) {
			req.setStartTime( req.getUpdateTime() + " 00:00:00" );
			req.setEndTime( req.getUpdateTime() + " 23:59:59" );
		}
		List<RspLotteryBet>      list = liveUserService.selectAnchorAward( req );
		ExportExcelUtil.exportExcel( list, "公司入款", "公司入款信息表", RspLotteryBet.class, response );
	}

	/**
	 * 踢出家族
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
	@PutMapping( value = "/kickOutLive/{id}" )
	public AjaxResult kickOutLive( @PathVariable( "id" ) Long id ) {
		return liveUserService.kickOutLiveById( id );
	}

}
