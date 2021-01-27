package com.qiqilm.server.admin.domain;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 域名配置对象 config_domain
 *
 * @author 77tv
 * @date 2021-01-26
 */
public class ConfigDomain extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Long id;

	/** 域名 */
	private String domain;

	/** 动态编码 */
	private String dcode;

	/** 域名分组 */
	private Long dgroup;

	/** 排序 */
	private Long sort;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}
	public void setDcode(String dcode) {
		this.dcode = dcode;
	}

	public String getDcode() {
		return dcode;
	}
	public void setDgroup(Long dgroup) {
		this.dgroup = dgroup;
	}

	public Long getDgroup() {
		return dgroup;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}

	public Long getSort() {
		return sort;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("id", getId())
				.append("domain", getDomain())
				.append("dcode", getDcode())
				.append("dgroup", getDgroup())
				.append("remark", getRemark())
				.append("sort", getSort())
				.append("createBy", getCreateBy())
				.append("createTime", getCreateTime())
				.append("updateBy", getUpdateBy())
				.append("updateTime", getUpdateTime())
				.toString();
	}
}