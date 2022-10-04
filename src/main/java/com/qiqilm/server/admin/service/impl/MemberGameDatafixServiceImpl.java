package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.domain.MemberGameDatafix;
import com.qiqilm.server.admin.domain.MemberGameMoney;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.MemberGameDatafixMapper;
import com.qiqilm.server.admin.mapper.MemberGameMoneyMapper;
import com.qiqilm.server.admin.service.IMemberGameDatafixService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Service
public class MemberGameDatafixServiceImpl implements IMemberGameDatafixService {
    @Resource
    private MemberGameDatafixMapper memberGameDatafixMapper;

    @Resource
    private MemberGameMoneyMapper memberGameMoneyMapper;


    /**
     * 查询【请填写功能名称】列表
     *
     * @param memberGameDatafix 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MemberGameDatafix> selectMemberGameDatafixList(MemberGameDatafix memberGameDatafix) {
        return memberGameDatafixMapper.selectMemberGameDatafixList(memberGameDatafix);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param memberGameDatafix 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertMemberGameDatafix(MemberGameDatafix memberGameDatafix) {
        return memberGameDatafixMapper.insertMemberGameDatafix(memberGameDatafix);
    }

    @Override
    public int deleteMemberGameDatafixByIds(String[] ids) {
        return memberGameDatafixMapper.deleteMemberGameDatafixByIds(ids);
    }

    @Override
    public int deleteMemberGameDatafixById(String id) {
        return memberGameDatafixMapper.deleteMemberGameDatafixById(id);
    }

    @Override
    public MemberGameDatafix selectMemberGameDatafixById(String id) {
        return memberGameDatafixMapper.selectMemberGameDatafixById(id);
    }

    /**
     * add member game data money service implementation layer
     */
    @Override
    public int insertMemberGameMoney(MemberGameMoney memberGameMoney) {
        memberGameMoney.setCtime(new Date());
        memberGameMoney.setStatus(0);
        memberGameMoney.setOderSn(null);
        memberGameMoney.setMoney(BigDecimal.valueOf(0.0d));

        if (memberGameMoney.getMemberId() != null && memberGameMoney.getMemberId() != null) {
            String customId = memberGameMoney.getMemberId()+ "_" + memberGameMoney.getPlatformId();
            memberGameMoney.setId(customId);
            System.out.println(memberGameMoney.getId());
//             add member game data money service implementation layer
            int countId = memberGameMoneyMapper.countMemberGameMoneyId(memberGameMoney.getId());
            if (countId > 0) {
                throw new BusinessException("ID已存在于系统中!");
            }
        }
        return memberGameMoneyMapper.insertMemberGameMoney(memberGameMoney);
    }


}