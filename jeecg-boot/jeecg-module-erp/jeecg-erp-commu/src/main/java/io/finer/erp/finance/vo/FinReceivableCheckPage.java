package io.finer.erp.finance.vo;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 往来核销单
 * @Author: jeecg-boot
 * @Date:   2020-04-17
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_receivable_checkPage对象", description="往来核销单")
public class FinReceivableCheckPage extends BillPage {
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

	@ExcelCollection(name="往来核销明细")
	@ApiModelProperty(value = "往来核销明细")
	private List<FinReceivableCheckEntry> finReceivableCheckEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + receivableCheckType;
	}

}
