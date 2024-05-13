package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.BankList;
import com.qiqilm.server.admin.domain.LiveUser;
import com.qiqilm.server.admin.domain.LiveVideo;
import com.qiqilm.server.admin.domain.req.ReqLotteryBat;
import com.qiqilm.server.admin.domain.rsp.RspLotteryBet;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IBankListService;
import com.qiqilm.server.admin.service.ILiveUserService;
import com.qiqilm.server.admin.service.ILiveVideoService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 主播用户信息Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Log4j2
@RestController
@RequestMapping( "/admin/liveUser" )
public class LiveUserController extends BaseController {

    @Resource
    private ILiveUserService   liveUserService;
    @Resource
    private TokenService       tokenService;
    @Resource
    private ISysUserService    userService;
    @Resource
    private ILiveVideoService  liveVideoService;
    @Resource
    private SysConfigCacheUtil sysConfigCacheUtil;
    @Resource
    private IBankListService   bankListService;
    @Resource
    private ISysUserService    sysUserService;

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
     * 银行卡列表
     */
    @GetMapping( "/banks" )
    public AjaxResult banks() {
        List<BankList> data = bankListService.selectBankListLists();
        if ( data.size() == 0 || data == null ) {
            data = new ArrayList<>();
        }
        return AjaxResult.success( data );
    }

