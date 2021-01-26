package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author 77tv
 * @date 2021-01-26
 */
@RestController
@RequestMapping( "/admin/member-game-data" )
public class MemberGameDataController extends BaseController {
	@Autowired
	private IMemberGameDataService memberGameDataService;

/**
 * 查询【请填写功能名称】列表
 */
@PreAuthorize( "@ss.hasPermi('admin:member-game-data:list')" )
@GetMapping( "/list" )
    	public TableDataInfo list(MemberGameData memberGameData) {
		startPage();
		List<MemberGameData> list = memberGameDataService.selectMemberGameDataList(memberGameData);
		return getDataTable( list );
	}


}