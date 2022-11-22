package io.finer.erp.finance.vo;

import java.util.List;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.finance.entity.FinPaymentReq;
import io.finer.erp.finance.entity.FinPaymentReqEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 付款申请单
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_payment_reqPage对象", description="付款申请单")
public class FinPaymentReqPage extends BillPage {
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
	/**业务部门*/
	@Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
	@ApiModelProperty(value = "业务部门")
	private java.lang.String opDept;
	/**业务员*/
	@Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "业务员")
    private java.lang.String operator;
	/**申请金额*/
	@Excel(name = "申请金额", width = 15)
	@ApiModelProperty(value = "申请金额")
	private java.math.BigDecimal amt;
	/**已付金额*/
	@Excel(name = "已付金额", width = 15)
	@ApiModelProperty(value = "已付金额")
	private java.math.BigDecimal paidAmt;

	@ExcelCollection(name="付款申请单明细")
	@ApiModelProperty(value = "付款申请单明细")
	private List<FinPaymentReqEntry> finPaymentReqEntryList;

	@Override
	public String getBillType() {
		return super.getBillType() + ":" + paymentType;
	}
}
