package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.utils.ExcelUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

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
	public AjaxResult export( LiveUser liveUser ) {
		List<LiveUser>      list = liveUserService.selectLiveUserList( liveUser );
		ExcelUtil<LiveUser> util = new ExcelUtil<LiveUser>( LiveUser.class );
		return util.exportExcel( list, "liveUser" );
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
	 * 新增主播用户信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
	@Log( title = "新增主播用户信息", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody LiveUser liveUser ) {
		return toAjax( liveUserService.insertLiveUser( liveUser ) );
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

	@PreAuthorize( "@ss.hasPermi('live:anchorAward:list')" )
	@GetMapping( "/anchorAward" )
	public TableDataInfo anchorAward( ReqLotteryBat req ) throws ParseException {
		if ( StringUtils.isNotBlank( req.getUpdateTime() ) ) {
			req.setStartTime( req.getUpdateTime() + " 00:00:00" );
			req.setEndTime( req.getUpdateTime() + " 23:59:59");
		}
		startPage();
		List<RspLotteryBet> list = liveUserService.selectAnchorAward( req );
		return getDataTable( list );
	}

	@PreAuthorize( "@ss.hasPermi('live:anchorAward:export')" )
	@GetMapping( "/anchorAward/export" )
	public AjaxResult anchorAwardExport( ReqLotteryBat req ) {
		if ( StringUtils.isNotBlank( req.getUpdateTime() ) ) {
			req.setStartTime( req.getUpdateTime() + " 00:00:00" );
			req.setEndTime( req.getUpdateTime() + " 23:59:59");
		}
		List<RspLotteryBet>      list = liveUserService.selectAnchorAward( req );
		ExcelUtil<RspLotteryBet> util = new ExcelUtil<>( RspLotteryBet.class );
		return util.exportExcel( list, "anchorAward" );
	}
}
