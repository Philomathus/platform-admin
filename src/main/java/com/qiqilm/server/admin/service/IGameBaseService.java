package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;

public interface IGameBaseService {
	AjaxResult balance( String userId );

	AjaxResult esc( String userId, Integer platformId );
}
