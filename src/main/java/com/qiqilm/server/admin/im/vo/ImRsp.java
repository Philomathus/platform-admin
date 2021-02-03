package com.qiqilm.server.admin.im.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImRsp {
	@JsonProperty( "ActionStatus" )
	protected String  actionStatus;
	@JsonProperty( "ErrorInfo" )
	protected String  errorInfo;
	@JsonProperty( "ErrorCode" )
	protected Integer errorCode;

	public static ImRsp error( String msg ) {
		ImRsp rsp = new ImRsp();
		rsp.setActionStatus( "FAIL" );
		rsp.setErrorCode( -1 );
		rsp.setErrorInfo( msg );
		return rsp;
	}
}
