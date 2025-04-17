package com.qiqilm.server.admin.im.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RspGiftAndBalance {
    BigDecimal gift;
    BigDecimal userBalance;
}
