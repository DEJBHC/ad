<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="助记名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="auxName">
              <a-input v-model="model.auxName" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="客户分类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerCategory">
              <j-dict-select-tag v-model="model.customerCategory" dictCode="x_customer_category" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="客户等级" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerLevel">
              <j-dict-select-tag v-model="model.customerLevel" dictCode="x_customer_level" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="纳税规模" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="taxScale">
              <j-dict-select-tag v-model="model.taxScale" dictCode="x_tax_scale" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="欠款额度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="creditQuota">
              <a-input-number
                v-model="model.creditQuota" placeholder="请输入" style="width: 100%" :disabled="disabled"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="启用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isEnabled">
              <j-dict-select-tag v-model="model.isEnabled" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-textarea v-model="model.remark" :disabled="disabled" rows="1" autoSize></a-textarea>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="1" header="开票信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceCompany">
                  <a-input v-model="model.invoiceCompany" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="税号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceTaxCode">
                  <a-input v-model="model.invoiceTaxCode" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="开户行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceBankName">
                  <a-input v-model="model.invoiceBankName" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="行号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceBankCode">
                  <a-input v-model="model.invoiceBankCode" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceAccount">
                  <a-input v-model="model.invoiceAccount" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoicePhone">
                  <a-input v-model="model.invoicePhone" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="开票地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="invoiceAddress">
                  <a-input v-model="model.invoiceAddress" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="办款资料" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="单位名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentCompany">
                  <a-input v-model="model.paymentCompany" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="开户行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentBankName">
                  <a-input v-model="model.paymentBankName" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="行号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentBankCode">
                  <a-input v-model="model.paymentBankCode" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentAccount">
                  <a-input v-model="model.paymentAccount" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="3" header="收件信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="收件人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvName">
                  <a-input v-model="model.recvName" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvPhone">
                  <a-input v-model="model.recvPhone" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="传真" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvFax">
                  <a-input v-model="model.recvFax" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="Email" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvEmail">
                  <a-input v-model="model.recvEmail" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvAddress">
                  <a-input v-model="model.recvAddress" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="邮编" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="recvPostcode">
                  <a-input v-model="model.recvPostcode" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="4" header="财务信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="财务信息联系人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="financialContacts">
                  <a-input v-model="model.financialContacts" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="财务信息联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="financialPhone">
                  <a-input v-model="model.financialPhone" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="9" header="其他信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="简称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="shortName">
                  <a-input v-model="model.shortName" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="辅助名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="auxName">
                  <a-input v-model="model.auxName" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="客户网站" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="website">
                  <a-input v-model="model.website" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="法人代表" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="legalPerson">
                  <a-input v-model="model.legalPerson" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="法人电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="legalPersonPhone">
                  <a-input v-model="model.legalPersonPhone" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="所属总公司" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="headquarters">
                  <a-input v-model="model.headquarters" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="所属地区" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="area">
                  <j-area-linkage type="cascader" v-model="model.area" placeholder="请选择" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="业务区域" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bizArea">
                  <j-area-linkage type="cascader" v-model="model.bizArea" placeholder="请选择" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="客户地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
                  <a-input v-model="model.address" placeholder="请输入" :disabled="disabled"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="attachment">
                  <j-upload v-model="model.attachment" :disabled="disabled"></j-upload>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="0" header="操作信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
                  <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
                  <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="修改时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
                  <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="修改人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
                  <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </div>
  </a-spin>
</template>

<script>
  import { validateDuplicateValue } from '@/utils/util'
  import {FormMixin} from '../../common/mixins/FormMixin'

  export default {
    name: 'BasCustomerForm',
    mixins: [FormMixin],

    data () {
      return {
        model: {
          code: '',
          name: '',
          auxName: '',
          isEnabled: 1,
        },

        validatorRules: {
          code: [
            { required: true, message: '请输入编码!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_customer', 'code', value, this.model.id, callback) }
          ],
          name: [
            { required: true, message: '请输入名称!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_customer', 'name', value, this.model.id, callback) }
          ],
          auxName: [
            { required: true, message: '请输入助记名!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_customer', 'aux_name', value, this.model.id, callback) }
          ],
          isEnabled: [{ required: true, message: '请选择是否启用!'},],
          creditQuota: [{ pattern: /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: '请输入正确的金额!'},],
          recvEmail: [{ pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!'},],
          recvPostcode: [{ pattern: /^[1-9]\d{5}$/, message: '请输入正确的邮政编码!'},],
        },
        url: {
          add: "/base/basCustomer/add",
          edit: "/base/basCustomer/edit",
          queryById: "/base/basCustomer/queryById"
        }
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
