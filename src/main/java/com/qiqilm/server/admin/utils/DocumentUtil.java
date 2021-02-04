package com.qiqilm.server.admin.utils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class DocumentUtil {

	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

	public static Document getXml( String text ) throws Exception {
		InputSource src = new InputSource();
		src.setCharacterStream( new StringReader( text ) );
		return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse( src );
	}

}
