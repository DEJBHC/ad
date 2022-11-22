package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 付款申请单
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
@ApiModel(value="fin_payment_req对象", description="付款申请单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payment_req")
public class FinPaymentReq extends Bill {
	/**付款类型*/
	@Excel(name = "付款类型", width = 15, dicCode = "x_payment_type")
    @Dict(dicCode = "x_payment_type")
    @ApiModelProperty(value = "付款类型")
    private java.lang.String paymentType;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private java.lang.String supplierId;
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
    /**申请金额*/
    @Excel(name = "申请金额", width = 15)
    @ApiModelProperty(value = "申请金额")
    private java.math.BigDecimal amt;
    /**已付金额*/
    @Excel(name = "已付金额", width = 15)
    @ApiModelProperty(value = "已付金额")
    private java.math.BigDecimal paidAmt;

    @Override
    public String getBillType() {
        return super.getBillType() + ":" + paymentType;
    }
}
