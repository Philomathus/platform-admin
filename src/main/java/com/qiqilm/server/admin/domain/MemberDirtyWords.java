package com.qiqilm.server.admin.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class MemberDirtyWords {
	private Long id;
	private String name;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "id", getId() )
				.append( "name", getName() )
				.toString();
	}
}