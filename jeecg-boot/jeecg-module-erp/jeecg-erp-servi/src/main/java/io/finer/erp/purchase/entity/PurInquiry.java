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
 * @Description: 采购询价单
 * @Author: jeecg-boot
 * @Date:   2022-05-31
 * @Version: V1.0
 */
@ApiModel(value="pur_inquiry对象", description="采购询价单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_inquiry")
public class PurInquiry extends Bill {
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
    @Excel(name = "联系人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "联系人")
    private java.lang.String contact;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
	/**fax*/
	@Excel(name = "fax", width = 15)
    @ApiModelProperty(value = "fax")
    private java.lang.String fax;
	/**email*/
	@Excel(name = "email", width = 15)
    @ApiModelProperty(value = "email")
    private java.lang.String email;
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**参考金额*/
    @Excel(name = "参考金额", width = 15)
    @ApiModelProperty(value = "参考金额")
    private java.math.BigDecimal amt;
    /**已生效报价单数*/
    @Excel(name = "已生效报价单数", width = 15)
    @ApiModelProperty(value = "已生效报价单数")
    private Integer quotCount;
}
