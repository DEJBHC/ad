package io.finer.erp.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Bill;
import io.finer.erp.common.entity.Entry;
import io.finer.erp.common.util.BillUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 收款单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="fin_receipt对象", description="收款单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_receipt")
public class FinReceipt extends Bill {
	/**收款类型*/
	@Excel(name = "收款类型", width = 15, dicCode = "x_receipt_type")
    @Dict(dicCode = "x_receipt_type")
    @ApiModelProperty(value = "收款类型")
    private java.lang.String receiptType;
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private java.lang.String customerId;
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

    public String getBillType() {
        return this.getClass().getSimpleName() + ":" + receiptType;
    }

}
