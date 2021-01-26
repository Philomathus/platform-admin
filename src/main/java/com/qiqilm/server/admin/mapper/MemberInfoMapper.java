package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.MemberInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author 77tv
 * @date 2021-01-25
 */
public interface MemberInfoMapper {
	/**
	 * 查询【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 【请填写功能名称】
	 */
	public MemberInfo selectMemberInfoById(String id);

	/**
	 * 查询【请填写功能名称】列表
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 【请填写功能名称】集合
	 */
	public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

	/**
	 * 新增【请填写功能名称】
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int insertMemberInfo(MemberInfo memberInfo);

	/**
	 * 修改【请填写功能名称】
	 *
	 * @param memberInfo 【请填写功能名称】
	 * @return 结果
	 */
	public int updateMemberInfo(MemberInfo memberInfo);

	/**
	 * 删除【请填写功能名称】
	 *
	 * @param id 【请填写功能名称】ID
	 * @return 结果
	 */
	public int deleteMemberInfoById(String id);

	/**
	 * 批量删除【请填写功能名称】
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteMemberInfoByIds(String[] ids );

    int selectMaxMemberCode();

    int updateMoneySelect(@Param("userId") String userId, @Param("money") BigDecimal money, @Param("invite_money") BigDecimal invite_money, @Param("level_integral") BigDecimal level_integral, @Param("code_account") BigDecimal code_account, @Param("code_total") BigDecimal code_total);
}
