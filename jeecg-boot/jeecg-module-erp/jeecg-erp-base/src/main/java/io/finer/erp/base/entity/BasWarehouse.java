package io.finer.erp.base.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date:   2020-04-01
 * @Version: V1.0
 */
@Data
@TableName("bas_warehouse")
public class BasWarehouse implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**父级*/
	@Excel(name = "父级", width = 15)
	private java.lang.String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15)
	private java.lang.String hasChild;
	/**编码*/
	@Excel(name = "编码", width = 15)
	private java.lang.String code;
	/**名称*/
	@Excel(name = "名称", width = 15)
	private java.lang.String name;
	/**助记名*/
	@Excel(name = "助记名", width = 15)
	@ApiModelProperty(value = "助记名")
	private java.lang.String auxName;
	/**电话*/
	@Excel(name = "电话", width = 15)
	private java.lang.String phone;
	/**是否启用*/
	@Excel(name = "是否启用", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
	private java.lang.Integer isEnabled;
	/**备注*/
	@Excel(name = "备注", width = 15)
	private java.lang.String remark;
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
	/**版本*/
	@ApiModelProperty(value = "版本")
	private java.lang.Integer version;
}
