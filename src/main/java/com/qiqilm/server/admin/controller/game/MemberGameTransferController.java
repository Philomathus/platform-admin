package com.qiqilm.server.admin.controller.game;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberGameTransfer;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.service.IMemberGameTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员上下分Controller
 *
 * @author 77tv
 * @date 2021-08-04
 */
@RestController
@RequestMapping( "/member/memberGameTransfer" )
public class MemberGameTransferController extends BaseController {

	@Autowired
	private IMemberGameTransferService memberGameTransferService;
	@Autowired
	private IGamePlatformService gamePlatformService;

	/** 交易状态列表 **/
	@GetMapping( value = "/transferTypeList" )
	public AjaxResult transferTypeList(){
		return AjaxResult.success(defaultTransferType());
	}

	/** 交易类型列表 **/
	@GetMapping( value = "/transferStateList" )
	public AjaxResult transferStateList(){
		return AjaxResult.success(defaultTransferState());
	}

	/**
	 * 查询会员上分交易流水
	 */
	@PreAuthorize( "@ss.hasPermi('member:memberGameTransfer:list')" )
	@GetMapping( "/list" )
	public TableDataInfo list( MemberGameTransfer memberGameTransfer ) {
		startPage();
		List<MemberGameTransfer> list = memberGameTransferService.selectMemberGameTransferList( memberGameTransfer );
		return getDataTable( list );
	}


}
