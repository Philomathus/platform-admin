package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.service.IMemberGameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}