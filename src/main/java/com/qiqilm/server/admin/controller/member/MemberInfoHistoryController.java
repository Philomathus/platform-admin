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
import com.qiqilm.server.admin.domain.ImMute;
import com.qiqilm.server.admin.domain.MemberCard;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.req.DownLoadTime;
import com.qiqilm.server.admin.domain.req.ReqSmallFeatures;
import com.qiqilm.server.admin.domain.vo.*;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.vo.ForbidItem;
import com.qiqilm.server.admin.im.vo.UserForbid;
import com.qiqilm.server.admin.mapper.MemberInfoHistoryMapper;
import com.qiqilm.server.admin.service.IMemberInfoHistoryService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
@RequestMapping("/member/memberInfoHistory")
@Log4j2
public class MemberInfoHistoryController extends BaseController {
    @Autowired
    private IMemberInfoHistoryService memberInfoHistoryService;
    @Autowired
    private MemberInfoHistoryMapper memberInfoHistoryMapper;
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
        List<MemberInfo> list = memberInfoHistoryService.selectMemberInfoList(memberInfo);
        return getDataTable(list);
    }

    /**
     * 导出用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:export')")
    @Log(title = "导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MemberInfo memberInfo, HttpServletResponse response) {
        List<MemberInfo> list = memberInfoHistoryService.selectMemberInfoList(memberInfo);
        if (list.size()<= DownLoadTime.downLoadLimit) {
            ExportExcelUtil.exportExcel( list, "用户信息", "用户信息表", MemberInfo.class, response );
            return AjaxResult.success("下载成功");
        }else {
            return AjaxResult.error("导出条数超过20万条");
        }
    }

    /**
     * 获取用户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoHistoryService.selectMemberInfoById(id));
    }

    /**
     * 获取完整手机号
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:fullMobile')")
    @GetMapping(value = "/fullMobile/{id}")
    public AjaxResult fullMobile(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoHistoryService.selectMemberInfoById(id));
    }

    /**
     * 手机号批量更新密码
     */
    @PostMapping(value = "/memberSmallFeatures")
    public Object memberSmallFeatures(HttpServletRequest request,ReqSmallFeatures req) throws Exception {
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
        return AjaxResult.success(memberInfoHistoryService.updatePhones(req));
    }

    /**
     * 获取用户登录地址
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:query')")
    @GetMapping(value = "/getMemberLoginAddress/{id}")
    public AjaxResult getMemberLoginAddress(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoHistoryService.getMemberLoginAddress(id));
    }

    /**
     * 获取用户线上充值历史金额
     */
    @PreAuthorize("@ss.hasPermi('member:memberInfo:query')")
    @GetMapping(value = "/getHistoryRecharge/{id}")
    public AjaxResult getHistoryRecharge(@PathVariable("id") String id) {
        return AjaxResult.success(memberInfoHistoryService.getHistoryRecharge(id));
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
        return memberInfoHistoryService.insertMemberInfo(memberInfo);
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
        return toAjax(memberInfoHistoryService.updateMemberInfo(memberInfo));
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
        return toAjax(memberInfoHistoryService.changeSpeak(memberInfo));
    }

    @PreAuthorize("@ss.hasPermi('member:memberInfo:changeStatus')")
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE)
    @PutMapping("/change-status")
    public Object changeStatus(ReqMemberInfo req) {
        RspBase rspBase = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo = memberInfoHistoryService.selectMemberInfoById(req.getId());
        newMemberInfo.setStatus(req.getStatus());
        memberForbidUtil.setPlatformUserStatus(memberInfo.getId(),req.getStatus());

        if (1 == req.getStatus()) {
            newMemberInfo.setLoginNum(0);
        }
        newMemberInfo.setId(memberInfo.getId());
        memberInfoHistoryService.updateMemberInfo(newMemberInfo);
        if (req.getStatus() == 0) {
            memberCacheManager.delToken(memberInfo.getId());
        }
        return rspBase;
    }

    @PreAuthorize("@ss.hasPermi('member:memberInfo:changeStatus')")
    @Log(title = "修改用户状态", businessType = BusinessType.UPDATE)
    @PutMapping("/change-statusBan")
    public Object changeStatusBan(@RequestBody ReqMemberInfo req) {
        RspBase rspBase = new RspBase();
        MemberInfo newMemberInfo = new MemberInfo();
        MemberInfo memberInfo = memberInfoHistoryService.selectMemberInfoById(req.getId());
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
        memberInfoHistoryService.updateMemberInfo(newMemberInfo);
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
        memberInfoHistoryService.updateMemberInfo(memberInfo);
        return new RspBase();
    }

    /**
     * 加分
     *
     * @return
     */

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
            rspBase.setMsg("请勿重复提交");
            rspBase.setCode(1);
            return rspBase;
        }
        String username = loginUser.getUser().getUserName();
        rspBase = memberInfoHistoryService.addMemberMoneyOnly(ip, req.getId(), req.getScore(), req.getBeatNum(), req.getMk()+",操作人:"+username,
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
     * 修改邀请码
     *
     * @return
     */

    @RequestMapping(value = "/updateInviterCode", method = RequestMethod.POST)
    @Log(title = "会员修改邀请码", businessType = BusinessType.UPDATE)
    public AjaxResult updateInviterCode(@RequestBody Map map) throws Exception {
        String memberId = (String)map.getOrDefault("memberId", "");
        String inviterCode = (String)map.getOrDefault("inviterCode", "");
        String googleAuthCode = (String)map.getOrDefault("googleAuthCode", "");
        if (StringUtils.isEmpty(inviterCode)) {
            return AjaxResult.error("邀请不能为空");
        }
        if (StringUtils.isEmpty(googleAuthCode)) {
            return AjaxResult.error("谷歌验证码不能为空");
        }

        AjaxResult x = checkGoogle(googleAuthCode);
        if (x != null) return x;
        return memberInfoHistoryService.updateInviterCode(inviterCode,memberId);
    }

    private AjaxResult checkGoogle(String googleAuthCode) throws Exception {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = sysUserService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

        if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
            return AjaxResult.error("未绑定google验证秘钥，无法审核");
        }
        if (googleAuthSecret.length() == 32) {
            return AjaxResult.error("google验证秘钥未加密，请重新登录");
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, Integer.parseInt(googleAuthCode))) {
            return AjaxResult.error("google验证码不正确，请检查");
        }
        return null;
    }


    /**
     * 发送短信
     *
     * @return
     */

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

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public PageBO<WithdrawReport> findMemberCardList(@RequestParam("id") String memberId, PageVO req) {
        return memberInfoHistoryService.withdrawReport(memberId, req.getPage(), req.getLimit());
    }

    /**
     * 会员银行卡列表
     *
     * @return
     */

    @RequestMapping(value = "/card-list", method = RequestMethod.GET)
    public PageBO<MemberCard> findMemberCardList(@RequestParam("id") String memberId, ReqMemberInfo req) {
        return memberInfoHistoryService.findMemberCardPage(memberId, req.getPage(), req.getLimit(), req.getOrderBy());
    }

    @PostMapping("/resetPassword")
    public Object resetPassword(HttpServletRequest request,
                                @RequestParam(value = "userId") String userId) {
        RspBase rspBase = new RspBase();
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setId(userId);
        memberInfo.setBoxPass("");
        memberInfoHistoryService.updateMemberInfo(memberInfo);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }

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
        memberInfoHistoryService.updateMemberInfo(memberInfo);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }

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
        memberInfoHistoryService.repairMemberBcode(memberId);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }

    @PostMapping("/updateVip")
    public Object updateVip(HttpServletRequest request,
                            MemberInfo memberInfo) throws Exception {
        RspBase rspBase = new RspBase();
        String memberId = memberInfo.getId();
        Integer vip = memberInfo.getVip();
        String nickName = memberInfo.getNickName();
        memberInfoHistoryService.updateVip(memberId,vip,nickName);
        rspBase.setCode(Constants.URC_SUCCESS);
        rspBase.setData("成功");
        return rspBase;
    }
    @Log(title = "解绑银行卡", businessType = BusinessType.UPDATE)
    @PutMapping("/unbindCard")
    public Object unbindCard(@RequestBody MemberCard memberCard) {
        AjaxResult ajaxResult = memberInfoHistoryService.unbindCard(memberCard);
        return (ajaxResult);
    }
    @Log(title = "修改用户银行卡信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeBank")
    public Object changeBank(@RequestBody MemberCard memberCard) {
        AjaxResult ajaxResult = memberInfoHistoryService.changeBank(memberCard);
        return (ajaxResult);
    }

    @Log(title = "修改用户备注", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public Object updateEmail(@RequestBody MemberInfo memberInfo) {
        AjaxResult ajaxResult = memberInfoHistoryService.changeEmail(memberInfo);
        return (ajaxResult);
    }

    @PostMapping("/imDealBan")
    public Object imDealBan( MemberInfo memberInfo) {
        if (Objects.isNull(memberInfo.getBanSpeakTime())){
            return AjaxResult.success("禁言时间不能为空");
        }
        //im禁言备注
        if(StringUtils.isNotBlank(memberInfo.getEmail())){
            memberInfoHistoryService.updateMemberInfo(memberInfo);
        }
        memberInfoHistoryService.updataStatus(memberInfo);
        if(imApi.nospeakingT(memberInfo.getId(),memberInfo.getBanSpeakTime())){
            log.info("IM禁言成功");
            return AjaxResult.success("IM禁言成功");
        }
        return AjaxResult.success("正在禁言中");
    }

    /**
     * 查询腾讯IM禁言查询列表
     */
    //@PreAuthorize("@ss.hasPermi('live-web:ImMute:list')")
    @GetMapping("/imList")
    public AjaxResult list(ImMute imMute) {
        if (com.qiqilm.server.admin.utils.StringUtils.isEmpty(imMute.getUserId()) || com.qiqilm.server.admin.utils.StringUtils.isNotEmpty(imMute.getNickName())) {
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setNickName(imMute.getNickName());
            List<MemberInfo> memberInfos = memberInfoHistoryMapper.selectMemberInfoList(memberInfo);
            if (memberInfos.isEmpty()) {
                return AjaxResult.error("会员昵称不存在");
            } else {
                imMute.setUserId(memberInfos.get(0).getId());
            }
        }

        List<ForbidItem> forbidItems = new ArrayList<>();
/*            //查询主播所在的直播间
            List<String> strings = imApi.allGroup(imMute.getUserId());
            for (String string : strings) {
                forbidItems.addAll(imApi.getShutted(string).getShuttedUin());
            }*/
        //查询在线群
/*        String on_line_group_id = serverImCacheUtil.getValue("on_line_group_id");
        forbidItems.addAll(imApi.getShutted(on_line_group_id).getShuttedUin());
        //当查询某个用户时
        if (StringUtils.isNotEmpty(imMute.getUserId())) {
            List<ForbidItem> forbidItems2 = new ArrayList<>();
            for (ForbidItem forbidItem : forbidItems) {
                if (forbidItem.getAccount().equals(imMute.getUserId())) {
                    forbidItems2.add(forbidItem);
                }
            }
            forbidItems = forbidItems2;
        }*/

        //查某个用户
        UserForbid userShutted = imApi.getUserShutted(imMute.getUserId());
        ForbidItem forbidItem = new ForbidItem();
        if (userShutted==null) {
            forbidItem.setShuttedUnitl("-1");
            forbidItem.setShutTamp("-1");
        }else {
            if (userShutted.getGroupmsgNospeakingTime()==0) {
                forbidItem.setShuttedUnitl("0");
                forbidItem.setShutTamp("0");
            }else {
                forbidItem.setShuttedUnitl(System.currentTimeMillis()/1000 + userShutted.getGroupmsgNospeakingTime()+"");
                forbidItem.setShutTamp(userShutted.getGroupmsgNospeakingTime().toString());

            }
        }
        forbidItem.setAccount(imMute.getUserId());
        if (com.qiqilm.server.admin.utils.StringUtils.isEmpty(imMute.getNickName())) {
            MemberInfo memberInfo = memberInfoHistoryMapper.selectMemberInfoById(imMute.getUserId());
            if (memberInfo!=null) {
                forbidItem.setNickName(memberInfo.getNickName());
                if(com.qiqilm.server.admin.utils.StringUtils.isNotBlank(memberInfo.getEmail())) {
                    forbidItem.setMuteRemark(memberInfo.getEmail());
                }
            }
        }else {
            forbidItem.setNickName(imMute.getNickName());
        }
        forbidItems.add(forbidItem);

//        List<ImMute> list = imMuteService.selectImMuteList(imMute);
        return AjaxResult.success(forbidItems);
    }

    /**
     * 获取会员提现信息详细信息
     */
    @PreAuthorize( "@ss.hasPermi('pay:memberWithdrawLog:query')" )
    @GetMapping( value = "/report/{id}" )
    public AjaxResult getReport(@PathVariable( "id" ) String id ) {
        return memberInfoHistoryService.withdrawReport( id );
    }

}
