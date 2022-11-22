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
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@ApiModel(value="stk_io对象", description="出入库单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_io")
public class StkIo extends Bill {
    /**出入库类型*/
    @Excel(name = "出入库类型", width = 15, dicCode = "x_stock_io_type")
    @Dict(dicCode = "x_stock_io_type")
    @ApiModelProperty(value = "出入库类型")
    private java.lang.String stockIoType;
    /**是否有往来*/
    @Excel(name = "是否有往来", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有往来")
    private java.lang.Integer hasRp;
    /**是否有涨吨*/
    @Excel(name = "是否有涨吨", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有涨吨")
    private java.lang.Integer hasSwell;
    /**供应商*/
    @Excel(name = "供应商", width = 15, dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_supplier", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "供应商")
    private java.lang.String supplierId;
    /**客户*/
    @Excel(name = "客户", width = 15, dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bas_customer", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private java.lang.String customerId;
    /**发票类型*/
    @Excel(name = "发票类型", width = 15, dicCode = "x_invoice_type")
    @Dict(dicCode = "x_invoice_type")
    @ApiModelProperty(value = "发票类型")
    private java.lang.String invoiceType;
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
    /**出入库经办*/
    @Excel(name = "出入库经办", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "出入库经办")
    private java.lang.String handler;
    /**成本*/
    @Excel(name = "成本", width = 15)
    @ApiModelProperty(value = "成本")
    private java.math.BigDecimal cost;
    /**结算金额*/
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settleAmt;
    /**已结算金额*/
    @Excel(name = "已结算金额", width = 15)
    @ApiModelProperty(value = "已结算金额")
    private java.math.BigDecimal settledAmt;
    /**已开票金额*/
    @Excel(name = "已开票金额", width = 15)
    @ApiModelProperty(value = "已开票金额")
    private java.math.BigDecimal invoicedAmt;

    @Override
    public String getBillType() {
        return this.getClass().getSimpleName() + ":" + stockIoType;
    }

    /**是否退货*/
    public Integer getIsReturned() {
        //controller中，QueryWrapper也会创建entity；
        //(id == null)，是为了避免作为查询数据库参数。
        if (getId() == null) {
            return null;
        }
        return stockIoType == null ? null : (stockIoType.equals("1011") || stockIoType.equals("2011")) ? 1 : 0;
    }
}
