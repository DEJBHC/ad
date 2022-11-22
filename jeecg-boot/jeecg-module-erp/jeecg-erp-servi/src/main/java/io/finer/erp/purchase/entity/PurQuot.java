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
 * @Description: pur_quot
 * @Author: jeecg-boot
 * @Date:   2022-06-03
 * @Version: V1.0
 */
@ApiModel(value="pur_quot对象", description="pur_quot")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_quot")
public class PurQuot extends Bill {
	/**是否临时供应商*/
	@Excel(name = "是否临时供应商", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否临时供应商")
    private java.lang.Integer isTempSupplier;
	/**供应商名称*/
	@Excel(name = "供应商名称", width = 15)
    @ApiModelProperty(value = "供应商名称")
    private java.lang.String supplierName;
    /**供应商*/
    @Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private java.lang.String supplierId;
	/**付款方式*/
	@Excel(name = "付款方式", width = 15, dicCode = "x_payment_method")
    @Dict(dicCode = "x_payment_method")
    @ApiModelProperty(value = "付款方式")
    private java.lang.String paymentMethod;
	/**交货地点*/
	@Excel(name = "交货地点", width = 15)
    @ApiModelProperty(value = "交货地点")
    private java.lang.String deliveryPlace;
	/**交货时间*/
	@Excel(name = "交货时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "交货时间")
    private java.util.Date deliveryTime;
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
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**金额*/
    @Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amt;
}
