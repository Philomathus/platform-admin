package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.BankList;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.IBankListService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 出款银行列表Controller
 *
 * @author 77tv
 * @date 2021-04-06
 */
@RestController
@RequestMapping( "/admin/bankList" )
public class BankListController extends BaseController {
	@Autowired
	private IBankListService bankListService;

	/**
	 * 查询出款银行列表列表
	 */
	//@PreAuthorize( "@ss.hasPermi('admin:bankList:list')" )
	@GetMapping( "/list" )
    	public TableDataInfo list(BankList bankList) {
		startPage();
		List<BankList> list = bankListService.selectBankListList(bankList);
		return getDataTable( list );
	}
    
	/**
	 * 导出出款银行列表列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:export')" )
	@Log( title = "出款银行列表", businessType = BusinessType.EXPORT )
	@GetMapping( "/export" )
	public void export(BankList bankList, HttpServletResponse response) {
		List<BankList>      list = bankListService.selectBankListList(bankList);
		ExportExcelUtil.exportExcel( list, "出款银行列表", "出款银行列表表", BankList.class, response );
	}

	/**
	 * 获取出款银行列表详细信息
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:query')" )
	@GetMapping( value = "/{id}" )
	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
		return AjaxResult.success( bankListService.selectBankListById(id) );
	}

	/**
	 * 出款银行状态修改
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:edit')" )
	@Log( title = "出款银行状态", businessType = BusinessType.UPDATE )
	@PutMapping( "/changeStatus" )
	public AjaxResult changeStatus( @RequestBody BankList bankList ) {
		return toAjax(bankListService.updateBankList(bankList));
	}

	/**
	 * 新增出款银行列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:add')" )
	@Log( title = "出款银行列表", businessType = BusinessType.INSERT )
	@PostMapping
	public AjaxResult add( @RequestBody BankList bankList) {
		bankList.setStatus("1");
		return toAjax( bankListService.insertBankList(bankList) );
	}

	/**
	 * 修改出款银行列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:edit')" )
	@Log( title = "出款银行列表", businessType = BusinessType.UPDATE )
	@PutMapping
	public AjaxResult edit( @RequestBody BankList bankList) {
		return toAjax( bankListService.updateBankList(bankList) );
	}

	/**
	 * 删除出款银行列表
	 */
	@PreAuthorize( "@ss.hasPermi('admin:bankList:remove')" )
	@Log( title = "出款银行列表", businessType = BusinessType.DELETE )
	@DeleteMapping( "/{ids}" )
	public AjaxResult remove( @PathVariable Long[] ids ) {
		return toAjax( bankListService.deleteBankListByIds( ids ) );
	}
}
