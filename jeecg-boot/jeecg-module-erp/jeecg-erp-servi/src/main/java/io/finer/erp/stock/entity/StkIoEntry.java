package io.finer.erp.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Entry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2020-03-31
 * @Version: V1.0
 */
@ApiModel(value="stk_io对象", description="出入库单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_io_entry")
public class StkIoEntry extends Entry {
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料")
	private java.lang.String materialId;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
	@ApiModelProperty(value = "批次号")
	private java.lang.String batchNo;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "仓库")
	private java.lang.String warehouseId;
	/**出入方向*/
	@Excel(name = "出入方向", width = 15, dicCode = "x_stock_io_direction")
	@Dict(dicCode = "x_stock_io_direction")
	@ApiModelProperty(value = "出入方向")
	private java.lang.String stockIoDirection;
	/**供应商*/
	@Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "供应商")
	private java.lang.String supplierId;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "计量单位")
	private java.lang.String unitId;
	/**涨吨数量+/-*/
	@Excel(name = "涨吨数量+/-", width = 15)
	@ApiModelProperty(value = "涨吨数量+/-")
	private java.math.BigDecimal swellQty;
	/**数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private java.math.BigDecimal qty;
	/**计入成本费用*/
	@Excel(name = "计入成本费用", width = 15)
	@ApiModelProperty(value = "计入成本费用")
	private java.math.BigDecimal expense;
	/**成本*/
	@Excel(name = "成本", width = 15)
	@ApiModelProperty(value = "成本")
	private java.math.BigDecimal cost;
	/**结算数量*/
	@Excel(name = "结算数量", width = 15)
	@ApiModelProperty(value = "结算数量")
	private java.math.BigDecimal settleQty;
	/**税率%*/
	@Excel(name = "税率%", width = 15)
	@ApiModelProperty(value = "税率%")
	private java.math.BigDecimal taxRate;
	/**含税单价*/
	@Excel(name = "含税单价", width = 15)
	@ApiModelProperty(value = "含税单价")
	private java.math.BigDecimal price;
	/**折扣率%*/
	@Excel(name = "折扣率%", width = 15)
	@ApiModelProperty(value = "折扣率%")
	private java.math.BigDecimal discountRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
	@ApiModelProperty(value = "税额")
	private java.math.BigDecimal tax;
	/**结算金额*/
	@Excel(name = "结算金额", width = 15)
	@ApiModelProperty(value = "结算金额")
	private java.math.BigDecimal settleAmt;

	/**已开票数量*/
	@Excel(name = "已开票数量", width = 15)
	@ApiModelProperty(value = "已开票数量")
	private java.math.BigDecimal invoicedQty;
	/**已开票金额*/
	@Excel(name = "已开票金额", width = 15)
	@ApiModelProperty(value = "已开票金额")
	private java.math.BigDecimal invoicedAmt;

	/**实时库存计量单位*/
	@TableField(exist = false)
	private java.lang.String inventoryUnitId;
	/**实时库存数量*/
	@TableField(exist = false)
	private java.math.BigDecimal inventoryQty;
	/**实时库存成本*/
	@TableField(exist = false)
	private java.math.BigDecimal inventoryCost;
}
