package com.qiqilm.server.admin.domain.rsp;

import com.qiqilm.server.admin.annotation.Excel;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class RspPayJour {
	private String id;

	@Excel(name = "订单号")
	private String order_no;

	private String payment_code;

	@Excel(name = "请求金额")
	private BigDecimal money;

	@Excel(name = "实际金额")
	private BigDecimal sub_money;

	private BigDecimal current_success_rate;

	@Excel(name = "通道成功率")
	private String current_success_rate_str;

	public String getCurrent_success_rate_str() {
		if (current_success_rate != null) {
			return current_success_rate.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toString().concat("%");
		}
		return "";
	}


	private BigDecimal pay_rate;

	@Excel(name = "支付通道费率" )
	private String pay_rate_str;

	public String getPay_rate_str() {
		if (pay_rate != null) {
			return pay_rate.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toString().concat("%");
		}
		return "";
	}

	@Excel(name = "上游订单号")
	private String trade_sn;

	private String payment_time;

	@Excel(name = "商户下单时间")
	private String pay_time;

	private String status;

	@Excel(name = "订单状态")
	private String statusDes;
	public String getStatusDes() {
		if(!StringUtils.isEmpty(status)){
			switch (status){
				case "1":
					return "成功";
				case "0":
					return "失败";
				case "-1":
					return "待确认";
			}
		}
		return "待确认";
	}

	private Integer first;

	@Excel(name = "是否首冲")
	private Integer firstDes;
	public String getFirstDes() {
		if(first != null) {
			switch (first) {
				case 1:
					return "是";
				case 0:
					return "否";
			}
		}
		return "否";
	}


	@Excel(name = "备注")
	private String remark;

	@Excel(name = "会员id")
	private String member_id;

	private String user_name;

	private String platform_id;

	@Excel(name = "支付平台名称")
	private String platform_name;

	private Boolean is_patch_order;

	private String channel_id;

	@Excel(name = "支付通道名称")
	private String channel_name;

	@Excel(name = "回调时间")
	private String update_time;
}
