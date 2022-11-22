package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: fin_payable_sum
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Data
@TableName("fin_payable_sum")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fin_payable_sum对象", description="fin_payable_sum")
public class FinPayableSum implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private String id;
	/**业务年度*/
	@Excel(name = "业务年度", width = 15)
    @ApiModelProperty(value = "业务年度")
    private Integer year;
	/**业务月度*/
	@Excel(name = "业务月度", width = 15)
    @ApiModelProperty(value = "业务月度")
    private Integer month;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private String supplierId;
	/**期初贷方余额*/
	@Excel(name = "期初贷方余额", width = 15)
    @ApiModelProperty(value = "期初贷方余额")
    private BigDecimal beginCreditBal;
	/**借方金额*/
	@Excel(name = "借方金额", width = 15)
    @ApiModelProperty(value = "借方金额")
    private BigDecimal debitAmt;
	/**贷方金额*/
	@Excel(name = "贷方金额", width = 15)
    @ApiModelProperty(value = "贷方金额")
    private BigDecimal creditAmt;
	/**贷方余额*/
	@Excel(name = "贷方余额", width = 15)
	@ApiModelProperty(value = "贷方余额")
	private BigDecimal creditBal;
}
