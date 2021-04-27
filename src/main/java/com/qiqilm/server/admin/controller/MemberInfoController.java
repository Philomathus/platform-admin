package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.cache.MemberCacheManager;
import com.qiqilm.server.admin.cache.MemberForbidUtil;
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
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 用户信息Controller
 *
 * @author 77tv
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/member/memberInfo")
@Log4j2
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
    @Autowired
    private MemberForbidUtil memberForbidUtil;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ImApi imApi;
    /**
     * 查询用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(MemberInfo memberInfo) {
        startPage();
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        return getDataTable(list);
    }

    /**
     * 导出用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:export')")
    @Log(title = "导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(MemberInfo memberInfo, HttpServletResponse response) {
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        ExportExcelUtil.exportExcel( list, "用户信息", "用户信息表", MemberInfo.class, response );
    }

    /**
     * 获取用户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoService.selectMemberInfoById(id));
    }

    /**
     * 新增用户信息
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:add')")
    @Log(title = "用户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MemberInfo memberInfo) {
//        String userName = memberInfo.getUserName();
        String password = memberInfo.getPassword();
        String phone = memberInfo.getPhone();
        if (phone == null) {
            return AjaxResult.error("手机号不能为空");
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

        if (StringUtils.isEmpty(password)) {
            return AjaxResult.error("密码不能为空");
        }

        if (password.length() < 6 || password.length() > 15) {
            return AjaxResult.error("密码长度必须大于等于6小于15");
        }
        return memberInfoService.insertMemberInfo(memberInfo);
    }

    /**
     * 注册账号检查
     */
    private boolean checkABC(String username) {
        if (username.length() < 1) {
            return false;
        }
        Pattern pattern = Pattern.compile("[a-z]");
        return pattern.matcher(username.substring(0, 1)).find();
    }


    /**
     * 注册账号检查
     */
    private boolean checkUserName(String username) {
        Pattern pattern = Pattern.compile("[0-9a-z.@]");
        return pattern.matcher(username).find();
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:edit')")
    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MemberInfo memberInfo) {
        return toAjax(memberInfoService.updateMemberInfo(memberInfo));
    }

    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeSpeak")
    public AjaxResult changeSpeak(@RequestBody MemberInfo memberInfo) {
        //备注禁言原因
        if (memberInfo.getRemark() != null) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
            String username = loginUser.getUser().getUserName();
            memberInfo.setEmail("禁言操作人" + username + ";禁言原因:" + memberInfo.getRemark());
        }
        return toAjax(memberInfoService.changeSpeak(memberInfo));
    }

    @PutMapping("/change-status")
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE)
    public Object changeStatus(ReqMemberInfo req) {
        RspBase rspBase = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo = memberInfoService.selectMemberInfoById(req.getId());
        newMemberInfo.setStatus(req.getStatus());
        memberForbidUtil.setPlatformUserStatus(memberInfo.getId(),req.getStatus());

        if (1 == req.getStatus()) {
            newMemberInfo.setLoginNum(0);
        }
        newMemberInfo.setId(memberInfo.getId());
        memberInfoService.updateMemberInfo(newMemberInfo);
        if (req.getStatus() == 0) {
            memberCacheManager.delToken(memberInfo.getId());
        }
        return rspBase;
    }

    @PutMapping("/change-statusBan")
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE)
    public Object changeStatusBan(@RequestBody ReqMemberInfo req) {
        RspBase rspBase = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo = memberInfoService.selectMemberInfoById(req.getId());
        newMemberInfo.setStatus(req.getStatus());
        memberForbidUtil.setPlatformUserStatus(memberInfo.getId(),req.getStatus());
        //备注禁用原因
        if (req.getRemark() != null) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
            String username = loginUser.getUser().getUserName();
            newMemberInfo.setEmail("禁用操作人" + username + ";禁用原因:" + req.getRemark());
        }
        if (1 == req.getStatus()) {
            newMemberInfo.setLoginNum(0);
        }
        newMemberInfo.setId(memberInfo.getId());
        memberInfoService.updateMemberInfo(newMemberInfo);
        if (req.getStatus() == 0) {
            memberCacheManager.delToken(memberInfo.getId());
        }
        return rspBase;
    }

    /**
     * 重置密码
     *
     * @return
     */
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    public Object reset(HttpServletRequest request, ReqAddScore req) throws Exception {
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
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, req.getGoogleAuthCode())) {
            rspBase.setMsg("google验证码不正确，请检查");
            rspBase.setCode(1);
            return rspBase;
        }
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId(req.getId());
        memberInfo.setPassword(req.getPassword());
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
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, req.getGoogleAuthCode())) {
            rspBase.setMsg("google验证码不正确，请检查");
            rspBase.setCode(1);
            return rspBase;
        }

        String ip = UserDataUtil.getIp(request);
        if (!redisUtil.lock(EnumLock.member, "addScore"+req.getId(), "1", 15)) {
            throw new BusinessException("请勿重复提交");
        }
        String username = loginUser.getUser().getUserName();
        rspBase = memberInfoService.addMemberMoneyOnly(ip, req.getId(), req.getScore(), req.getBeatNum(), req.getMk()+",操作人:"+username,
                req.getOrdermk(), loginUser.getUsername());
        if(rspBase.getCode() == 2){
            redisUtil.unLock(EnumLock.member, "addScore"+req.getId());
        }
        return rspBase;
    }

    /**
     * 发送短信
     *
     * @return
     */
    @ApiOperation(value = "会员发送短信", notes = "会员发送短信")
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @Log(title = "会员发送短信", businessType = BusinessType.UPDATE)
    public RspBase sendMsg(@RequestBody Map map) throws Exception {
        RspBase rspBase = new RspBase();
        String msg = (String)map.get("msg");
        String memberId = (String)map.get("memberId");
        if (StringUtils.isNotBlank(msg) && StringUtils.isNotBlank(memberId)) {
            sysUserService.sendMsg(msg,memberId);
            rspBase.setMsg("发送成功");
        }else {
            rspBase.setMsg("发送失败");
        }
        return rspBase;
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
        String memberId = (String)map.get("memberId");
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
        return sysUserService.updateMobile(newMobile,oldMobile,memberId);
    }


    /**
     * 查询资金明细列表
     *
     * @return
     */
    @ApiOperation(value = "查询资金明细列表", notes = "查询资金明细列表")
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public PageBO<WithdrawReport> findMemberCardList(@RequestParam("id") String memberId, PageVO req) {
        return memberInfoService.withdrawReport(memberId, req.getPage(), req.getLimit());
    }

    /**
     * 会员银行卡列表
     *
     * @return
     */
    @ApiOperation(value = "会员银行卡列表", notes = "会员银行卡列表")
    @RequestMapping(value = "/card-list", method = RequestMethod.GET)
    public PageBO<MemberCard> findMemberCardList(@RequestParam("id") String memberId, ReqMemberInfo req) {
        return memberInfoService.findMemberCardPage(memberId, req.getPage(), req.getLimit(), req.getOrderBy());
    }

    @ApiOperation(value = "重置保险箱账户", notes = "重置保险箱账户")
    @PostMapping("/resetPassword")
    public Object resetPassword(HttpServletRequest request,
                                @RequestParam(value = "userId") String userId) {
        RspBase rspBase = new RspBase();
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId(userId);
        memberInfo.setBoxPass("");
        memberInfoService.updateMemberInfo(memberInfo);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }

    @ApiOperation(value = "重置提现", notes = "重置提现")
    @PostMapping("/resettx")
    public Object resettx(HttpServletRequest request,
                          MemberInfo memberInfo) throws Exception {
        RspBase rspBase = new RspBase();
        if (memberInfo.getGoogleAuthCode() == null) {
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
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, memberInfo.getGoogleAuthCode())) {
            rspBase.setMsg("google验证码不正确，请检查");
            rspBase.setCode(1);
            return rspBase;
        }
        memberInfo.setId(memberInfo.getId());
        memberInfo.setWithdrawalPass("");
        memberInfoService.updateMemberInfo(memberInfo);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }
    @ApiOperation(value = "修復打碼", notes = "修復打碼")
    @PostMapping("/memberBcodeRepair")
    public Object memberBcodeRepair(HttpServletRequest request,
                          MemberInfo memberInfo) throws Exception {
        RspBase rspBase = new RspBase();
        if (memberInfo.getGoogleAuthCode() == null) {
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
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, memberInfo.getGoogleAuthCode())) {
            rspBase.setMsg("google验证码不正确，请检查");
            rspBase.setCode(1);
            return rspBase;
        }
        String memberId = memberInfo.getId();
        memberInfoService.repairMemberBcode(memberId);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }

    @ApiOperation(value = "修改vip等级", notes = "修改vip等级")
    @PostMapping("/updateVip")
    public Object updateVip(HttpServletRequest request,
                          MemberInfo memberInfo) throws Exception {
        RspBase rspBase = new RspBase();
        String memberId = memberInfo.getId();
        Integer vip = memberInfo.getVip();
        String nickName = memberInfo.getNickName();
        memberInfoService.updateVip(memberId,vip,nickName);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }
    @Log(title = "解绑银行卡", businessType = BusinessType.UPDATE)
    @PutMapping("/unbindCard")
    public Object unbindCard(@RequestBody MemberCard memberCard) {
        AjaxResult ajaxResult = memberInfoService.unbindCard(memberCard);
        return (ajaxResult);
    }
    @Log(title = "修改用户银行卡信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeBank")
    public Object changeBank(@RequestBody MemberCard memberCard) {
        AjaxResult ajaxResult = memberInfoService.changeBank(memberCard);
        return (ajaxResult);
    }

    @ApiOperation(value = "禁言用户IM", notes = "禁言用户IM")
    @PostMapping("/imDealBan")
    public Object imDealBan( MemberInfo memberInfo) {
        if (Objects.isNull(memberInfo.getBanSpeakTime())){
            return AjaxResult.success("禁言时间不能为空");
        }
        memberInfoService.updataStatus(memberInfo);
        if(imApi.nospeakingT(memberInfo.getId(),memberInfo.getBanSpeakTime())){
            log.info("IM禁言成功");
            return AjaxResult.success("IM禁言成功");
        }else{
            log.error("IM禁言失败");
            return AjaxResult.success("IM禁言失败");
        }
    }

}
