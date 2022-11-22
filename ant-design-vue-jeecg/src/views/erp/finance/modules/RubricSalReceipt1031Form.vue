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
              <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId">
                <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                  <j-search-select-tag v-model="model.customerId" :async="true" dict="bas_customer,aux_name,id"
                                       :disabled="disabled || entryTable.rowCount>0"/>
                </a-tooltip>
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
          <a-tab-pane tab="销售退货退款申请单" :key="refKeys[1]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[1]"
              :loading="srcTable.loading"
              :columns="srcTable.columns"
              :dataSource="srcTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="false"
              :toolbar="false"
              :resizable="true"
              :edit-config="{enabled: false, showIcon: false}"/>
          </a-tab-pane>

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
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove', 'clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @selectRowChange="({selectedRows}) => this.entryTable.selectedRowCount = selectedRows.length"
              @remove="event => {this.entryTable.rowCount = this.$refs.entryTable.getTableData().length; this.removeFreeSrcBills();}"
            >
              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="!model.customerId || model.customerId.length===0 ? '请先选择客户！':'客户是弹窗查询参数'"
                           placement="bottom">
                  <a-button @click="$refs.reqPopup.openModal()" icon="plus" type="primary"
                            :disabled="!model.customerId || model.customerId.length===0">销售退货退款申请单</a-button>
                </a-tooltip>
                <a-tooltip :title="entryTable.selectedRowCount!==1 ? '请选择一行明细!':''" placement="bottom">
                  <a-button @click="handleCopyAndAdd" icon="plus" :disabled="entryTable.selectedRowCount!==1">复制新增</a-button>
                </a-tooltip>

                <j-popup v-show="false" ref="reqPopup" code="fin_receipt_req" :param="reqPopupParam"
                         org-fields="id" dest-fields="id" :multi="true" @input="onReqPopupInput"/>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
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
  import { getRefPromise} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import { BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import { BillVxeTableMixin } from '../../common/mixins/BillVxeTableMixin'
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter";
  import pick from "lodash.pick";
  import XEUtils from "xe-utils";

  export default {
    name: 'RubricSalReceipt1031Modal',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo: '',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          srcBillType: '',
          srcBillId: '',
          srcNo: '',
          receiptType: '1031', //销售退货退款
          customerId:'',
          amt: 0,
          checkedAmt: 0,
        },

        validatorRules: {
          customerId: [{required: true, message: '请输入客户!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', 'srcTable'],
        tableKeys:['entryTable'],//用于校验和提交子表数据的方法getAllTable(),须与refkeys中位置相同
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          rowCount: 0,
          selectedRowCount: 0,
          dataSource: [],
          url: {
            list: '/finance/finReceipt/queryEntryByMainId'
          },
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
              title: '源单号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"180px",
              defaultValue: '',
              disabled: true,
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
              validateRules: [{ required: true, message: '${title}不能为空' },
                {handler: this.rubricValidator}, {handler: this.amtValidator}],
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

        // 源单：(红字)销售收款申请
        srcTable: {
          loading: false,
          dataSource: [],
          url: {
            list: '/finance/finReceiptReq/listByIds'
          },
          columns: [
            {
              title: '单据编号',
              key: 'billNo',
              type: JVXETypes.input,
              width:"180px",
              align:"center",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '单据日期',
              key: 'billDate',
              type: JVXETypes.date,
              width:"100px",
              align:"center",
              sortable: true,
            },
            {
              title: '单据主题',
              key: 'subject',
              type: JVXETypes.input,
              width:"200px",
              defaultValue:'',
              sortable: true,
            },
            {
              title: '申请金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已收金额',
              key: 'receivedAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '备注',
              key: 'remark',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
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

    computed: {
      reqPopupParam() {
        const v = {receipt_type: '1031', is_closed: 0};
        v.customer_id = this.model.customerId;
        return v;
      },
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_xsthtk_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id)
          this.requestSubDatas(this.entryTable, this.srcTable);
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finReceiptEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onReqPopupInput(val, row) {
        if(!row.id || row.id.length===0) return;
        const that = this;
        this.requestSrcDeltas(row.id, this.srcTable, null, success);

        function success(deltas) {
          const jxTable = that.$refs.entryTable;
          let entries = jxTable.getTableData();
          for (let srcBill of deltas.srcBills) {
            if (!entries.find(e => e.srcBillId === srcBill.id))
              jxTable.addRows({srcBillType: srcBill.billType, srcBillId: srcBill.id, srcNo: srcBill.billNo,
                amt: srcBill.amt - srcBill.receivedAmt}).then(
                ({row}) => jxTable.$refs.vxe.$refs.xTable.setActiveCell(row, 'amt'));
          }
        }
      },

      handleCopyAndAdd() {
        let jxTable = this.$refs.entryTable;
        if (jxTable.selectedRows.length !== 1) {
          this.$message.warning('请选择一行明细');
          return;
        }
        let row = pick(jxTable.selectedRows[0], 'srcBillType','srcBillId','srcNo','remark','custom1','custom2');
        jxTable.addRows(row);
      },

      amtValidator({cellValue, row, column}, callback, target) {
        let v = Number(cellValue);
        if (isNaN(v) || !row.srcBillId || row.srcBillId.length === 0) {
          callback();
          return;
        }

        let rows = target.getTableData().filter(r => r.srcBillId === row.srcBillId);
        v =  XEUtils.sum(rows, 'amt');
        let row1 = this.$refs.srcTable.getTableData().find(r => r.id === row.srcBillId);
        if (v < row1.amt - row1.receivedAmt) {
          callback(false, '${title}不能超出未收金额');
        } else {
          callback(true); //true：通过验证
        }
      },

    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
