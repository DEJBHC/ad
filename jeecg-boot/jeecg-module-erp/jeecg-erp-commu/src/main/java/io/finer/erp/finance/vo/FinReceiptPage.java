package io.finer.erp.finance.vo;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_receiptPage对象", description="收款单")
public class FinReceiptPage extends BillPage {
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
	private java.math.BigDecimal uncheckedAmt;

	@ExcelCollection(name="收款明细")
	@ApiModelProperty(value = "收款明细")
	private List<FinReceiptEntry> finReceiptEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + receiptType;
	}
}
