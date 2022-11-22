<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus"/>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject"  :readOnly="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator"ref="operatorFmi">
              <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
              <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
                <j-search-select-tag v-model="model.customerId" :async="true" dict="bas_customer,aux_name,id" :disabled="true"/>
              </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="源单号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo">
              <a-input v-model="model.srcNo" :readOnly="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="amt">
              <a-input-number
                v-model="model.amt"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" v-show="action==='detail'">
            <a-form-model-item label="已核销金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checkedAmt">
              <a-input-number
                v-model="model.checkedAmt"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%"  :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-divider/>
        <bill-footer ref="billFooter" :model="model" :disabled="disabled" :action="action"/>
      </a-form-model>
    </div>

  </a-spin>
</template>

<script>

  import JFormContainer from '@/components/jeecg/JFormContainer'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import {BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import {BillNoTableMixin} from "../../common/mixins/BillNoTableMixin";
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";

  export default {
    name: 'SalReceivableForm',
    mixins: [BillFormMixin, BillFormGridMixin, BillNoTableMixin],
    components: {JFormContainer, JSelectUserByDep, BillHeader, BillFooter},

    data() {
      return {
        validatorRules: {},
        url: {},
     }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
