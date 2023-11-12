package com.qiqilm.server.admin.service;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.MemberPayJour;

import java.util.Map;

public interface IPayService {
	AjaxResult payPatchOrder( Map<String, Object> requestMap ) throws Exception;

    boolean updatePayJourStatus( MemberPayJour payJour, String opName );
}
