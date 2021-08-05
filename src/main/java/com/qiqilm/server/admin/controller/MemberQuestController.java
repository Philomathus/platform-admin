package com.qiqilm.server.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.MemberQuest;
import com.qiqilm.server.admin.service.IMemberQuestService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 会员任务列表Controller
 *
 * @author 77tv
 * @date 2021-08-04
 */
@RestController
@RequestMapping( "/member/memberQuest" )
public class MemberQuestController extends BaseController {
	@Autowired
	private IMemberQuestService memberQuestService;

	/**
	 * 查询会员任务列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(MemberQuest memberQuest) {
		startPage();
		List<MemberQuest> list = memberQuestService.selectMemberQuestList(memberQuest);
		return getDataTable( list );
	}

	/**
	 * 增加会员积分
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PostMapping("/addScore")
	public AjaxResult addScore( @RequestBody MemberQuest memberQuest) {
		return toAjax( memberQuestService.addMemberScore(memberQuest) );
	}
    
	/**
	 * 导出会员任务列表
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:export')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(MemberQuest memberQuest, HttpServletResponse response) {
		List<MemberQuest>      list = memberQuestService.selectMemberQuestList(memberQuest);
		ExportExcelUtil.exportExcel( list, "【请填写功能名称】", "【请填写功能名称】表", MemberQuest.class, response );
	}

	/**
	 * 获取会员任务详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) String id) {
		return AjaxResult.success( memberQuestService.selectMemberQuestById(id) );
	}

	/**
	 * 新增会员任务
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:add')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody MemberQuest memberQuest) {
		return toAjax( memberQuestService.insertMemberQuest(memberQuest) );
	}

	/**
	 * 修改会员任务
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:edit')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody MemberQuest memberQuest) {
		return toAjax( memberQuestService.updateMemberQuest(memberQuest) );
	}

	/**
	 * 删除会员任务
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberQuest:remove')" )
	@Log( title = "【请填写功能名称】", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable String[] ids ) {
		return toAjax( memberQuestService.deleteMemberQuestByIds( ids ) );
	}
}
