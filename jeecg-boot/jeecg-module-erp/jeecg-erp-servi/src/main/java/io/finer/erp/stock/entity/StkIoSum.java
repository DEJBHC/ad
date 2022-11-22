package io.finer.erp.stock.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 出入库汇总
 * @Author: jeecg-boot
 * @Date:   2022-09-28
 * @Version: V1.0
 */
@Data
@TableName("stk_io_sum")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="stk_io_sum对象", description="出入库汇总")
public class StkIoSum implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private String id;
	/**year*/
	@Excel(name = "year", width = 15)
    @ApiModelProperty(value = "year")
    private Integer year;
	/**month*/
	@Excel(name = "month", width = 15)
    @ApiModelProperty(value = "month")
    private Integer month;
	/**物料*/
	@Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "物料")
    private String materialId;
	/**批次*/
	@Excel(name = "批次", width = 15)
    @ApiModelProperty(value = "批次")
    private String batchNo;
	/**仓库*/
	@Excel(name = "仓库", width = 15, dictTable = "bas_warehouse", dicText = "nam", dicCode = "id")
	@Dict(dictTable = "bas_warehouse", dicText = "nam", dicCode = "id")
    @ApiModelProperty(value = "仓库")
    private String warehouseId;
	/**计量单位*/
	@Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
	@Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "计量单位")
    private String unitId;
	/**期初数量*/
	@Excel(name = "期初数量", width = 15)
    @ApiModelProperty(value = "期初数量")
    private BigDecimal beginQty;
	/**期初成本*/
	@Excel(name = "期初成本", width = 15)
    @ApiModelProperty(value = "期初成本")
    private BigDecimal beginCost;
	/**入库数量*/
	@Excel(name = "入库数量", width = 15)
    @ApiModelProperty(value = "入库数量")
    private BigDecimal inQty;
	/**入库成本*/
	@Excel(name = "入库成本", width = 15)
    @ApiModelProperty(value = "入库成本")
    private BigDecimal inCost;
	/**出库数量*/
	@Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private BigDecimal outQty;
	/**出库成本*/
	@Excel(name = "出库成本", width = 15)
    @ApiModelProperty(value = "出库成本")
    private BigDecimal outCost;
	/**结存数量*/
	@Excel(name = "结存数量", width = 15)
    @ApiModelProperty(value = "结存数量")
    private BigDecimal balQty;
	/**结存成本*/
	@Excel(name = "结存成本", width = 15)
    @ApiModelProperty(value = "结存成本")
    private BigDecimal balCost;
}
