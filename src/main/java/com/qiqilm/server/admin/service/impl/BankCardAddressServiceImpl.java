package com.qiqilm.server.admin.service.impl;

import java.util.List;
import java.util.Objects;

import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.BankCardAddressMapper;
import com.qiqilm.server.admin.domain.BankCardAddress;
import com.qiqilm.server.admin.service.IBankCardAddressService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-04-21
 */
@Service
public class BankCardAddressServiceImpl implements IBankCardAddressService {
    @Autowired
    private BankCardAddressMapper bankCardAddressMapper;
    @Autowired
    private  TokenService tokenService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BankCardAddress selectBankCardAddressById(String id) {
        return bankCardAddressMapper.selectBankCardAddressById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param bankCardAddress 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BankCardAddress> selectBankCardAddressList(BankCardAddress bankCardAddress) {
        return bankCardAddressMapper.selectBankCardAddressList(bankCardAddress);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param bankCardAddress 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBankCardAddress(BankCardAddress bankCardAddress) {

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String userId = loginUser.getUser().getUserName();
        bankCardAddress.setCreateName(userId);
        bankCardAddress.setStatus("0");
        BankCardAddress bankCardAddress1=bankCardAddressMapper.selectBankCardAddress(bankCardAddress.getProvince());
        if (Objects.isNull(bankCardAddress1)){
            return bankCardAddressMapper.insertBankCardAddress(bankCardAddress);
        }else {
            bankCardAddress.setId(bankCardAddress1.getId());
          return   bankCardAddressMapper.updateBankCardAddress(bankCardAddress);
        }
    }


    /**
     * 修改【请填写功能名称】
     *
     * @param bankCardAddress 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBankCardAddress(BankCardAddress bankCardAddress) {
        return bankCardAddressMapper.updateBankCardAddress(bankCardAddress);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBankCardAddressByIds(String[] ids) {
        return bankCardAddressMapper.deleteBankCardAddressByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBankCardAddressById(String id) {
        return bankCardAddressMapper.deleteBankCardAddressById(id);
    }
}