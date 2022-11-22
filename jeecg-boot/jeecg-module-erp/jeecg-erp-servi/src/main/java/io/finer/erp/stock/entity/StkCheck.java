package io.finer.erp.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 盘点卡
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="stk_check对象", description="盘点卡")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_check")
public class StkCheck extends Bill {
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
}
