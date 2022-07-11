//package com.qiqilm.server.admin.controller;
//
//import java.util.List;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.qiqilm.server.admin.annotation.Log;
//import com.qiqilm.server.admin.core.controller.BaseController;
//import com.qiqilm.server.admin.core.vo.AjaxResult;
//import com.qiqilm.server.admin.enums.BusinessType;
//import com.qiqilm.server.admin.domain.MemberDepositLog;
//import com.qiqilm.server.admin.service.IMemberDepositLogService;
//import com.qiqilm.server.admin.utils.ExportExcelUtil;
//import com.qiqilm.server.admin.core.page.TableDataInfo;
//
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 人工加分日志Controller
// *
// * @author 77tv
// * @date 2021-07-29
// */
//@RestController
//@RequestMapping( "/pay/memberDepositLog" )
//public class MemberDepositLogController extends BaseController {
//	@Autowired
//	private IMemberDepositLogService memberDepositLogService;
//
//	/**
//	 * 查询人工加分日志列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:list')" )
//	@GetMapping( "/list" )
//    	public TableDataInfo list(MemberDepositLog memberDepositLog) {
//		startPage();
//		List<MemberDepositLog> list = memberDepositLogService.selectMemberDepositLogList(memberDepositLog);
//		return getDataTable( list );
//	}
//
//	/**
//	 * 导出人工加分日志列表
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:export')" )
//	@Log( title = "人工加分日志", businessType = BusinessType.EXPORT )
//	@GetMapping( "/export" )
//	public void export(MemberDepositLog memberDepositLog, HttpServletResponse response) {
//		List<MemberDepositLog>      list = memberDepositLogService.selectMemberDepositLogList(memberDepositLog);
//		ExportExcelUtil.exportExcel( list, "人工加分日志", "人工加分日志表", MemberDepositLog.class, response );
//	}
//
//	/**
//	 * 获取人工加分日志详细信息
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:query')" )
//	@GetMapping( value = "/{id}" )
//	public AjaxResult getInfo( @PathVariable( "id" ) Long id) {
//		return AjaxResult.success( memberDepositLogService.selectMemberDepositLogById(id) );
//	}
//
//	/**
//	 * 新增人工加分日志
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:add')" )
//	@Log( title = "人工加分日志", businessType = BusinessType.INSERT )
//	@PostMapping
//	public AjaxResult add( @RequestBody MemberDepositLog memberDepositLog) {
//		return toAjax( memberDepositLogService.insertMemberDepositLog(memberDepositLog) );
//	}
//
//	/**
//	 * 修改人工加分日志
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:edit')" )
//	@Log( title = "人工加分日志", businessType = BusinessType.UPDATE )
//	@PutMapping
//	public AjaxResult edit( @RequestBody MemberDepositLog memberDepositLog) {
//		return toAjax( memberDepositLogService.updateMemberDepositLog(memberDepositLog) );
//	}
//
//	/**
//	 * 删除人工加分日志
//	 */
//	@PreAuthorize( "@ss.hasPermi('pay:memberDepositLog:remove')" )
//	@Log( title = "人工加分日志", businessType = BusinessType.DELETE )
//	@DeleteMapping( "/{ids}" )
//	public AjaxResult remove( @PathVariable Long[] ids ) {
//		return toAjax( memberDepositLogService.deleteMemberDepositLogByIds( ids ) );
//	}
//}
