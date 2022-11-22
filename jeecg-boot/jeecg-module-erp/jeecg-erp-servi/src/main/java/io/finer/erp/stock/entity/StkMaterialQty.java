package io.finer.erp.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: stk_material_qty
 * @Author: jeecg-boot
 * @Date:   2022-08-16
 * @Version: V1.0
 */
@Data
@TableName("stk_material_qty")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="stk_material_qty对象", description="stk_material_qty")
public class StkMaterialQty implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**计量单位*/
    @Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "计量单位")
    private java.lang.String unitId;
	/**qty*/
	@Excel(name = "qty", width = 15)
    @ApiModelProperty(value = "qty")
    private java.math.BigDecimal qty;
}
