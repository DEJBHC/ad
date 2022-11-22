<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="accountNo">
              <a-input v-model="model.accountNo" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="账户名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="币种" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="currency">
              <j-dict-select-tag v-model="model.currency" dictCode="bas_currency,name,code" placeholder="请选择":disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="行号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankNo">
              <a-input v-model="model.bankNo" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="银行地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankAddress">
              <a-input v-model="model.bankAddress" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="启用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isEnabled">
              <j-dict-select-tag v-model="model.isEnabled" dictCode="yn" placeholder="请选择":disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="账户管理员" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="manager">
              <j-dict-select-tag v-model="model.manager" dictCode="sys_user,realname,username" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="10" header="操作信息" :style="collapseStyle">
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
    name: "BasBankAccountForm",
    mixins: [FormMixin],

    data () {
      return {
        model: {
          currency: 'CNY',
          isEnabled: 1
        },
        validatorRules: {
          accountNo: [
            {required: true, message: '请输入账号!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bank_account', 'account_no', value, this.model.id, callback) }
          ],
          name: [
            {required: true, message: '请输入账户名!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bank_account', 'name', value, this.model.id, callback) }
          ],
          currency: [{required: true, message: '请输入币种!'},],
          isEnabled: [{ required: true, message: '请选择是否启用!'},],
        },
        url: {
          add: "/base/basBankAccount/add",
          edit: "/base/basBankAccount/edit",
        }
      }
    },

    computed: {
      readOnly: function() {
        return this.action !== "add" && this.action !== "edit"
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
