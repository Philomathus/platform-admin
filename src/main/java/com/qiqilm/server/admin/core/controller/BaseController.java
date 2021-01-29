package com.qiqilm.server.admin.core.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiqilm.server.admin.constant.HttpStatus;
import com.qiqilm.server.admin.core.page.PageDomain;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.page.TableSupport;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author 77tv
 */
@Log4j2
public class BaseController {

	/**
	 * 将前台传递过来的日期格式的字符串，自动转化为Date类型
	 */
	@InitBinder
	public void initBinder( WebDataBinder binder ) {
		// Date 类型转换
		binder.registerCustomEditor( Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText( String text ) {
				if(text.indexOf( " " ) > 0){
					setValue( DateFormatUtils.parse( text ) );
				}else{
					setValue( DateFormatUtils.parse( text,DateFormatUtils.SPLIT_PATTERN_DATE ) );
				}

			}
		} );
	}

	/**
	 * 设置请求分页数据
	 */
	protected void startPage() {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer    pageNum    = pageDomain.getPageNum();
		Integer    pageSize   = pageDomain.getPageSize();
		if ( StringUtils.isNotNull( pageNum ) && StringUtils.isNotNull( pageSize ) ) {
			String orderBy = SqlUtil.escapeOrderBySql( pageDomain.getOrderBy() );
			PageHelper.startPage( pageNum, pageSize, orderBy );
		}
	}

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    protected TableDataInfo getDataTable( List<?> list ) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode( HttpStatus.SUCCESS );
        rspData.setMsg( "查询成功" );
        rspData.setRows( list );
        rspData.setTotal( new PageInfo( list ).getTotal() );
        return rspData;
    }

	/**
	 * 响应请求分页数据
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected TableDataInfo getDataTable2( List<?> list ) {
		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode( HttpStatus.SUCCESS );
		rspData.setMsg( "查询成功" );
        String pageNum = ServletUtil.getParameter("pageNum");
        String pageSize = ServletUtil.getParameter("pageSize");
		rspData.setRows( PageUtil.pageBySubList(list, Integer.parseInt(pageSize), Integer.parseInt(pageNum)) );
		rspData.setTotal( new PageInfo( list ).getTotal() );
		return rspData;
	}

	/**
	 * 响应返回结果
	 *
	 * @param rows 影响行数
	 * @return 操作结果
	 */
	protected AjaxResult toAjax( int rows ) {
		return rows > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	/**
	 * 页面跳转
	 */
	public String redirect( String url ) {
		return StringUtils.format( "redirect:{}", url );
	}
}