    /**
     * 修改主播银行卡
     */
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:query')" )
    @PutMapping( value = "/updateBank" )
    public AjaxResult updateLiveUserBank( @RequestBody LiveUser liveUser ) {
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        log.info( "修改主播银行卡操作人:" + userName );
        return AjaxResult.success( liveUserService.updateLiveUserBank( liveUser ) );
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
    @Log( title = "新增虚拟主播", businessType = BusinessType.INSERT )
    @PostMapping
    public AjaxResult add( @RequestBody LiveUser liveUser ) {
        return liveUserService.insertLiveUser( liveUser );
    }

    /**
     * 发送短信
     */
    @ApiOperation( value = "修改手机号", notes = "修改手机号" )
    @RequestMapping( value = "/updateMobile", method = RequestMethod.POST )
    @Log( title = "会员发送短信", businessType = BusinessType.UPDATE )
    public AjaxResult updateMobile( @RequestBody Map map ) throws Exception {
        String id             = ( String ) map.get( "userId" );
        String newMobile      = ( String ) map.get( "newMobile" );
        String oldMobile      = ( String ) map.get( "oldMobile" );
        String googleAuthCode = ( String ) map.get( "googleAuthCode" );
        if ( !ValidatorUtil.isNumber11( newMobile ) ) {
            return AjaxResult.error( "新手机号格式错误: 11位数字" );
        }
/*        if (!ValidatorUtil.isNumber11(oldMobile)) {
            return AjaxResult.error("旧手机号格式错误: 11位数字");
        }*/
        //校验谷歌验证码
        LoginUser  loginUser        = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String     googleAuthSecret = userService.selectGoogleAuthKeyByUserName( loginUser.getUsername() );
        AjaxResult x                = userService.checkGoogleAuthCode( Integer.parseInt( googleAuthCode ), googleAuthSecret );
        if ( x != null ) {
            return x;
        }
        return liveUserService.updateMobile( newMobile, oldMobile, id );
    }


    /**
     * Get full phone number
     * 获取完整手机号
     */
    //    @PreAuthorize("@ss.hasPermi('admin:liveUser:fullMobile')")
    @GetMapping( value = "/fullMobile/{id}" )
    public AjaxResult fullMobile( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( liveUserService.selectMobileById( id ) );
    }

    /**
     * 重置密码
     */
    @ApiOperation( value = "重置密码", notes = "重置密码" )
    @PostMapping( "/resetPass" )
    @Log( title = "重置密码", businessType = BusinessType.UPDATE )
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:resetPass')" )
    public Object resetPass( LiveUser liveUser ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( liveUser.getGoogleAuthCode() == null ) {
            rspBase.setMsg( "请输入google验证码" );
            rspBase.setCode( 1 );
            return rspBase;
        }

        LoginUser loginUser        = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName( loginUser.getUsername() );

        if ( !org.springframework.util.StringUtils.hasText( googleAuthSecret ) ) {
            rspBase.setMsg( "未绑定google验证秘钥，无法审核" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        if ( googleAuthSecret.length() == 32 ) {
            rspBase.setMsg( "google验证秘钥未加密，请重新登录" );
            rspBase.setCode( 1 );
            return rspBase;
        }

        if ( StringUtils.isEmpty( liveUser.getUserPass() ) ) {
            rspBase.setMsg( "请输入密码验证码" );
            rspBase.setCode( 1 );
            return rspBase;
        }

        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                "secretkey" + "/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, liveUser.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        liveUserService.updateLiveUser( liveUser );
        return new RspBase();
    }


    /**
     * 开播
     */
    //    @PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
    @Log( title = "开播", businessType = BusinessType.INSERT )
    @PostMapping( "openLive" )
    public AjaxResult openLive( @RequestBody Map map ) throws Exception {
        return liveUserService.openLive( map );
    }

    /**
     * 关播
     */
    //    @PreAuthorize( "@ss.hasPermi('admin:liveUser:add')" )
    @Log( title = "关播", businessType = BusinessType.INSERT )
    @PostMapping( "closeLive" )
    public AjaxResult closeLive( @RequestBody Map map ) {
        return liveUserService.closeLive( map );
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
        newLiveUser.setQqId( liveUser.getQqId() );
        newLiveUser.setQqToken( liveUser.getQqToken() );
        newLiveUser.setIsAuthentication( liveUser.getIsAuthentication() );
        newLiveUser.setVExplain( liveUser.getVExplain() );
        newLiveUser.setInvestorSendInfo( liveUser.getInvestorSendInfo() );
        newLiveUser.setOpenPay( null );
        newLiveUser.setCoin( liveUser.getCoin() );
        if ( liveUser.getXpoint() != null ) {
            BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd( "ticket_catty_ratio" );
            if ( liveUser.getXpoint().compareTo( ticketCattyRatio ) == 1
                    || liveUser.getXpoint().compareTo( BigDecimal.ZERO ) == -1 ) {
                return AjaxResult.error( "彩票抽成比例不能大于上限" + ticketCattyRatio );
            }
        }
        if ( liveUser.getYpoint() != null ) {
            BigDecimal gifCattyRatio = sysConfigCacheUtil.getConfBd( "gif_tcatty_ratio" );
            if ( liveUser.getYpoint().compareTo( gifCattyRatio ) == 1
                    || liveUser.getYpoint().compareTo( BigDecimal.ZERO ) == -1 ) {
                return AjaxResult.error( "礼物抽成比例不能大于上限" + gifCattyRatio );
            }
        }
        newLiveUser.setXpoint( liveUser.getXpoint() );
        newLiveUser.setYpoint( liveUser.getYpoint() );
        newLiveUser.setWeiboMoney( liveUser.getWeiboMoney() );
        newLiveUser.setWeixinPrice( liveUser.getWeixinPrice() );
        return toAjax( liveUserService.updateLiveUser( newLiveUser ) );
    }

    @PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
    @Log( title = "更改收费开关", businessType = BusinessType.UPDATE )
    @PutMapping( "/openPay" )
    public AjaxResult openPay( @RequestBody LiveUser liveUser ) {
        LiveUser newLiveUser = new LiveUser();
        newLiveUser.setId( liveUser.getId() );
        newLiveUser.setOpenPay( liveUser.getOpenPay() );
        return toAjax( liveUserService.updateLiveUser( newLiveUser ) );
    }

    /**
     * 重置提现密码
     */
    @ApiOperation( value = "重置提现密码", notes = "重置提现密码" )
    @PostMapping( "/reset" )
    @Log( title = "重置提现密码", businessType = BusinessType.UPDATE )
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:reset')" )
    public Object reset( HttpServletRequest request, LiveUser liveUser ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( liveUser.getGoogleAuthCode() == null ) {
            rspBase.setMsg( "请输入google验证码" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        LoginUser loginUser        = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName( loginUser.getUsername() );

        if ( !org.springframework.util.StringUtils.hasText( googleAuthSecret ) ) {
            rspBase.setMsg( "未绑定google验证秘钥，无法审核" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        if ( googleAuthSecret.length() == 32 ) {
            rspBase.setMsg( "google验证秘钥未加密，请重新登录" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                "secretkey" + "/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, liveUser.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        liveUserService.updateLiveUser( liveUser );
        return new RspBase();
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
    public AjaxResult gofamiily( @RequestBody LiveUser user ) {
        return liveUserService.updateFamilyID( user.getFamilyId(), user.getUserIdSet() );
    }

    @ApiOperation( "修改印票" )
    @Log( title = "修改印票", businessType = BusinessType.UPDATE )
    @PostMapping( "/updateTicket" )
    public AjaxResult updateTicket( LiveUser user ) {
        //判断主播当前是否在关播状态
        LiveVideo liveVideo = liveVideoService.liveInStatus( user.getId() );
        if ( liveVideo != null && liveVideo.getLiveIn() != 0 ) {
            return AjaxResult.error( 100, "该主播不在关播状态,修改印票失败" );
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
    public void anchorAwardExport( ReqLotteryBat req, HttpServletResponse response ) {
        if ( StringUtils.isNotBlank( req.getUpdateTime() ) ) {
            req.setStartTime( req.getUpdateTime() + " 00:00:00" );
            req.setEndTime( req.getUpdateTime() + " 23:59:59" );
        }
        List<RspLotteryBet> list = liveUserService.selectAnchorAward( req );
        ExportExcelUtil.exportExcel( list, "公司入款", "公司入款信息表", RspLotteryBet.class, response );
    }

    /**
     * 踢出家族
     */
    @Log( title = "踢出家族", businessType = BusinessType.AUDIT )
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
    @PutMapping( value = "/kickOutLive/{id}" )
    public AjaxResult kickOutLive( @PathVariable( "id" ) String id ) {
        return liveUserService.kickOutLiveById( Collections.singleton( id ) );
    }

    @Log( title = "踢出家族", businessType = BusinessType.AUDIT )
    @PreAuthorize( "@ss.hasPermi('admin:liveUser:edit')" )
    @PutMapping( value = "/kickOutLive" )
    public AjaxResult kickOutLive( @RequestBody LiveUser user ) {
        return liveUserService.kickOutLiveById( user.getUserIdSet() );
    }

    @GetMapping( "/authList" )
    public TableDataInfo authList( LiveUser liveUser ) {
        startPage();
        List<LiveUser> authList = liveUserService.selectLiveUserAuthList( liveUser );
        return getDataTable( authList );
    }

    @PreAuthorize( "@ss.hasPermi('admin:liveUser:changeAuth')" )
    @Log( title = "修改主播认证状态", businessType = BusinessType.UPDATE )
    @PutMapping( "/change_auth" )
    public Object changeAuth( LiveUser liveUser ) {
        LiveUser getLiveUser = liveUserService.selectLiveUserById( liveUser.getId() );
        if ( liveUser != null ) {
            getLiveUser.setIsAuthentication( liveUser.getIsAuthentication() );
            liveUserService.updateLiveUser( liveUser );
        }
        return new RspBase();
    }

}
