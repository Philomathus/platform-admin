package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.vo.*;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/admin/memberInfo")
public class MemberInfoController extends BaseController {
    @Autowired
    private IMemberInfoService memberInfoService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private MemberCacheManager memberCacheManager;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(MemberInfo memberInfo) {
        startPage();
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MemberInfo memberInfo) {
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        ExcelUtil<MemberInfo> util = new ExcelUtil<MemberInfo>(MemberInfo.class);
        return util.exportExcel(list, "memberInfo");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoService.selectMemberInfoById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MemberInfo memberInfo) {
        return toAjax(memberInfoService.insertMemberInfo(memberInfo));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MemberInfo memberInfo) {
        return toAjax(memberInfoService.updateMemberInfo(memberInfo));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('admin:memberInfo:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(memberInfoService.deleteMemberInfoByIds(ids));
    }

    @PutMapping("/change-status")
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE)
    public Object changeStatus(ReqMemberInfo req) {
        RspBase rspBase = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo = memberInfoService.selectMemberInfoById(req.getId());
        newMemberInfo.setStatus(Long.valueOf(req.getStatus()));
        if (1 == req.getStatus()) {
            newMemberInfo.setLoginNum(Long.valueOf(0));
        }
        newMemberInfo.setId(memberInfo.getId());
        memberInfoService.updateMemberInfo(newMemberInfo);
        if (req.getStatus() == 0) {
            memberCacheManager.delToken(memberInfo.getId());
        }
        return rspBase;
    }

    /**
     * 重置密码123456
     *
     * @return
     */
    @ApiOperation(value = "重置密码123456", notes = "重置密码123456")
    @RequestMapping(value = "/reset/{id}", method = RequestMethod.DELETE)
    @Log(title = "重置密码123456", businessType = BusinessType.UPDATE)
    public Object reset(@ApiParam(required = true, name = "id", value = "系统编号")
                        @PathVariable("id") String id) {
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId(id);
        memberInfo.setPassword("123456");
        memberInfoService.updateMemberInfo(memberInfo);
        return new RspBase();
    }

    /**
     * 加分
     *
     * @return
     */
    @ApiOperation(value = "加分", notes = "人工入款")
    @RequestMapping(value = "/addScore", method = RequestMethod.POST)
    @Log(title = "加分", businessType = BusinessType.UPDATE)
    public Object addScore(HttpServletRequest request, ReqAddScore req) throws Exception {
        RspBase rspBase = new RspBase();
        if (req.getGoogleAuthCode() == null) {
            rspBase.setMsg("请输入google验证码");
            rspBase.setCode(1);
            return rspBase;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

        if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
            rspBase.setMsg("未绑定google验证秘钥，无法审核");
            rspBase.setCode(1);
            return rspBase;
        }
        if (googleAuthSecret.length() == 32) {
            rspBase.setMsg("google验证秘钥未加密，请重新登录");
            rspBase.setCode(1);
            return rspBase;
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, req.getGoogleAuthCode())) {
            rspBase.setMsg("google验证码不正确，请检查");
            rspBase.setCode(1);
            return rspBase;
        }

        String ip = UserDataUtil.getIp(request);
        if (!redisUtil.lock(EnumLock.member, req.getId(), "1", 5)) {
            throw new BusinessException("请勿重复提交");
        }
        rspBase = memberInfoService.addMemberMoneyOnly(ip, req.getId(), req.getScore(), req.getBeatNum(), req.getMk(), req.getOrdermk(), loginUser.getUsername());
        redisUtil.unLock(EnumLock.member, req.getId());
        return rspBase;
    }


    /**
     * 查询资金明细列表
     *
     * @return
     */
    @ApiOperation( value = "查询资金明细列表", notes = "查询资金明细列表" )
    @RequestMapping( value = "/report", method = RequestMethod.GET )
    public PageBO<WithdrawReport> findMemberCardList(@RequestParam( "id" ) String memberId, PageVO req ) {
        return memberInfoService.withdrawReport( memberId, req.getPage(), req.getLimit() );
    }

    /**
     * 会员银行卡列表
     *
     * @return
     */
    @ApiOperation( value = "会员银行卡列表", notes = "会员银行卡列表" )
    @RequestMapping( value = "/card-list", method = RequestMethod.GET )
    public PageBO<MemberCard> findMemberCardList(@RequestParam( "id" ) String memberId, ReqMemberInfo req ) {
        return memberInfoService.findMemberCardPage( memberId, req.getPage(), req.getLimit() );
    }

    @ApiOperation( value = "重置保险箱账户", notes = "重置保险箱账户" )
    @PostMapping( "/resetPassword" )
    public Object resetPassword( HttpServletRequest request,
                                 @RequestParam(value = "userId") String userId ) {
        RspBase rspBase = new RspBase();
        MemberInfo memberInfo=new MemberInfo();
        memberInfo.setId(userId);
        memberInfo.setBoxPass("");
        memberInfoService.updateMemberInfo(memberInfo);
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData("成功");
        return rspBase;
    }
    @ApiOperation( value = "重置体现", notes = "重置体现" )
    @PostMapping( "/resettx" )
    public Object resettx( HttpServletRequest request,
                           MemberInfo memberInfo) throws Exception {
        RspBase rspBase = new RspBase();
        if (  memberInfo.getGoogleAuthCode() == null ) {
            rspBase.setMsg( "请输入google验证码" );
            rspBase.setCode(1);
            return rspBase;
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

        if ( !org.springframework.util.StringUtils.hasText( googleAuthSecret ) ) {
            rspBase.setMsg( "未绑定google验证秘钥，无法审核" );
            rspBase.setCode(1);
            return rspBase;
        }
        if ( googleAuthSecret.length() == 32 ) {
            rspBase.setMsg(  "google验证秘钥未加密，请重新登录" );
            rspBase.setCode(1);
            return rspBase;
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey( googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey/googleAuthPrivateKey" ) );

        if ( !GoogleAuthUtil.verifyCode( googleAuthKey, memberInfo.getGoogleAuthCode()  )  ) {
            rspBase.setMsg ( "google验证码不正确，请检查" );
            rspBase.setCode(1);
            return rspBase;
        }
        memberInfo.setId(memberInfo.getId());
        memberInfo.setWithdrawalPass("");
        memberInfoService.updateMemberInfo(memberInfo);
        rspBase.setCode( Constants.URC_SUCCESS );
        rspBase.setData("成功");
        return rspBase;
    }
}
