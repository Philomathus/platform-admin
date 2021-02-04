package com.qiqilm.server.admin.payagent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PayAgentProcessorFactoryUtil {
	@Autowired
	private ApplicationContext context;

	public BasePayAgent createPayProcessor( String type ) {
		return ( BasePayAgent ) context.getBean( type + "PayAgentProcessor" );
	}
}
