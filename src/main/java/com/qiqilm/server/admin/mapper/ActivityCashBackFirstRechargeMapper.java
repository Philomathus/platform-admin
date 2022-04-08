package com.qiqilm.server.admin.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ActivityCashBackFirstRechargeMapper {
    BigDecimal selectByRechargeMoney(@Param("rechargeMoney") BigDecimal rechargeMoney);
}
