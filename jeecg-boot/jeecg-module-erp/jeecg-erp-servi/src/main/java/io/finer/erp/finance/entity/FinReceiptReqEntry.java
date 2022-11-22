package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: (红字)收款申请单明细
 * @Author: jeecg-boot
 * @Date:   2022-09-07
 * @Version: V1.0
 */
@ApiModel(value="fin_receipt_req_entry对象", description="(红字)收款申请单明细")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_receipt_req_entry")
public class FinReceiptReqEntry extends Entry {
    /**申请金额*/
    @Excel(name = "申请金额", width = 15)
    @ApiModelProperty(value = "申请金额")
    private java.math.BigDecimal amt;
    /**已收金额*/
    @Excel(name = "已收金额", width = 15)
    @ApiModelProperty(value = "已收金额")
    private java.math.BigDecimal receivedAmt;
}
