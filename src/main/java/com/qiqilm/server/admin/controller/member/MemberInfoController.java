package com.qiqilm.server.admin.controller.member;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.MemberForbidUtil;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.LiveGuardUser;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.MemberMoney;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.vo.*;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.mapper.MemberMoneyMapper;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用户信息Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping( "/member/memberInfo" )
@Log4j2
public class MemberInfoController extends BaseController {
    @Resource
    private IMemberInfoService memberInfoService;
    @Resource
    private MemberMoneyMapper  memberMoneyMapper;
    @Resource
    private TokenService       tokenService;
    @Resource
    private ISysUserService    sysUserService;
    @Resource
    private MemberCacheManager memberCacheManager;
    @Resource
    private RedisUtil          redisUtil;
    @Resource
    private MemberForbidUtil   memberForbidUtil;
    @Resource
    private ISysUserService    userService;
    @Resource
    private ImApi              imApi;

    /**
     * 查询用户信息列表
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list( MemberInfo memberInfo ) {
        startPage();
        List<MemberInfo> list = memberInfoService.selectMemberInfoList( memberInfo );
        return getDataTable( list );
    }

    /**
     * 导出用户信息列表
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:export')" )
    @Log( title = "导出", businessType = BusinessType.EXPORT )
    @GetMapping( "/export" )
    public AjaxResult export( MemberInfo memberInfo, HttpServletResponse response ) {
        List<MemberInfo> list = memberInfoService.selectMemberInfoList( memberInfo );
        if ( list.size() <= DownLoadTime.downLoadLimit ) {
            ExportExcelUtil.exportExcel( list, "用户信息", "用户信息表", MemberInfo.class, response );
            return AjaxResult.success( "下载成功" );
        } else {
            return AjaxResult.error( "导出条数超过20万条" );
        }
    }

    /**
     * 获取用户信息详细信息
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:query')" )
    @GetMapping( value = "/{id}" )
    public AjaxResult getInfo( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( memberInfoService.selectMemberInfoById( id ) );
    }

    /**
     * 统计会员余额
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:query')" )
    @GetMapping( "/listCount" )
    public Map listCount( MemberInfo memberInfo ) {
        return memberInfoService.listCount( memberInfo );
    }

    /**
     * 获取完整手机号
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:fullMobile')" )
    @GetMapping( value = "/fullMobile/{id}" )
    public AjaxResult fullMobile( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( memberInfoService.selectMemberInfoById( id ) );
    }

    /**
     * 手机号批量更新密码
     */
    @PostMapping( value = "/memberSmallFeatures" )
    public Object memberSmallFeatures( ReqSmallFeatures req ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( req.getGoogleAuthCode() == null ) {
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

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, req.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        return AjaxResult.success( memberInfoService.updatePhones( req ) );
    }

    /**
     * 批量会员ID查询手机号
     */
    @PostMapping( value = "/queryPhones" )
    public Object queryPhones( @RequestBody ReqSmallFeatures req ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( req.getGoogleAuthCode() == null ) {
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

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, req.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        return AjaxResult.success( memberInfoService.queryPhones( req ) );
    }

    @RequestMapping( value = "/batchInsertShops", method = RequestMethod.POST )
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult batchInsert( @RequestParam( "excelFile" ) MultipartFile excelFile ) throws Exception {
        Set<String> memberSet    = new HashSet<>();
        Set<String> duplicateSet = new HashSet<>();

        List<MemberMoney> memberMoneyList = new ArrayList<>();
        try ( InputStream inputStream = excelFile.getInputStream(); Workbook workbook = WorkbookFactory.create( inputStream ) ) {
            //工作表对象
            Sheet sheet = workbook.getSheetAt( 0 );

            //得到指定的单元格
            for ( int i = 1; i < sheet.getLastRowNum() + 1; i++ ) {
                Row    row   = sheet.getRow( i );
                String cell1 = null;
                String cell2 = null;
                String cell3 = null;
                for ( int j = 0; j < 3; j++ ) {
                    Cell cell = row.getCell( j );
                    if ( cell != null ) {
                        cell.setCellType( CellType.STRING );
                        String data = cell.getStringCellValue();
                        if ( j == 0 ) {
                            cell1 = data.trim();
                        } else if ( j == 1 ) {
                            cell2 = data.trim();
                        } else {
                            cell3 = data.trim();
                        }
                    }
                }
                if ( StringUtils.isBlank( cell1 ) || StringUtils.isBlank( cell2 ) ) {
                    return AjaxResult.error( "第" + ( i + 1 ) + "行数据不完整" );
                }
                if ( StringUtils.isBlank( cell3 ) ) {
                    cell3 = "1";
                }
                if ( memberSet.contains( cell1 ) ) {
                    duplicateSet.add( cell1 );
                } else {
                    memberSet.add( cell1 );
                    MemberMoney memberMoney = new MemberMoney();
                    memberMoney.setMemberId( cell1 );
                    memberMoney.setMoney( new BigDecimal( cell2 ) );
                    memberMoney.setBeat( new BigDecimal( cell3 ) );
                    memberMoneyList.add( memberMoney );
                }
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(), e );
        }
        if ( !CollectionUtils.isEmpty( duplicateSet ) ) {
            return AjaxResult.error( "数据重复，请检查 - 重复ID: " + StringUtils.join( duplicateSet, "," ) );
        }
        if ( CollectionUtils.isEmpty( memberMoneyList ) ) {
            return AjaxResult.error( "无数据,或者数据格式不正确" );
        }
        //清除表中数据
        memberMoneyMapper.clear();
        memberMoneyMapper.insertBatch( memberMoneyList );
        return AjaxResult.success();
    }

    /**
     * 获取用户登录地址
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:query')" )
    @GetMapping( value = "/getMemberLoginAddress/{id}" )
    public AjaxResult getMemberLoginAddress( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( memberInfoService.getMemberLoginAddress( id ) );
    }

    /**
     * 获取用户线上充值历史金额
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:query')" )
    @GetMapping( value = "/getHistoryRecharge/{id}" )
    public AjaxResult getHistoryRecharge( @PathVariable( "id" ) String id ) {
        return AjaxResult.success( memberInfoService.getHistoryRecharge( id ) );
    }

    /**
     * 新增用户信息
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:add')" )
    @Log( title = "用户信息", businessType = BusinessType.INSERT )
    @PostMapping
    public AjaxResult add( @RequestBody MemberInfo memberInfo ) {
        //        String userName = memberInfo.getUserName();
        String password = memberInfo.getPassword();
        String phone    = memberInfo.getPhone();
        if ( phone == null ) {
            return AjaxResult.error( "手机号不能为空" );
        }
/*
        if (userName == null) {
            return AjaxResult.error("账号信息为空");
        }
        if (userName.length() < 6 || userName.length() > 15) {
            return AjaxResult.error("账号长度必须大于等于6小于15");
        }
        userName = userName.toLowerCase();
        if (!this.checkABC(userName)) {
            return AjaxResult.error("第一个字符须字母");
        }
        if (!this.checkUserName(userName)) {
            return AjaxResult.error("账号不合合法");
        }*/

        if ( StringUtils.isEmpty( password ) ) {
            return AjaxResult.error( "密码不能为空" );
        }

        if ( password.length() < 6 || password.length() > 15 ) {
            return AjaxResult.error( "密码长度必须大于等于6小于15" );
        }
        return memberInfoService.insertMemberInfo( memberInfo );
    }

    /**
     * 注册账号检查
     */
    private boolean checkABC( String username ) {
        if ( username.length() < 1 ) {
            return false;
        }
        Pattern pattern = Pattern.compile( "[a-z]" );
        return pattern.matcher( username.substring( 0, 1 ) ).find();
    }

    /**
     * 注册账号检查
     */
    private boolean checkUserName( String username ) {
        Pattern pattern = Pattern.compile( "[0-9a-z.@]" );
        return pattern.matcher( username ).find();
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:edit')" )
    @Log( title = "用户信息", businessType = BusinessType.UPDATE )
    @PutMapping
    public AjaxResult edit( @RequestBody MemberInfo memberInfo ) {
        return toAjax( memberInfoService.updateMemberInfo( memberInfo ) );
    }

    @Log( title = "用户信息", businessType = BusinessType.UPDATE )
    @PutMapping( "/changeSpeak" )
    public AjaxResult changeSpeak( @RequestBody MemberInfo memberInfo ) {
        //备注禁言原因
        if ( memberInfo.getRemark() != null ) {
            LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
            String    username  = loginUser.getUser().getUserName();
            memberInfo.setEmail( "禁言操作人" + username + ";禁言原因:" + memberInfo.getRemark() );
        }
        return toAjax( memberInfoService.changeSpeak( memberInfo ) );
    }

    @PreAuthorize( "@ss.hasPermi('member:memberInfo:changeStatus')" )
    @Log( title = "修改用户状态", businessType = BusinessType.UPDATE )
    @PutMapping( "/change-status" )
    public Object changeStatus( ReqMemberInfo req ) {
        RspBase    rspBase       = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo    = memberInfoService.selectMemberInfoById( req.getId() );
        if ( memberInfo.getStatus() == 2 ) {
            return AjaxResult.error( "测试号不允许修改为其它状态" );
        }
        newMemberInfo.setStatus( req.getStatus() );
        memberForbidUtil.setPlatformUserStatus( memberInfo.getId(), req.getStatus() );

        if ( 1 == req.getStatus() ) {
            newMemberInfo.setLoginNum( 0 );
        }
        newMemberInfo.setId( memberInfo.getId() );
        memberInfoService.updateMemberInfo( newMemberInfo );
        if ( req.getStatus() == 0 ) {
            memberCacheManager.delToken( memberInfo.getId() );
        }
        return rspBase;
    }

    @PreAuthorize( "@ss.hasPermi('member:memberInfo:changeStatus')" )
    @Log( title = "修改用户状态", businessType = BusinessType.UPDATE )
    @PutMapping( "/change-statusBan" )
    public Object changeStatusBan( @RequestBody ReqMemberInfo req ) {
        RspBase    rspBase       = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo    = memberInfoService.selectMemberInfoById( req.getId() );
        newMemberInfo.setStatus( req.getStatus() );
        memberForbidUtil.setPlatformUserStatus( memberInfo.getId(), req.getStatus() );
        //备注禁用原因
        if ( req.getRemark() != null ) {
            LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
            String    username  = loginUser.getUser().getUserName();
            newMemberInfo.setEmail( "禁用操作人" + username + ";禁用原因:" + req.getRemark() );
        }
        if ( 1 == req.getStatus() ) {
            newMemberInfo.setLoginNum( 0 );
        }
        newMemberInfo.setId( memberInfo.getId() );
        memberInfoService.updateMemberInfo( newMemberInfo );
        if ( req.getStatus() == 0 ) {
            memberCacheManager.delToken( memberInfo.getId() );
        }
        return rspBase;
    }

    /**
     * 重置密码
     */
    @ApiOperation( value = "重置密码", notes = "重置密码" )
    @RequestMapping( value = "/reset", method = RequestMethod.POST )
    @Log( title = "重置密码", businessType = BusinessType.UPDATE )
    public Object reset( HttpServletRequest request, ReqAddScore req ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( req.getGoogleAuthCode() == null ) {
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

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, req.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId( req.getId() );
        memberInfo.setPassword( req.getPassword() );
        int i = memberInfoService.updateMemberInfo( memberInfo );
        if ( i > 0 ) {
            memberCacheManager.delToken( memberInfo.getId() );
        }
        return new RspBase();
    }

    /**
     * 加分
     */
    @ApiOperation( value = "加分", notes = "人工入款" )
    @RequestMapping( value = "/addScore", method = RequestMethod.POST )
    @Log( title = "加分", businessType = BusinessType.UPDATE )
    public Object addScore( HttpServletRequest request, ReqAddScore req ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( req.getGoogleAuthCode() == null ) {
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

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, req.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }

        if ( !redisUtil.lock( EnumLock.member, "addScore" + req.getId(), "1", 15 ) ) {
            rspBase.setMsg( "请勿重复提交" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        String ip = UserDataUtil.getIp( request );
        rspBase = memberInfoService.addMemberMoneyOnly( ip, loginUser, req );
        if ( rspBase.getCode() == 2 ) {
            redisUtil.unLock( EnumLock.member, "addScore" + req.getId() );
            return rspBase;
        }
        rspBase.setCode( 0 );
        return rspBase;
    }

    /**
     * 发送短信
     */
    @ApiOperation( value = "会员发送短信", notes = "会员发送短信" )
    @RequestMapping( value = "/sendMsg", method = RequestMethod.POST )
    @Log( title = "会员发送短信", businessType = BusinessType.UPDATE )
    public RspBase sendMsg( @RequestBody Map map ) throws Exception {
        RspBase rspBase  = new RspBase();
        String  msg      = ( String ) map.get( "msg" );
        String  memberId = ( String ) map.get( "memberId" );
        if ( StringUtils.isNotBlank( msg ) && StringUtils.isNotBlank( memberId ) ) {
            sysUserService.sendMsg( msg, memberId );
            rspBase.setMsg( "发送成功" );
        } else {
            rspBase.setMsg( "发送失败" );
        }
        return rspBase;
    }

    /**
     * 修改邀请码
     */
    @ApiOperation( value = "会员修改邀请码", notes = "会员修改邀请码" )
    @RequestMapping( value = "/updateInviterCode", method = RequestMethod.POST )
    @Log( title = "会员修改邀请码", businessType = BusinessType.UPDATE )
    public AjaxResult updateInviterCode( @RequestBody Map map ) throws Exception {
        String memberId       = ( String ) map.getOrDefault( "memberId", "" );
        String inviterCode    = ( String ) map.getOrDefault( "inviterCode", "" );
        String googleAuthCode = ( String ) map.getOrDefault( "googleAuthCode", "" );
        if ( StringUtils.isEmpty( inviterCode ) ) {
            return AjaxResult.error( "邀请不能为空" );
        }
        if ( StringUtils.isEmpty( googleAuthCode ) ) {
            return AjaxResult.error( "谷歌验证码不能为空" );
        }

        AjaxResult x = checkGoogle( googleAuthCode );
        if ( x != null ) {
            return x;
        }
        return memberInfoService.updateInviterCode( inviterCode, memberId );
    }

    private AjaxResult checkGoogle( String googleAuthCode ) throws Exception {
        LoginUser loginUser        = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName( loginUser.getUsername() );

        if ( !org.springframework.util.StringUtils.hasText( googleAuthSecret ) ) {
            return AjaxResult.error( "未绑定google验证秘钥，无法审核" );
        }
        if ( googleAuthSecret.length() == 32 ) {
            return AjaxResult.error( "google验证秘钥未加密，请重新登录" );
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr(
                "secretkey" + "/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, Integer.parseInt( googleAuthCode ) ) ) {
            return AjaxResult.error( "google验证码不正确，请检查" );
        }
        return null;
    }

    /**
     * 发送短信
     */
    @ApiOperation( value = "修改手机号", notes = "修改手机号" )
    @RequestMapping( value = "/updateMobile", method = RequestMethod.POST )
    @Log( title = "会员发送短信", businessType = BusinessType.UPDATE )
    public AjaxResult updateMobile( @RequestBody Map map ) throws Exception {
        String memberId       = ( String ) map.get( "memberId" );
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
        return sysUserService.updateMobile( newMobile, oldMobile, memberId );
    }

    /**
     * 查询资金明细列表
     */
    @ApiOperation( value = "查询资金明细列表", notes = "查询资金明细列表" )
    @RequestMapping( value = "/report", method = RequestMethod.GET )
    public PageBO<WithdrawReport> findMemberCardList( @RequestParam( "id" ) String memberId, PageVO req ) {
        return memberInfoService.withdrawReport( memberId, req.getPage(), req.getLimit() );
    }

    /**
     * 会员银行卡列表
     */
    @ApiOperation( value = "会员银行卡列表", notes = "会员银行卡列表" )
    @RequestMapping( value = "/card-list", method = RequestMethod.GET )
    public PageBO<MemberCard> findMemberCardList( @RequestParam( "id" ) String memberId, ReqMemberInfo req ) {
        return memberInfoService.findMemberCardPage( memberId, req.getPage(), req.getLimit(), req.getOrderBy() );
    }

    @ApiOperation( value = "重置保险箱账户", notes = "重置保险箱账户" )
    @PostMapping( "/resetPassword" )
    public Object resetPassword( HttpServletRequest request, @RequestParam( value = "userId" ) String userId ) {

        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId( userId );
        memberInfo.setBoxPass( "" );
        memberInfoService.updateMemberInfo( memberInfo );

        RspBase rspBase = new RspBase();
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData( "成功" );
        return rspBase;
    }

    @ApiOperation( value = "重置提现", notes = "重置提现" )
    @PostMapping( "/resettx" )
    public Object resettx( HttpServletRequest request, MemberInfo memberInfo ) throws Exception {
        RspBase<?> checkRspBase = this.checkGoogleAuth( memberInfo );
        if ( checkRspBase != null ) {
            return checkRspBase;
        }
        memberInfo.setId( memberInfo.getId() );
        memberInfo.setWithdrawalPass( "" );
        memberInfoService.updateMemberInfo( memberInfo );

        RspBase rspBase = new RspBase();
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData( "成功" );
        return rspBase;
    }

    @ApiOperation( value = "修复打码", notes = "修復打碼" )
    @Log( title = "打码修复", businessType = BusinessType.INSERT )
    @PostMapping( "/memberBcodeRepair" )
    public Object memberBcodeRepair( HttpServletRequest request, MemberInfo memberInfo ) throws Exception {
        RspBase<?> checkRspBase = this.checkGoogleAuth( memberInfo );
        if ( checkRspBase != null ) {
            return checkRspBase;
        }
        String memberId = memberInfo.getId();
        memberInfoService.repairMemberBcode( memberId );

        RspBase rspBase = new RspBase();
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData( "成功" );
        return rspBase;
    }

    @Log( title = "修改vip等级", businessType = BusinessType.UPDATE )
    @ApiOperation( value = "修改vip等级", notes = "修改vip等级" )
    @PostMapping( "/updateVip" )
    public Object updateVip( HttpServletRequest request, MemberInfo memberInfo ) throws Exception {
        RspBase rspBase = new RspBase();
        if ( memberInfo.getVip() > 50 ) {
            rspBase.setCode( 1 );
            rspBase.setData( "vip等级最大为50级" );
            return rspBase;
        }
        MemberInfo memberInfo1 = memberInfoService.selectMemberInfoById( memberInfo.getId() );
        if ( memberInfo1 != null && memberInfo1.getVip() > memberInfo.getVip() ) {
            rspBase.setCode( 1 );
            rspBase.setData( "vip等级修改不能小于之前的等级" );
            return rspBase;
        }
        String  memberId = memberInfo.getId();
        Integer vip      = memberInfo.getVip();
        String  nickName = memberInfo.getNickName();
        memberInfoService.updateVip( memberId, vip, nickName );
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData( "vip等级修改成功" );
        return rspBase;
    }

    @Log( title = "解绑银行卡", businessType = BusinessType.UPDATE )
    @PutMapping( "/unbindCard" )
    public Object unbindCard( @RequestBody MemberCard memberCard ) {
        AjaxResult ajaxResult = memberInfoService.unbindCard( memberCard );
        return ( ajaxResult );
    }

    @Log( title = "修改用户银行卡信息", businessType = BusinessType.UPDATE )
    @PutMapping( "/changeBank" )
    public Object changeBank( @RequestBody MemberCard memberCard ) {
        AjaxResult ajaxResult = memberInfoService.changeBank( memberCard );
        return ( ajaxResult );
    }

    @Log( title = "修改用户备注", businessType = BusinessType.UPDATE )
    @PutMapping( "/updateEmail" )
    public Object updateEmail( @RequestBody MemberInfo memberInfo ) {
        AjaxResult ajaxResult = memberInfoService.changeEmail( memberInfo );
        return ( ajaxResult );
    }

    @ApiOperation( value = "禁言用户IM", notes = "禁言用户IM" )
    @PostMapping( "/imDealBan" )
    public Object imDealBan( MemberInfo memberInfo ) {
        if ( Objects.isNull( memberInfo.getBanSpeakTime() ) ) {
            return AjaxResult.success( "禁言时间不能为空" );
        }
        //im禁言备注
        if ( StringUtils.isNotBlank( memberInfo.getEmail() ) ) {
            memberInfoService.updateMemberInfo( memberInfo );
        }
        memberInfoService.updataStatus( memberInfo );
        if ( imApi.nospeakingT( memberInfo.getId(), memberInfo.getBanSpeakTime() ) ) {
            log.info( "IM禁言成功" );
            return AjaxResult.success( "IM禁言成功" );
        }
        return AjaxResult.success( "正在禁言中" );
    }

    /**
     * 会员银行卡列表
     */
    @ApiOperation( value = "会员关注主播列表", notes = "会员关注主播列表" )
    @RequestMapping( value = "/follow-list", method = RequestMethod.GET )
    public AjaxResult findMemberFollowList( ReqMemberInfo req ) {
        return memberInfoService.findMemberFollowList( req.getId() );
    }

    /**
     * handle status set 0 (Deactivate)
     */
    @Log( title = "根据IP禁用用户", businessType = BusinessType.UPDATE )
    @PutMapping( "/ban-ip" )
    public int banIp( MemberInfo memberInfo ) {
        if ( StringUtils.isBlank( memberInfo.getLoginIp() ) ) {
            throw new BusinessException( "封禁IP为空" );
        }
        if ( UserDataUtil.internalIp( memberInfo.getLoginIp() ) ) {
            throw new BusinessException( "IP为内网,无法禁用" );
        }
        memberInfo.setLoginIp( memberInfo.getLoginIp() );
        memberInfo.setRealName( memberInfo.getRealName() );
        return memberInfoService.banStatus( memberInfo );
    }

    /**
     * handle status set 1 (activate)
     */
    @Log( title = "根据IP禁用用户", businessType = BusinessType.UPDATE )
    @PutMapping( "/unBlock-ip" )
    public int unBlockIp( MemberInfo memberInfo ) {
        if ( StringUtils.isBlank( memberInfo.getLoginIp() ) ) {
            throw new BusinessException( "封禁IP为空" );
        }
        if ( UserDataUtil.internalIp( memberInfo.getLoginIp() ) ) {
            throw new BusinessException( "IP为内网,无法解禁" );
        }
        memberInfo.setLoginIp( memberInfo.getLoginIp() );
        memberInfo.setRealName( memberInfo.getRealName() );
        return memberInfoService.unBlockStatus( memberInfo );
    }

    @GetMapping( "/personal-report/{memberId}" )
    public AjaxResult personalReport( @PathVariable String memberId, HttpServletRequest request ) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String                startTime    = parameterMap.get( "dateRange[0]" )[ 0 ];
        String                endTime      = parameterMap.get( "dateRange[1]" )[ 0 ];
        return memberInfoService.personalReport( startTime, endTime, memberId );
    }

    @Log( title = "保险箱余额转出", businessType = BusinessType.UPDATE )
    @PostMapping( "/boxDish" )
    public RspBase<?> boxDish( @RequestBody MemberInfo memberInfo ) throws Exception {
        RspBase<?> checkRspBase = this.checkGoogleAuth( memberInfo );
        if ( checkRspBase != null ) {
            return checkRspBase;
        }

        return memberInfoService.boxDish( memberInfo.getId() );
    }

    private RspBase<?> checkGoogleAuth( MemberInfo memberInfo ) throws Exception {
        RspBase<?> rspBase = new RspBase<>();
        if ( memberInfo.getGoogleAuthCode() == null ) {
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

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, memberInfo.getGoogleAuthCode() ) ) {
            rspBase.setMsg( "google验证码不正确，请检查" );
            rspBase.setCode( 1 );
            return rspBase;
        }
        return null;
    }

    @GetMapping( "/liveGuardUser/list" )
    public TableDataInfo list( LiveGuardUser liveGuardUser ) {
        startPage();
        List<LiveGuardUser> list = memberInfoService.selectLiveGuard( liveGuardUser );
        return getDataTable( list );
    }


    @PreAuthorize( "@ss.hasPermi('member:withdrawStatus:edit')" )
    @Log( title = "取款状态", businessType = BusinessType.UPDATE )
    @PutMapping( "/withdrawStatus" )
    public AjaxResult withdrawStatus( MemberInfo memberInfo ) {
        log.info( memberInfo );
        return toAjax( memberInfoService.withdrawStatus( memberInfo ) );
    }

    /**
     * 修改总打码和VIP等级
     */
    @PreAuthorize( "@ss.hasPermi('member:memberInfo:editCode')" )
    @Log( title = "修改总打码和VIP等级", businessType = BusinessType.UPDATE )
    @PutMapping( "/updateCode" )
    public AjaxResult updateCode( @RequestBody MemberInfo memberInfo ) {
        return toAjax( memberInfoService.updateCodeTotalVipLevel( memberInfo ) );
    }

}
