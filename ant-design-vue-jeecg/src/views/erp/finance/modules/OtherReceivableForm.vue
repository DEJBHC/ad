<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus"/>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator"ref="operatorFmi">
              <j-select-user-by-dep v-model="model.operator" :multi="false" @change="onOperatorChange" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <a-tooltip :title="model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_depart,depart_name,org_code,(id IN (SELECT dept_id FROM sys_user_dept WHERE username='${model.operator}'))` "
                                   :disabled="disabled"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
              <j-search-select-tag v-model="model.customerId" :disabled="disabled" :async="true" dict="bas_customer,aux_name,id"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="amt">
              <a-input-number v-model="model.amt" :disabled="disabled"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" v-show="action==='detail'">
            <a-form-model-item label="已核销金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checkedAmt">
              <a-input-number v-model="model.checkedAmt" :disabled="true"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%"/>
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
  import {BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import {BillNoTableMixin} from "../../common/mixins/BillNoTableMixin";
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";

  export default {
    name: 'OtherReceivableForm',
    mixins: [BillFormMixin, BillFormGridMixin, BillNoTableMixin],
    components: {BillHeader, BillFooter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          srcBillType: '',
          srcBillId: '',
          srcNo: '',
          receivableType: '199', //其他应收
          operator: '',
          opDept: '',
          checkedAmt: 0,
        },

        validatorRules: {
          customerId: [{required: true, message: '请输入客户!'}],
          amt: [{required: true, message: '请输入金额!'}, {validator: this.rubricFormModelValidator}],
        },

        url: {
          add: "/finance/finReceivable/add",
          edit: "/finance/finReceivable/edit",
          check: "/finance/finReceivable/check",
          ebpm: "/finance/finReceivable/bpm/end",
          execute: "/finance/finReceivable/execute",
          void: "/finance/finReceivable/void",
        },
      }
    },

    methods: {
      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_qtar_bill_no');
      },
    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
