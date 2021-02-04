package com.qiqilm.server.admin.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.exception.BaseException;

import java.util.Objects;

public enum MessageType {
	//
	TIMTextElem( "TIMTextElem" ),  //文本
	TIMLocationElem( "TIMLocationElem" ),  //位置
	TIMFaceElem( "TIMFaceElem" ), //表情
	TIMCustomElem( "TIMCustomElem" ),//自定义
	TIMSoundElem( "TIMSoundElem" ), //语言
	TIMImageElem( "TIMImageElem" ),//图像
	TIMFileElem( "TIMFileElem" ),//文件
	TIMVideoFileElem( "TIMVideoFileElem" ) //视频
	;

	private String   val;
	private Object[] data;


	MessageType( String str ) {
		val = str;
	}

	@Override
	public String toString() {
		return val;
	}

	public MessageType setData( Object... args ) {
		if ( args.length < 4 ) {
			data = new Object[ 4 ];
			System.arraycopy( args, 0, data, 0, args.length );
			for ( int i = args.length; i < 4; i++ ) {
				data[ i ] = "";
			}
		} else {
			data = args;
		}
		return this;
	}

	public ObjectNode getNode() {
		if ( Objects.isNull( data ) ) {
			throw new BaseException( "获取节点失败" );
		}
		return ofNode( data );
	}

	public ObjectNode ofNode( Object... args ) {
		if ( Objects.isNull( data ) ) {
			setData( args );
		}
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode   node   = mapper.createObjectNode();
		node.put( "MsgType", val );
		final ObjectNode content = mapper.createObjectNode();

		try {
			switch ( val ) {
			case "TIMTextElem":
				content.put( "Text", data[ 0 ].toString() );
				break;
			case "TIMLocationElem":
				content.put( "Desc", data[ 0 ].toString() );
				content.put( "Latitude", Double.valueOf( data[ 1 ].toString() ) );
				content.put( "Longitude", Double.valueOf( data[ 2 ].toString() ) );
				break;
			case "TIMFaceElem":
				content.put( "Index", Integer.valueOf( data[ 0 ].toString() ) );
				content.put( "Data", data[ 1 ].toString() );
				break;
			case "TIMCustomElem":
				content.put( "Data", data[ 0 ].toString() );
				content.put( "Desc", data[ 1 ].toString() );
				content.put( "Ext", data[ 2 ].toString() );
				content.put( "Sound", data[ 3 ].toString() );
				break;
			default:
				return null;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new BaseException( e.getMessage() );
		}

		node.put( "MsgContent", content );
		return node;
	}
}
