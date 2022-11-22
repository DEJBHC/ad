package io.finer.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

/**
 * @Description: 应付单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Data
@TableName("fin_payable")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="fin_payable对象", description="应付单")
public class FinPayable extends Bill {
	/**应付类型*/
	@Excel(name = "应付类型", width = 15, dicCode = "x_payable_type")
	@Dict(dicCode = "x_payable_type")
	@ApiModelProperty(value = "应付类型")
	private java.lang.String payableType;
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
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amt;
	/**已核销金额*/
	@Excel(name = "已核销金额", width = 15)
    @ApiModelProperty(value = "已核销金额")
    private java.math.BigDecimal checkedAmt;
	/**未核销金额*/
	@TableField(exist = false)
	private java.math.BigDecimal uncheckedAmt;

	public BigDecimal getUncheckedAmt() {
		//controller中，QueryWrapper也会创建entity；
		//(uncheckedAmt == null)，是为了避免作为查询数据库参数。
		return this.getId() == null ? null : amt.subtract(checkedAmt);
	}

	@Override
	public String getBillType() {
		return this.getClass().getSimpleName() + ":" + payableType;
	}
}
