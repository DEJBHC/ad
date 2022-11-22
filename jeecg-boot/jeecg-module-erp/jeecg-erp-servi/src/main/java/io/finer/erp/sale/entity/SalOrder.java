package io.finer.erp.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 采购订单
 * @Author:
 * @Date:
 * @Version:
 */
@ApiModel(value="sal_order对象", description="采购订单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_order")
public class SalOrder extends Bill {
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private String customerId;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private String contact;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String phone;
	/**传真*/
	@Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private String fax;
	/**电子邮件*/
	@Excel(name = "电子邮件", width = 15)
    @ApiModelProperty(value = "电子邮件")
    private String email;
    /**业务部门*/
    @Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "业务部门")
    private java.lang.String opDept;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "业务员")
    private String operator;
	/**交货方式*/
	@Excel(name = "交货方式", width = 15, dicCode = "x_delivery_method")
    @Dict(dicCode = "x_delivery_method")
    @ApiModelProperty(value = "交货方式")
    private String deliveryMethod;
	/**交货地点*/
	@Excel(name = "交货地点", width = 15)
    @ApiModelProperty(value = "交货地点")
    private String deliveryPlace;
	/**交货日期*/
	@Excel(name = "交货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "交货日期")
    private java.util.Date deliveryTime;
	/**运输方式*/
	@Excel(name = "运输方式", width = 15, dicCode = "x_transport_method")
    @Dict(dicCode = "x_transport_method")
    @ApiModelProperty(value = "运输方式")
    private String transportMethod;
	/**付款方式*/
	@Excel(name = "付款方式", width = 15, dicCode = "x_payment_method")
    @Dict(dicCode = "x_payment_method")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;
	/**结算方式*/
	@Excel(name = "结算方式", width = 15, dicCode = "x_settle_method")
    @Dict(dicCode = "x_settle_method")
    @ApiModelProperty(value = "结算方式")
    private String settleMethod;
	/**结算日期*/
	@Excel(name = "结算日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结算日期")
    private java.util.Date settleTime;
	/**开票方式*/
	@Excel(name = "开票方式", width = 15, dicCode = "x_invoice_method")
    @Dict(dicCode = "x_invoice_method")
    @ApiModelProperty(value = "开票方式")
    private String invoiceMethod;
	/**发票类型*/
	@Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
    @Dict(dicCode = "x_invoice_type")
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;
	/**外币币种*/
	@Excel(name = "外币币种", width = 15, dictTable = "bas_currency", dicText = "name", dicCode = "code")
    @Dict(dictTable = "bas_currency", dicText = "name", dicCode = "code")
    @ApiModelProperty(value = "外币币种")
    private String currency;
	/**汇率*/
	@Excel(name = "汇率", width = 15)
    @ApiModelProperty(value = "汇率")
    private java.math.BigDecimal exchangeRate;
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amt;
	/**预收余额*/
	@Excel(name = "预收余额", width = 15)
    @ApiModelProperty(value = "预收余额")
    private java.math.BigDecimal prereceiptBal;
    /**已出库数量*/
    @Excel(name = "已出库数量", width = 15)
    @ApiModelProperty(value = "已出库数量")
    private java.math.BigDecimal outQty;
    /**已出库成本*/
    @Excel(name = "已出库成本", width = 15)
    @ApiModelProperty(value = "已出库成本")
    private java.math.BigDecimal outCost;
    /**结算数量*/
    @Excel(name = "结算数量", width = 15)
    @ApiModelProperty(value = "结算数量")
    private java.math.BigDecimal settleQty;
    /**结算金额*/
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settleAmt;
    /**已结算金额*/
    @Excel(name = "已结算金额", width = 15)
    @ApiModelProperty(value = "已结算金额")
    private java.math.BigDecimal settledAmt;
    /**已开票金额*/
    @Excel(name = "已开票金额", width = 15)
    @ApiModelProperty(value = "已开票金额")
    private java.math.BigDecimal invoicedAmt;
    /**不含税结算金额*/
    @Excel(name = "不含税结算金额", width = 15)
    @ApiModelProperty(value = "不含税结算金额")
    private java.math.BigDecimal exTaxSettleAmt;
    /**毛利润*/
    @Excel(name = "毛利润", width = 15)
    @ApiModelProperty(value = "毛利润")
    private java.math.BigDecimal grossProfit;
}
