package io.finer.erp.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.finer.erp.common.entity.Bill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: sal_quot
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="sal_quot对象", description="sal_quot")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_quot")
public class SalQuot extends Bill {
    /**业务部门*/
    @Excel(name = "业务部门", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "业务部门")
    private java.lang.String opDept;
    /**业务员*/
    @Excel(name = "业务员", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "业务员")
    private String operator;
    /**是否临时客户*/
    @Excel(name = "是否临时客户", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否临时客户")
    private Integer isTempCustomer;
    /**客户名称*/
    @Excel(name = "客户名称", width = 15)
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    /**客户*/
    @Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private String customerId;
    /**付款方式*/
    @Excel(name = "付款方式", width = 15, dicCode = "x_payment_method")
    @Dict(dicCode = "x_payment_method")
    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;
    /**交货地点*/
    @Excel(name = "交货地点", width = 15)
    @ApiModelProperty(value = "交货地点")
    private String deliveryPlace;
    /**联系人*/
    @Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private String contact;
    /**联系电话*/
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String phone;
    /**传真*/
    @Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private String fax;
    /**电子邮件*/
    @Excel(name = "电子邮件", width = 15)
    @ApiModelProperty(value = "电子邮件")
    private String email;
    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
    /**金额*/
    @Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amt;
}
