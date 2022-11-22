package io.finer.erp.finance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 销售发票登记
 * @Author:
 * @Date:   2020-05-24
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_sal_invoicePage对象", description="销售发票登记")
public class FinSalInvoicePage extends BillPage {
	/**是否退货退票*/
	@Excel(name = "是否退货退票", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
	@ApiModelProperty(value = "是否退货退票")
	private Integer isReturned;
	/**发票类型*/
	@Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
	@Dict(dicCode = "x_invoice_type")
	@ApiModelProperty(value = "发票类型")
	private String invoiceType;
	/**蓝字发票号（红字发票使用）*/
	@Excel(name = "蓝字发票号", width = 15)
	@ApiModelProperty(value = "蓝字发票号")
	private java.lang.String blueInvoiceNo;
	/**发票号*/
	@Excel(name = "发票号", width = 15)
	@ApiModelProperty(value = "发票号")
	private java.lang.String invoiceNo;
	/**开票日期*/
	@Excel(name = "开票日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "开票日期")
	private java.util.Date invoiceDate;
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "客户")
	private String customerId;
	/**金额*/
	@Excel(name = "金额", width = 15)
	@ApiModelProperty(value = "金额")
	private java.math.BigDecimal amt;

	@ExcelCollection(name="明细")
	@ApiModelProperty(value = "明细")
	private List<FinSalInvoiceEntry> finSalInvoiceEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + invoiceType;
	}
}
