package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * @Description: fin_receivable_check_entry
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Data
@TableName("fin_receivable_check_entry")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_receivable_check_entry对象", description="fin_receivable_check_entry")
public class FinReceivableCheckEntry extends Entry {
    /**核销方向*/
    @Excel(name = "核销方向", width = 15, dicCode = "x_rp_check_side")
    @Dict(dicCode = "x_rp_check_side")
    @ApiModelProperty(value = "核销方向")
    private String checkSide;
	/**核销金额*/
	@Excel(name = "核销金额", width = 15)
    @ApiModelProperty(value = "核销金额")
    private BigDecimal amt;

    /**未核销金额*/
    @TableField(exist = false)
    @Excel(name = "未核销金额", width = 15)
    @ApiModelProperty(value = "未核销金额")
    private BigDecimal uncheckedAmt; //在controller中设置
}
