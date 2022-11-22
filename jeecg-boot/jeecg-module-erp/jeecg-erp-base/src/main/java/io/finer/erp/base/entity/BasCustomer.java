package io.finer.erp.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 客户
 * @Author: jeecg-boot
 * @Date:   2022-09-05
 * @Version: V1.0
 */
@Data
@TableName("bas_customer")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bas_customer对象", description="客户")
public class BasCustomer implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private java.lang.String id;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
    private java.lang.String code;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private java.lang.String name;
	/**简称*/
	@Excel(name = "简称", width = 15)
    @ApiModelProperty(value = "简称")
    private java.lang.String shortName;
	/**助记名*/
	@Excel(name = "助记名", width = 15)
    @ApiModelProperty(value = "助记名")
    private java.lang.String auxName;
	/**客户分类*/
	@Excel(name = "客户分类", width = 15, dicCode = "x_customer_category")
    @Dict(dicCode = "x_customer_category")
    @ApiModelProperty(value = "客户分类")
    private java.lang.String customerCategory;
	/**客户等级*/
	@Excel(name = "客户等级", width = 15, dicCode = "x_customer_level")
    @Dict(dicCode = "x_customer_level")
    @ApiModelProperty(value = "客户等级")
    private java.lang.String customerLevel;
	/**纳税规模*/
	@Excel(name = "纳税规模", width = 15, dicCode = "x_tax_scale")
    @Dict(dicCode = "x_tax_scale")
    @ApiModelProperty(value = "纳税规模")
    private java.lang.String taxScale;
	/**欠款额度*/
	@Excel(name = "欠款额度", width = 15)
    @ApiModelProperty(value = "欠款额度")
    private java.math.BigDecimal creditQuota;
	/**所属总公司*/
	@Excel(name = "所属总公司", width = 15)
    @ApiModelProperty(value = "所属总公司")
    private java.lang.String headquarters;
	/**所属地区*/
	@Excel(name = "所属地区", width = 15)
    @ApiModelProperty(value = "所属地区")
    private java.lang.String area;
	/**业务区域*/
	@Excel(name = "业务区域", width = 15)
    @ApiModelProperty(value = "业务区域")
    private java.lang.String bizArea;
	/**客户地址*/
	@Excel(name = "客户地址", width = 15)
    @ApiModelProperty(value = "客户地址")
    private java.lang.String address;
	/**客户网站*/
	@Excel(name = "客户网站", width = 15)
    @ApiModelProperty(value = "客户网站")
    private java.lang.String website;
	/**法人代表*/
	@Excel(name = "法人代表", width = 15)
    @ApiModelProperty(value = "法人代表")
    private java.lang.String legalPerson;
	/**法人电话*/
	@Excel(name = "法人电话", width = 15)
    @ApiModelProperty(value = "法人电话")
    private java.lang.String legalPersonPhone;
	/**财务信息联系人*/
	@Excel(name = "财务信息联系人", width = 15)
    @ApiModelProperty(value = "财务信息联系人")
    private java.lang.String financialContacts;
	/**财务信息联系电话*/
	@Excel(name = "财务信息联系电话", width = 15)
    @ApiModelProperty(value = "财务信息联系电话")
    private java.lang.String financialPhone;
	/**开票信息单位名称*/
	@Excel(name = "开票信息单位名称", width = 15)
    @ApiModelProperty(value = "开票信息单位名称")
    private java.lang.String invoiceCompany;
	/**开票信息税号*/
	@Excel(name = "开票信息税号", width = 15)
    @ApiModelProperty(value = "开票信息税号")
    private java.lang.String invoiceTaxCode;
	/**开票信息开户行*/
	@Excel(name = "开票信息开户行", width = 15)
    @ApiModelProperty(value = "开票信息开户行")
    private java.lang.String invoiceBankName;
	/**开票信息银行账号*/
	@Excel(name = "开票信息银行账号", width = 15)
    @ApiModelProperty(value = "开票信息银行账号")
    private java.lang.String invoiceBankCode;
	/**开票信息账号*/
	@Excel(name = "开票信息账号", width = 15)
    @ApiModelProperty(value = "开票信息账号")
    private java.lang.String invoiceAccount;
	/**开票信息联系电话*/
	@Excel(name = "开票信息联系电话", width = 15)
    @ApiModelProperty(value = "开票信息联系电话")
    private java.lang.String invoicePhone;
	/**开票地址*/
	@Excel(name = "开票地址", width = 15)
    @ApiModelProperty(value = "开票地址")
    private java.lang.String invoiceAddress;
	/**办款资料单位名称*/
	@Excel(name = "办款资料单位名称", width = 15)
    @ApiModelProperty(value = "办款资料单位名称")
    private java.lang.String paymentCompany;
	/**办款资料开户行*/
	@Excel(name = "办款资料开户行", width = 15)
    @ApiModelProperty(value = "办款资料开户行")
    private java.lang.String paymentBankName;
	/**办款资料行号*/
	@Excel(name = "办款资料行号", width = 15)
    @ApiModelProperty(value = "办款资料行号")
    private java.lang.String paymentBankCode;
	/**办款资料账号*/
	@Excel(name = "办款资料账号", width = 15)
    @ApiModelProperty(value = "办款资料账号")
    private java.lang.String paymentAccount;
	/**收件信息收件人*/
	@Excel(name = "收件信息收件人", width = 15)
    @ApiModelProperty(value = "收件信息收件人")
    private java.lang.String recvName;
	/**收件信息联系电话*/
	@Excel(name = "收件信息联系电话", width = 15)
    @ApiModelProperty(value = "收件信息联系电话")
    private java.lang.String recvPhone;
	/**收件信息传真*/
	@Excel(name = "收件信息传真", width = 15)
    @ApiModelProperty(value = "收件信息传真")
    private java.lang.String recvFax;
	/**recvEmail*/
	@Excel(name = "recvEmail", width = 15)
    @ApiModelProperty(value = "recvEmail")
    private java.lang.String recvEmail;
	/**收件信息地址*/
	@Excel(name = "收件信息地址", width = 15)
    @ApiModelProperty(value = "收件信息地址")
    private java.lang.String recvAddress;
	/**收件信息邮编*/
	@Excel(name = "收件信息邮编", width = 15)
    @ApiModelProperty(value = "收件信息邮编")
    private java.lang.String recvPostcode;
	/**附件*/
	@Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private java.lang.String attachment;
	/**启用*/
	@Excel(name = "启用", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "启用")
    private java.lang.Integer isEnabled;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
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
	@Excel(name = "版本", width = 15)
    @ApiModelProperty(value = "版本")
    private java.lang.Integer version;
}
