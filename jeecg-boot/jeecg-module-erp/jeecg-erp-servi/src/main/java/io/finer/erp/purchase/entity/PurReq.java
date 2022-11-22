package io.finer.erp.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 采购申请单
 * @Author: jeecg-boot
 * @Date:   2022-05-30
 * @Version: V1.0
 */
@ApiModel(value="pur_req对象", description="采购申请单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_req")
public class PurReq extends Bill {
	/**采购类型*/
    @Excel(name = "采购类型", width = 15, dicCode = "x_pur_type")
    @Dict(dicCode = "x_pur_type")
    @ApiModelProperty(value = "采购类型")
    private java.lang.String purType;
    /**需求部门*/
    @Excel(name = "需求部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "需求部门")
    private java.lang.String requestDept;
	/**需求人*/
	@Excel(name = "需求人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "需求人")
    private java.lang.String requester;
	/**需求时间*/
	@Excel(name = "需求时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "需求时间")
    private java.util.Date requestTime;
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**参考金额*/
    @Excel(name = "参考金额", width = 15)
    @ApiModelProperty(value = "参考金额")
    private java.math.BigDecimal amt;
    /**已订购数量*/
    @Excel(name = "已订数量", width = 15)
    @ApiModelProperty(value = "已订数量")
    private java.math.BigDecimal orderedQty;
}
