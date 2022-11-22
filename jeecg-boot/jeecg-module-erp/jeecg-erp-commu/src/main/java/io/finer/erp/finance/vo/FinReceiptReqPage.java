package io.finer.erp.finance.vo;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinReceiptReqEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: (红字)收款申请单
 * @Author: jeecg-boot
 * @Date:   2022-09-08
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_receipt_reqPage对象", description="(红字)收款申请单")
public class FinReceiptReqPage extends BillPage {
	/**收款类型*/
	@Excel(name = "收款类型", width = 15, dicCode = "x_receipt_type")
	@Dict(dicCode = "x_receipt_type")
	@ApiModelProperty(value = "收款类型")
	private String receiptType;
	/**客户*/
	@Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "客户")
	private java.lang.String customerId;
	/**业务部门*/
	@Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@ApiModelProperty(value = "业务部门")
	private java.lang.String opDept;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "业务员")
	private String operator;
	/**申请金额*/
	@Excel(name = "申请金额", width = 15)
	@ApiModelProperty(value = "申请金额")
	private java.math.BigDecimal amt;
	/**已收金额*/
	@Excel(name = "已收金额", width = 15)
	@ApiModelProperty(value = "已收金额")
	private java.math.BigDecimal receivedAmt;

	@ExcelCollection(name="付款申请单明细")
	@ApiModelProperty(value = "付款申请单明细")
	private List<FinReceiptReqEntry> finReceiptReqEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + receiptType;
	}

}
