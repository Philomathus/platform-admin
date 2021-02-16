package com.qiqilm.server.admin.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static List<?> pageBySubList( List<?> list, int pagesize, int currentPage ) {
		int     totalcount = list.size();
		int     pagecount  = 0;
		List<?> subList    = new ArrayList<>();
		int     m          = totalcount % pagesize;
		if ( m > 0 ) {
			pagecount = totalcount / pagesize + 1;
		} else {
			pagecount = totalcount / pagesize;
		}
		if ( ( ( currentPage - 1 ) * pagesize ) >= totalcount ) {
			return subList;
		}
		int toIndex = pagesize * ( currentPage ) > totalcount ? totalcount - 1 : pagesize * ( currentPage );
		if ( m == 0 ) {
			subList = list.subList( ( currentPage - 1 ) * pagesize, toIndex );
		} else {
			if ( currentPage == pagecount ) {
				subList = list.subList( ( currentPage - 1 ) * pagesize, totalcount );
			} else {
				subList = list.subList( ( currentPage - 1 ) * pagesize, toIndex );
			}
		}
		return subList;
	}

}
