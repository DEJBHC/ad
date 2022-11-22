package io.finer.erp.purchase.entity;

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
 * @Date:   2022-06-03
 * @Version: V1.0
 */
@ApiModel(value="pur_compare_entry对象", description="明细")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_compare_entry")
public class PurCompareEntry extends Entry {
    /**供应商*/
    @Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private java.lang.String supplierId;
	/**物料*/
    @Excel(name = "物料", width = 15, dictTable = "bas_material", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_material", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "物料")
    private String materialId;
	/**计量单位*/
    @Excel(name = "计量单位", width = 15, dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_unit", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "计量单位")
    private String unitId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
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
    /**含税金额*/
    @Excel(name = "含税金额", width = 15)
    @ApiModelProperty(value = "含税金额")
    private java.math.BigDecimal amt;
    /**排名*/
    @Excel(name = "排名", width = 15)
    @ApiModelProperty(value = "排名")
    private Integer ranking;
}
