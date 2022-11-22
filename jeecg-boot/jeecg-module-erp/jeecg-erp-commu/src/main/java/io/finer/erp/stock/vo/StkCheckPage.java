package io.finer.erp.stock.vo;

import io.finer.erp.common.vo.BillPage;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @Description: 盘点卡
 * @Author: jeecg-boot
 * @Date:   2020-05-18
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="stk_checkPage对象", description="盘点卡")
public class StkCheckPage extends BillPage {
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "仓库")
	private java.lang.String warehouseId;
	/**物料分类*/
	@Excel(name = "物料分类", width = 15, dictTable = "bas_material_category", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material_category", dicText = "name", dicCode = "id")
	@ApiModelProperty(value = "物料分类")
	private java.lang.String materialCategoryId;
	/**盘点人*/
	@Excel(name = "盘点人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@ApiModelProperty(value = "盘点人")
	private java.lang.String checker;

	@ExcelCollection(name="明细")
	@ApiModelProperty(value = "明细")
	private List<StkCheckEntry> stkCheckEntryList;
}
