<template>
  <a-spin :spinning="confirmLoading">
    <div>
    <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus"/>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" @change="onOperatorChange"
                                      :disabled="disabled || entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept"
                                 dictCode="sys_depart,depart_name,org_code" :disabled="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_depart,depart_name,org_code,(id IN (SELECT dept_id FROM sys_user_dept WHERE username='${model.operator}'))` "
                                   :disabled="entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.supplierId" :async="true" dict="bas_supplier,aux_name,id"
                                     :disabled="disabled || entryTable.rowCount>0"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" v-show="action==='detail'">
            <a-form-model-item label="已付金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="paidAmt">
              <a-input-number v-model="model.paidAmt" :disabled="true"
                              :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                              :precision="2" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="采购订单" :key="refKeys[1]" :forceRender="true">
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
              @remove="event => {this.entryTable.rowCount = this.$refs.entryTable.getTableData().length; this.removeFreeSrcBills();}"
            >
              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="tipTitleOfAdd" placement="bottom">
                  <a-button @click="$refs.orderPopup.openModal()" icon="plus" type="primary" :disabled="addDisabled">采购订单</a-button>
                </a-tooltip>
                <j-popup v-show="false" ref="orderPopup" code="pur_order" :param="orderPopupParam"
                         org-fields="id" dest-fields="id" :multi="true" @input="onOrderPopupInput"/>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? '':entryTable.editExcludeCols"
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
  import XEUtils from "xe-utils";

  export default {
    name: 'PurPrepayment2011ReqForm',
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
          srcBillId:  '',
          srcNo:'',
          paymentType: '2011',
          operator: '',
          opDept: '',
          supplierId:'',
        },

        validatorRules: {
          supplierId: [{required: true, message: '请输入供应商!'}],
          operator: [{required: true, message: '请输入业务员!'}],
          opDept: [{required: true, message: '请输入业务部门!'}],
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
          dataSource: [],
          url: {list: '/finance/finPaymentReq/queryEntryByMainId'},
          editExcludeCols: 'paidAmt',
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
              width:"200px",
              defaultValue:'',
              fixed: 'left',
              disabled: true,
              sortable: true,
            },
            {
              title: '申请金额',
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
              title: '已付金额',
              key: 'paidAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              disabled: true,
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

        // 源单：采购订单
        srcTable: {
          loading: false,
          dataSource: [],
          url: {list: '/purchase/purOrder/listByIds'},
          columns: [
           {
              title: '单据编号',
              key: 'billNo',
              type: JVXETypes.input,
              width:"160px",
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
              title:'单据主题',
              key: 'subject',
              type: JVXETypes.input,
              width:"200px",
              align:"left",
              sortable: true,
            },
            {
              title: '源单号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              align:"center",
              sortable: true,
            },
            {
              title: '采购类型',
              key: 'purType',
              type: JVXETypes.select,
              dictCode:"x_pur_type",
              width:"100px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '业务部门',
              key: 'opDept',
              type: JVXETypes.select,
              dictCode:"sys_depart,depart_name,org_code",
              width:"120px",
              align:"center",
              sortable: true,
            },
            {
              title: '业务员',
              key: 'operator',
              type: JVXETypes.select,
              dictCode:"sys_user,realname,username",
              width:"90px",
              align:"center",
              sortable: true,
            },
            {
              title: '采购金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '预付余额',
              key: 'prepaymentBal',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '结算金额',
              key: 'settleAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已结算金额',
              key: 'settledAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '付款方式',
              key: 'paymentMethod',
              type: JVXETypes.select,
              dictCode:"x_payment_method",
              width:"100px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '结算方式',
              key: 'paymentMethod',
              type: JVXETypes.select,
              dictCode:"x_settle_method",
              width:"120px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '交货日期',
              key: 'deliveryTime',
              type: JVXETypes.date,
              width:"100px",
              defaultValue:'',
              sortable: true,
            },
            {
              title: '交货方式',
              key: 'deliveryMethod',
              type: JVXETypes.select,
              dictCode:"x_delivery_method",
              width:"120px",
              align:"center",
              defaultValue: '',
              options:[],
            },
            {
              title: '交货地点',
              key: 'deliveryPlace',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
              sortable: true,
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
          add: "/finance/finPaymentReq/add",
          edit: "/finance/finPaymentReq/edit",
          check: "/finance/finPaymentReq/check",
          ebpm: "/finance/finPaymentReq/bpm/end",
          execute: "/finance/finPaymentReq/execute",
          void: "/finance/finPaymentReq/void",
        },
      }
    },


    computed: {
      tipTitleOfAdd() {
        if (!this.model.supplierId || this.model.supplierId.length === 0
          || !this.model.operator || this.model.operator.length === 0
          || !this.model.opDept || this.model.opDept.length === 0)
          return "请先选择业务员、业务部门和供应商！";

        if (this.entryTable.rowCount > 0)  return "只能添加一个采购订单！";

        return "业务员、业务部门和供应商是弹窗查询参数";
      },

      addDisabled() {
        return !this.model.supplierId || this.model.supplierId.length === 0
          || !this.model.operator || this.model.operator.length === 0
          || !this.model.opDept || this.model.opDept.length=== 0
          || this.entryTable.rowCount > 0;
      },

      orderPopupParam() {
        const v = {is_closed: 0};
        v.supplier_id = this.model.supplierId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },
    },

    created() {
      if (!this.disabled)
        this.hideColumns(this.entryTable.editExcludeCols);
      else
        this.restoreColumnsType(this.entryTable.editExcludeCols);
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_cgyfsq_bill_no');
      },

      editAfter() {
        if (this.model.id)
          this.requestSubDatas(this.entryTable, this.srcTable);
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finPaymentReqEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onOrderPopupInput(val, row) {
        if(!row.id || row.id.length===0) return;
        const that = this;
        this.requestSrcDeltas(row.id, this.srcTable, null, success);

        function success(deltas) {
          const jxTable = that.$refs.entryTable;
          let entries = jxTable.getTableData();
          for (let srcBill of deltas.srcBills) {
            if (!entries.find(e => e.srcBillId === srcBill.id))
              jxTable.addRows({srcBillType: srcBill.billType, srcBillId: srcBill.id, srcNo: srcBill.billNo,
                amt: srcBill.amt - srcBill.prepaymentBal - srcBill.settledAmt}).then(
                ({row}) => jxTable.$refs.vxe.$refs.xTable.setActiveCell(row, 'amt'));
          }
        }
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
        if (v > row1.amt - row1.prepaymentBal - row1.settledAmt) {
          callback(false, '${title}不能大于（采购金额 - 预付余额 - 已结算金额）');
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