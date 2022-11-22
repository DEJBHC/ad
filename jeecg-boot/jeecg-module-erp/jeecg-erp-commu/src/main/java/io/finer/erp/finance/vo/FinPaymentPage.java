package io.finer.erp.finance.vo;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinPaymentEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: 付款单
 * @Author: jeecg-boot
 * @Date:   2020-04-14
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_paymentPage对象", description="付款单")
public class FinPaymentPage extends BillPage {
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
	private java.math.BigDecimal uncheckedAmt;

	@ExcelCollection(name="付款明细")
	@ApiModelProperty(value = "付款明细")
	private List<FinPaymentEntry> finPaymentEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + paymentType;
	}
}
