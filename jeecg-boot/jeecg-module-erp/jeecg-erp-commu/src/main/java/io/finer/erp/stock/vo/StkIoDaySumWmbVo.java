package io.finer.erp.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * @Description: 出入库日汇总-仓库物料批次
 * @Author:
 * @Date:
 * @Version:
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StkIoDaySumWmbVo {
	private java.lang.String id;
	/**日期*/
	@Excel(name = "日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "日期")
	private java.util.Date date;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "仓库")
	private java.lang.String warehouseId;
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料")
	private java.lang.String materialId;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
	@ApiModelProperty(value = "物料编码")
	private java.lang.String materialCode;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
	@ApiModelProperty(value = "规格型号")
	private java.lang.String materialModel;
	/**批次号*/
	@Excel(name = "批次号", width = 15)
	@ApiModelProperty(value = "批次号")
	private java.lang.String batchNo;
	/**单位*/
	@Excel(name = "单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "单位")
	private java.lang.String unitId;
	/**入库数量*/
	@Excel(name = "入库数量", width = 15)
	@ApiModelProperty(value = "入库数量")
	private java.math.BigDecimal inQty;
	/**入库成本*/
	@Excel(name = "入库成本", width = 15)
	@ApiModelProperty(value = "入库成本")
	private java.math.BigDecimal inCost;
	/**出库数量*/
	@Excel(name = "出库数量", width = 15)
	@ApiModelProperty(value = "出库数量")
	private java.math.BigDecimal outQty;
	/**出库成本*/
	@Excel(name = "出库成本", width = 15)
	@ApiModelProperty(value = "出库成本")
	private java.math.BigDecimal outCost;
	/**结存数量*/
	@Excel(name = "结存数量", width = 15)
	@ApiModelProperty(value = "结存数量")
	private java.math.BigDecimal balQty;
	/**结存成本*/
	@Excel(name = "结存成本", width = 15)
	@ApiModelProperty(value = "结存成本")
	private java.math.BigDecimal balCost;
}
