package io.finer.erp.purchase.entity;

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
 * @Author: jeecg-boot
 * @Date:   2022-06-08
 * @Version: V1.0
 */
@ApiModel(value="pur_order对象", description="采购订单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_order")
public class PurOrder extends Bill {
 	/**采购类型*/
	@Excel(name = "采购类型", width = 15, dicCode = "x_pur_type")
    @Dict(dicCode = "x_pur_type")
    @ApiModelProperty(value = "采购类型")
    private java.lang.String purType;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private java.lang.String supplierId;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private java.lang.String contact;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
	/**传真*/
	@Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private java.lang.String fax;
	/**电子邮件*/
	@Excel(name = "电子邮件", width = 15)
    @ApiModelProperty(value = "电子邮件")
    private java.lang.String email;
    /**业务部门*/
    @Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "业务部门")
    private java.lang.String opDept;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "业务员")
    private java.lang.String operator;
	/**交货方式*/
	@Excel(name = "交货方式", width = 15, dicCode = "x_delivery_method")
    @Dict(dicCode = "x_delivery_method")
    @ApiModelProperty(value = "交货方式")
    private java.lang.String deliveryMethod;
	/**交货地点*/
	@Excel(name = "交货地点", width = 15)
    @ApiModelProperty(value = "交货地点")
    private java.lang.String deliveryPlace;
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
    private java.lang.String transportMethod;
	/**付款方式*/
	@Excel(name = "付款方式", width = 15, dicCode = "x_payment_method")
    @Dict(dicCode = "x_payment_method")
    @ApiModelProperty(value = "付款方式")
    private java.lang.String paymentMethod;
	/**结算方式*/
	@Excel(name = "结算方式", width = 15, dicCode = "x_settle_method")
    @Dict(dicCode = "x_settle_method")
    @ApiModelProperty(value = "结算方式")
    private java.lang.String settleMethod;
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
    private java.lang.String invoiceMethod;
	/**发票类型*/
	@Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
    @Dict(dicCode = "x_invoice_type")
    @ApiModelProperty(value = "发票类型")
    private java.lang.String invoiceType;
	/**外币币种*/
	@Excel(name = "外币币种", width = 15, dictTable = "bas_currency", dicText = "name", dicCode = "code")
    @Dict(dictTable = "bas_currency", dicText = "name", dicCode = "code")
    @ApiModelProperty(value = "外币币种")
    private java.lang.String currency;
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
	/**预付余额*/
	@Excel(name = "预付余额", width = 15)
    @ApiModelProperty(value = "预付余额")
    private java.math.BigDecimal prepaymentBal;
    /**已入库数量*/
    @Excel(name = "已入库数量", width = 15)
    @ApiModelProperty(value = "已入库数量")
    private java.math.BigDecimal inQty;
    /**已入库成本*/
    @Excel(name = "已入库成本", width = 15)
    @ApiModelProperty(value = "已入库成本")
    private java.math.BigDecimal inCost;
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
}
