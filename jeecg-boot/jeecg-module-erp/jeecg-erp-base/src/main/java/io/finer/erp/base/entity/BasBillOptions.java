package io.finer.erp.base.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: bas_bill_options
 * @Author: jeecg-boot
 * @Date:   2022-01-23
 * @Version: V1.0
 */
@Data
@TableName("bas_bill_options")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bas_bill_options对象", description="bas_bill_options")
public class BasBillOptions implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**单据类型*/
	@Excel(name = "单据类型", width = 15, dicCode = "x_bill_type")
	@Dict(dicCode = "x_bill_type")
    @ApiModelProperty(value = "单据类型")
    private java.lang.String billType;
	/**手动单据核批类型*/
	@Excel(name = "手动单据核批类型/审批", width = 15, dicCode = "x_approval_type")
	@Dict(dicCode = "x_approval_type")
    @ApiModelProperty(value = "手动单据核批类型")
    private java.lang.String mbillApprovalType;
    /**自动单据核批类型*/
    @Excel(name = "自动单据核批类型/审批", width = 15, dicCode = "x_approval_type")
    @Dict(dicCode = "x_approval_type")
    @ApiModelProperty(value = "自动单据核批类型")
    private java.lang.String abillApprovalType;
    /**BPM类型*/
    @Excel(name = "BPM类型", width = 15, dicCode = "x_bpm_type")
    @Dict(dicCode = "x_bpm_type")
    @ApiModelProperty(value = "BPM类型")
    private java.lang.String bpmType;
    /**是否生效后自动执行*/
    @Excel(name = "是否生效后自动执行", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否生效后自动执行")
    private java.lang.Integer isAutoExecute;
    /**是否自动结束执行*/
    @Excel(name = "是否自动结束执行", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否自动结束执行")
    private java.lang.Integer isAutoEndExecute;
    /**是否执行完后自动关闭*/
    @Excel(name = "是否执行完后自动关闭", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否执行完后自动关闭")
    private java.lang.Integer isAutoClose;
	/**创建人*/
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
