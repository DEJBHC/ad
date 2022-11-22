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

import java.math.BigDecimal;

/**
 * @Description: 付款单
 * @Author: jeecg-boot
 * @Date:   2020-04-14
 * @Version: V1.0
 */
@ApiModel(value="fin_payment对象", description="付款单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payment")
public class FinPayment extends Bill {

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
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amt;
	/**已核销金额*/
	@Excel(name = "已核销金额", width = 15)
    @ApiModelProperty(value = "已核销金额")
    private java.math.BigDecimal checkedAmt;

    /**未核销金额*/
    @TableField(exist = false)
    private java.math.BigDecimal uncheckedAmt;

    public BigDecimal getUncheckedAmt() {
        //controller中，QueryWrapper也会创建entity；
        //(uncheckedAmt == null)，是为了避免作为查询数据库参数。
        return this.getId() == null ? null : amt.subtract(checkedAmt);
    }

    @Override
    public String getBillType() {
        return this.getClass().getSimpleName() + ":" + paymentType;
    }

}
