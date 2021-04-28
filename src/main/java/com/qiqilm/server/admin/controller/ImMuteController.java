package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.cache.ServerImCacheUtil;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ImMute;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.im.ImApi;
import com.qiqilm.server.admin.im.vo.ForbidItem;
import com.qiqilm.server.admin.im.vo.UserForbid;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯IM禁言查询Controller
 *
 * @author 77tv
 * @date 2021-04-27
 */
@RestController
@RequestMapping("/live-web/ImMute")
public class ImMuteController extends BaseController {
    /*	@Autowired
        private IImMuteService imMuteService;*/
    @Autowired
    private ImApi imApi;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    @Autowired
    private ServerImCacheUtil serverImCacheUtil;

    /**
     * 查询腾讯IM禁言查询列表
     */
    @PreAuthorize("@ss.hasPermi('live-web:ImMute:list')")
    @GetMapping("/list")
    public AjaxResult list(ImMute imMute) {
        if (StringUtils.isEmpty(imMute.getUserId()) || StringUtils.isNotEmpty(imMute.getNickName())) {
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setNickName(imMute.getNickName());
            List<MemberInfo> memberInfos = memberInfoMapper.selectMemberInfoList(memberInfo);
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
            forbidItem.setShutTamp(-1);
        }else {
            if (userShutted.getGroupmsgNospeakingTime()==0) {
                forbidItem.setShuttedUnitl("0");
                forbidItem.setShutTamp(0);
            }else {
                forbidItem.setShuttedUnitl(System.currentTimeMillis()/1000 + userShutted.getGroupmsgNospeakingTime()+"");
                forbidItem.setShutTamp(userShutted.getGroupmsgNospeakingTime());
            }
        }
            forbidItem.setAccount(imMute.getUserId());
        if (StringUtils.isEmpty(imMute.getNickName())) {
            MemberInfo memberInfo = memberInfoMapper.selectMemberInfoById(imMute.getUserId());
            if (memberInfo!=null) {
                forbidItem.setNickName(memberInfo.getNickName());
            }
        }else {
            forbidItem.setNickName(imMute.getNickName());
        }
            forbidItems.add(forbidItem);

//        List<ImMute> list = imMuteService.selectImMuteList(imMute);
        return AjaxResult.success(forbidItems);
    }

    /*	*//**
     * 导出腾讯IM禁言查询列表
     *//*
	@PreAuthorize( "@ss.hasPermi('live-web:ImMute:export')" )
	@Log( title = "腾讯IM禁言查询", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(ImMute imMute, HttpServletResponse response) {
		List<ImMute>      list = imMuteService.selectImMuteList(imMute);
		ExportExcelUtil.exportExcel( list, "腾讯IM禁言查询", "腾讯IM禁言查询表", ImMute.class, response );
	}

	*//**
     * 获取腾讯IM禁言查询详细信息
     *//*
	@PreAuthorize( "@ss.hasPermi('live-web:ImMute:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( imMuteService.selectImMuteById(id) );
	}

	*//**
     * 新增腾讯IM禁言查询
     *//*
	@PreAuthorize( "@ss.hasPermi('live-web:ImMute:add')" )
	@Log( title = "腾讯IM禁言查询", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody ImMute imMute) {
		return toAjax( imMuteService.insertImMute(imMute) );
	}

	*//**
     * 修改腾讯IM禁言查询
     *//*
	@PreAuthorize( "@ss.hasPermi('live-web:ImMute:edit')" )
	@Log( title = "腾讯IM禁言查询", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody ImMute imMute) {
		return toAjax( imMuteService.updateImMute(imMute) );
	}

	*//**
     * 删除腾讯IM禁言查询
     *//*
	@PreAuthorize( "@ss.hasPermi('live-web:ImMute:remove')" )
	@Log( title = "腾讯IM禁言查询", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( imMuteService.deleteImMuteByIds( ids ) );
	}*/
}
