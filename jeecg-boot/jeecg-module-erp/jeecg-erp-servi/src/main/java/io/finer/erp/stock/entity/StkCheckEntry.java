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
 * @Date:   2020-05-18
 * @Version: V1.0
 */
@ApiModel(value="stk_check对象", description="盘点卡")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_check_entry")
public class StkCheckEntry extends Entry {
	/**是否新批次*/
	@Excel(name = "是否新批次", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
	@ApiModelProperty(value = "是否新批次")
	private java.lang.Integer isNewBatch;
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
	/**批号*/
	@Excel(name = "批号", width = 15)
	@ApiModelProperty(value = "批号")
	private java.lang.String batchNo;
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
	/**账存数量*/
	@Excel(name = "账存数量", width = 15)
	@ApiModelProperty(value = "账存数量")
	private java.math.BigDecimal bookQty;
	/**实存数量*/
	@Excel(name = "实存数量", width = 15)
	@ApiModelProperty(value = "实存数量")
	private java.math.BigDecimal qty;

	/**盘盈数量*/
	@TableField(exist = false)
	private java.math.BigDecimal profitQty;
	public java.math.BigDecimal getProfitQty() {
		//controller中，QueryWrapper也会创建entity；
		//(profitQty == null)，是为了避免作为查询数据库参数。
		return (this.getId() == null || qty == null) ? null : qty.subtract(bookQty);
	}

}
