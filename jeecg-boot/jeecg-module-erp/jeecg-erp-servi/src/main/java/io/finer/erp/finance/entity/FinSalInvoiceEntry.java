package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 明细
 * @Author:
 * @Date:   2020-05-24
 * @Version: V1.0
 */
@ApiModel(value="fin_sal_invoice对象", description="销售发票登记")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_sal_invoice_entry")
public class FinSalInvoiceEntry extends Entry {
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料")
	private java.lang.String materialId;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "计量单位")
	private java.lang.String unitId;
	/**税率%*/
	@Excel(name = "税率%", width = 15)
	@ApiModelProperty(value = "税率%")
	private java.math.BigDecimal taxRate;
	/**开票数量*/
	@Excel(name = "开票数量", width = 15)
	@ApiModelProperty(value = "开票数量")
	private java.math.BigDecimal qty;
	/**开票金额*/
	@Excel(name = "开票金额", width = 15)
	@ApiModelProperty(value = "开票金额")
	private java.math.BigDecimal amt;

	//20221018 del
//	/**物料ID*/
//	@TableField(exist = false)
//	private java.lang.String srcMaterialId;
//	/**计量单位*/
//	@TableField(exist = false)
//	private java.lang.String srcUnitId;
//	/**结算数量*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcSettleQty;
//	/**结算金额*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcSettleAmt;
//	/**已开票数量*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcInvoicedQty;
//	/**已开票金额*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcInvoicedAmt;
//	/**蓝单开票数量（用于红单）*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcBlueQty;
//	/**蓝单开票金额（用于红单）*/
//	@TableField(exist = false)
//	private java.math.BigDecimal srcBlueAmt;
}
