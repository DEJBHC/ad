package io.finer.erp.finance.entity;

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
 * @Description: 应收核销单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="fin_receivable_check对象", description="应收核销单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_receivable_check")
public class FinReceivableCheck extends Bill {
	/**核销类型*/
	@Excel(name = "核销类型", width = 15, dicCode = "x_receivable_check_type")
    @Dict(dicCode = "x_receivable_check_type")
    @ApiModelProperty(value = "核销类型")
    private String receivableCheckType;
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private String customerId;
    /**核销金额*/
    @Excel(name = "核销金额", width = 15)
    @ApiModelProperty(value = "核销金额")
    private BigDecimal amt;

    public String getBillType() {
        return this.getClass().getSimpleName() + ":" + receivableCheckType;
    }
}
