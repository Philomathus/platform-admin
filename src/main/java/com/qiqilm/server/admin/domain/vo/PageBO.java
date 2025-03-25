package com.qiqilm.server.admin.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by admin on 18/4/12.
 */
@Data
public class PageBO<T> {

    Integer code = 0;

    List<T> data;

    Long count;
}
