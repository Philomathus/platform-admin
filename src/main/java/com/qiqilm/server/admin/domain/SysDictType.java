package com.qiqilm.server.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 字典类型表 sys_n_dict_type
 *
 * @author 77tv
 */
@Data
public class SysDictType extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 字典主键
	 */
	@Excel( name = "字典主键" )
	private Long dictId;

	/**
	 * 字典名称
	 */
	@Excel( name = "字典名称" )
	@NotBlank( message = "字典名称不能为空" )
	@Size( min = 0, max = 100, message = "字典类型名称长度不能超过100个字符" )
	private String dictName;

	/**
	 * 字典类型
	 */
	@Excel( name = "字典类型" )
	@NotBlank( message = "字典类型不能为空" )
	@Size( min = 0, max = 100, message = "字典类型类型长度不能超过100个字符" )
	private String dictType;

	/**
	 * 状态（0正常 1停用）
	 */
	@Excel( name = "状态" )
	private String status;

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE )
				.append( "dictId", getDictId() )
				.append( "dictName", getDictName() )
				.append( "dictType", getDictType() )
				.append( "status", getStatus() )
				.append( "createBy", getCreateBy() )
				.append( "createTime", getCreateTime() )
				.append( "updateBy", getUpdateBy() )
				.append( "updateTime", getUpdateTime() )
				.append( "remark", getRemark() )
				.toString();
	}
}
