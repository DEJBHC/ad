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
 * @Description: 采购比价单
 * @Author: jeecg-boot
 * @Date:   2022-06-03
 * @Version: V1.0
 */
@ApiModel(value="pur_compare对象", description="采购比价单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_compare")
public class PurCompare extends Bill {
	/**付款方式*/
	@Excel(name = "付款方式", width = 15, dicCode = "x_payment_method")
    @Dict(dicCode = "x_payment_method")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;
	/**交货地点*/
	@Excel(name = "交货地点", width = 15)
    @ApiModelProperty(value = "交货地点")
    private String deliveryPlace;
	/**交货时间*/
	@Excel(name = "交货时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "交货时间")
    private java.util.Date deliveryTime;
    /**候选报价单ids*/
    @Excel(name = "候选报价单ids", width = 15)
    @ApiModelProperty(value = "候选报价单ids")
    private String candidateQuotIds;
}
