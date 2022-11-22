package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 收款明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="fin_receipt对象", description="收款单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_receipt_entry")
public class FinReceiptEntry extends Entry {
	/**结算方式*/
	@Excel(name = "结算方式", width = 15, dicCode = "x_settle_method")
	@Dict( dicCode = "x_settle_method")
	@ApiModelProperty(value = "结算方式")
	private java.lang.String settleMethod;
	/**资金账户*/
	@Excel(name = "资金账户", width = 15, dictTable = "bas_bank_account", dicText = "account_no", dicCode = "id")
	@Dict(dictTable = "bas_bank_account", dicText = "account_no", dicCode = "id")
	@ApiModelProperty(value = "资金账户")
	private java.lang.String bankAccountId;
	/**金额*/
	@Excel(name = "金额", width = 15)
	@ApiModelProperty(value = "金额")
	private java.math.BigDecimal amt;
}
