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
            <a-col :span="8">
              <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
                <j-search-select-tag v-model="model.customerId" :disabled="disabled" :async="true" dict="bas_customer,aux_name,id"/>
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

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="明细" :key="refKeys[0]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[0]"
              :loading="entryTable.loading"
              :columns="entryTable.columns"
              :dataSource="entryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="!disabled"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="onEntryAdded"
            />
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns"
              style="float: right;"/>
          </template>
        </a-tabs>

        <bill-footer ref="billFooter" :model="model" :disabled="disabled" :action="action"/>
      </a-form-model>
    </div>

  </a-spin>
</template>

<script>

  import { JVxeTableModelMixin } from '@/mixins/JVxeTableModelMixin'
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { getRefPromise,VALIDATE_FAILED} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import { BillVxeTableMixin } from '../../common/mixins/BillVxeTableMixin'
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter";

  export default {
    name: 'OtherReceiptForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

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
          receiptType: '192', //其他收款
          amt: 0,
          checkedAmt: 0,
        },

        validatorRules: {
          customerId: [{required: true, message: '请输入客户!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 1,
        refKeys: ['entryTable', ],
        tableKeys:['entryTable', ],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          url: {list: '/finance/finReceipt/queryEntryByMainId'},
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [
                { required: true, message: '${title}不能为空' },
                { pattern: /^[1-9]\d*$/, message: '${title}须为正整数' },
                { unique: true, message: '${title}不能重复' },
              ],
            },
            {
              title: '结算方式',
              key: 'settleMethod',
              type: JVXETypes.select,
              dictCode:"x_settle_method",
              width:"150px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '资金账户',
              key: 'bankAccountId',
              type: JVXETypes.select,
              dictCode:"bas_bank_account,account_no,id",
              width:"260px",
              defaultValue: '',
              options:[],
            },
            {
              title: '金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator}],
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"160px",
              defaultValue: '',
            },
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"100px",
              defaultValue: '',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"100px",
              defaultValue: '',
            },
          ]
        },
        url: {
          add: "/finance/finReceipt/add",
          edit: "/finance/finReceipt/edit",
          check: "/finance/finReceipt/check",
          ebpm: "/finance/finReceipt/bpm/end",
          execute: "/finance/finReceipt/execute",
          void: "/finance/finReceipt/void",
        },
     }
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_qtsk_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id)
          this.requestSubTableData(this.entryTable.url.list, { id: this.model.id }, this.entryTable);
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finReceiptEntryList: allValues.tablesValue[0].tableData,
        }
      },

    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
