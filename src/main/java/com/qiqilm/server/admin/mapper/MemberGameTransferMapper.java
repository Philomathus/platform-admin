package com.qiqilm.server.admin.mapper;


import com.qiqilm.server.admin.domain.MemberGameTransfer;

import java.util.List;

/**
 * Created by admin
 */
public interface MemberGameTransferMapper{

    List<MemberGameTransfer> selectMemberGameTransferList(MemberGameTransfer memberGameTransfer);

    MemberGameTransfer selectMemberGameTransferById(String id);

    int insertMemberGameTransfer(MemberGameTransfer memberGameTransfer);

    int updateMemberGameTransfer(MemberGameTransfer memberGameTransfer);

    int deleteMemberGameTransferByIds(String[] ids);

    int deleteMemberGameTransferById(String id);
}
