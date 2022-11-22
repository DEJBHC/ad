package io.finer.erp.finance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

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
@ApiModel(value="fin_payable_checkPage对象", description="往来核销单")
public class FinPayableCheckPage extends BillPage {
	/**核销类型*/
	@Excel(name = "核销类型", width = 15, dicCode = "x_payable_check_type")
	@Dict(dicCode = "x_payable_check_type")
	@ApiModelProperty(value = "核销类型")
	private String payableCheckType;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "供应商")
	private String supplierId;
	/**核销金额*/
	@Excel(name = "核销金额", width = 15)
	@ApiModelProperty(value = "核销金额")
	private BigDecimal amt;

	@ExcelCollection(name="往来核销明细")
	@ApiModelProperty(value = "往来核销明细")
	private List<FinPayableCheckEntry> finPayableCheckEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + payableCheckType;
	}
}
