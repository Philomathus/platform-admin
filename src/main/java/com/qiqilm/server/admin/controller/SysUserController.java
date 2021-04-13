package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.constant.UserConstants;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.core.vo.RspBase;
import com.qiqilm.server.admin.domain.PayAgentRechargeAccount;
import com.qiqilm.server.admin.domain.SysRole;
import com.qiqilm.server.admin.domain.SysUser;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.mapper.PayAgentRechargeAccountMapper;
import com.qiqilm.server.admin.mapper.SysUserMapper;
import com.qiqilm.server.admin.service.ISysRoleService;
import com.qiqilm.server.admin.service.ISysUserService;
import com.qiqilm.server.admin.service.impl.TokenService;
import com.qiqilm.server.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author 77tv
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PayAgentRechargeAccountMapper payAgentRechargeAccountMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    public void export(SysUser user, HttpServletResponse response) {
        List<SysUser> list = userService.selectUserList(user);
        ExportExcelUtil.exportExcel(list, "用户信息", "用户信息表", SysUser.class, response);
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        //		ExcelUtil<SysUser> util      = new ExcelUtil<>( SysUser.class );
        //		List<SysUser>      userList  = util.importExcel( file.getInputStream() );
        //		LoginUser          loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        //		String             operName  = loginUser.getUsername();
        //		String             message   = userService.importUser( userList, updateSupport, operName );
        return AjaxResult.success();
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles :
                roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        if (StringUtils.isNotNull(userId)) {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 获取谷歌验证码二维码
     */
    @GetMapping("getGoogleAuth")
    public AjaxResult getGoogleAuth(String name, Integer type) {
        if (type == null) {
            String secretKey = GoogleAuthUtil.createSecretKey();
            String qrBarcodeURL = GoogleAuthUtil.getQRBarcodeURL(name, "77管理后台", secretKey);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("secretKey", secretKey);
            resultMap.put("qrBarcodeBase", GoogleAuthUtil.tranUrlToBase64String(qrBarcodeURL));
            return AjaxResult.success(resultMap);
        } else if (type == 1) {
            String secretKey = GoogleAuthUtil.createSecretKey();
            String qrBarcodeURL = GoogleAuthUtil.getQRBarcodeURL(name, "77代付", secretKey);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("secretKey", secretKey);
            resultMap.put("qrBarcodeBase", GoogleAuthUtil.tranUrlToBase64String(qrBarcodeURL));
            return AjaxResult.success(resultMap);
        }
        return AjaxResult.success("ok");
    }

    /**
     * 重置代充人谷歌秘钥
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentRechargeAccount:reset')")
    @GetMapping("updateGoogleAuth")
    @Log(title = "重置代充人谷歌秘钥", businessType = BusinessType.UPDATE)
    public AjaxResult updateGoogleAuth(Long id, int googleAuthCode) throws Exception{
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = userService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

        if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
            return AjaxResult.error(0,"未绑定google验证秘钥，无法审核");
        }
        if (googleAuthSecret.length() == 32) {
            return AjaxResult.error(0,"google验证秘钥未加密，请重新登录");
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, googleAuthCode)) {
            return AjaxResult.error("google验证码不正确，请检查");
        }
        PayAgentRechargeAccount payAgentRechargeAccount = payAgentRechargeAccountMapper.selectPayAgentRechargeAccountById(id);
        payAgentRechargeAccount.setGoogleAuthSecret(null);
        payAgentRechargeAccountMapper.updateGoogle(payAgentRechargeAccount);
        return AjaxResult.success();
    }

    /**
     * 重置用户谷歌秘钥
     */
    @PreAuthorize("@ss.hasPermi('system:user:reset')")
    @GetMapping("updateUserGoogleAuth")
    @Log(title = "重置用户谷歌秘钥", businessType = BusinessType.UPDATE)
    public AjaxResult updateUserGoogleAuth(Long id,int googleAuthCode) throws Exception{
        LoginUser loginUser = tokenService.getLoginUser(ServletUtil.getHttpServletRequest());
        String googleAuthSecret = userService.selectGoogleAuthKeyByUserName(loginUser.getUsername());

        if (!org.springframework.util.StringUtils.hasText(googleAuthSecret)) {
            return AjaxResult.error(0,"未绑定google验证秘钥，无法审核");
        }
        if (googleAuthSecret.length() == 32) {
            return AjaxResult.error(0,"google验证秘钥未加密，请重新登录");
        }
        String googleAuthKey = RSACoder.decryptByPrivateKey(googleAuthSecret, AuthUtil.getSecurityKeyStr("secretkey" +
                "/googleAuthPrivateKey"));

        if (!GoogleAuthUtil.verifyCode(googleAuthKey, googleAuthCode)) {
            return AjaxResult.error("google验证码不正确，请检查");
        }
        SysUser sysUser = userService.selectUserById(id);
        sysUser.setGoogleAuthSecret(null);
        sysUserMapper.updateGoogle(sysUser);
        return AjaxResult.success();
    }

    /**
     * 绑定谷歌验证码
     */
    @PostMapping("bindGoogleAuth")
    public AjaxResult getGoogleAuth(@RequestBody Map<String, Object> requestMap) throws Exception {
        int googleAuthCode = Integer.parseInt(requestMap.getOrDefault("googleAuthCode", 0).toString());
        String googleAuthKey = requestMap.getOrDefault("googleAuthKey", "").toString();
        String googleAuthName = requestMap.getOrDefault("googleAuthName", "").toString();
        int type = Integer.parseInt(requestMap.getOrDefault("type", 0).toString());
        //正常用户
        if (type == 0) {
            if (GoogleAuthUtil.verifyCode(googleAuthKey, googleAuthCode)) {
                SysUser sysUser = userService.selectUserByUserName(googleAuthName);
                //当用户是重置谷歌
                if (StringUtils.isNotBlank(sysUser.getGoogleAuthSecret())) {
                    return AjaxResult.error("该账户已绑定谷歌验证码，请勿重复绑定");
                }
                sysUser.setGoogleAuthSecret(RSACoder.encryptByPublicKey(googleAuthKey, AuthUtil.getSecurityKeyStr(
                        "secretkey/googleAuthPublicKey")));
                userService.updateUser2(sysUser);
                return AjaxResult.success();
            }
        } else if (type == 1) {
            //重置代充的
            PayAgentRechargeAccount payAgentRechargeAccount = payAgentRechargeAccountMapper.selectPayAgentRechargeAccountById(Long.parseLong(googleAuthName));
            payAgentRechargeAccount.setGoogleAuthSecret(RSACoder.encryptByPublicKey(googleAuthKey, AuthUtil.getSecurityKeyStr(
                    "secretkey/googleAuthPublicKeyAgent")));
            payAgentRechargeAccountMapper.updatePayAgentRechargeAccount(payAgentRechargeAccount);
            return AjaxResult.success();
        }

        return AjaxResult.error("验证码校验失败，请重新绑定");
    }
}
