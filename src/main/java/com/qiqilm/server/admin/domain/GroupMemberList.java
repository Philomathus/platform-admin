package com.qiqilm.server.admin.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.domain.vo.PageVO;
import com.qiqilm.server.admin.im.BaseFuc;
import lombok.Data;

import java.util.Objects;

/**
 * @Author kehai
 * @Date 2020/7/10 10:51
 * @Version 1.0
 */
@Data
public class GroupMemberList implements BaseFuc {
    private String groupId;
    private PageVO pageVO;

    @Override
    public String getApi() {
        return "/group_open_http_svc/get_group_member_info";
    }

    @Override
    public ObjectNode get() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("GroupId",groupId);
        if(Objects.nonNull(pageVO)){
            node.put("Limit",pageVO.getLimit());
            if(Objects.isNull(pageVO.getPage()) || pageVO.getPage()<1)
                pageVO.setPage(1);
            node.put("Offset",pageVO.getLimit()*(pageVO.getPage()-1));
        }
        return node;
    }
}
