<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus"/>
        <a-row v-show="moreStatus">
          <a-col :span="8">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId" ref="supplierIdFmi">
              <a-input v-if="disabled" v-model="model.supplierId_dictText" :readOnly="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.supplierId" :disabled="entryTable.rowCount>0"
                                     :async="true" dict="bas_supplier,aux_name,id" @change="onSupplierChange"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="蓝字单据" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : '“供应商”是本处弹窗查询的参数！'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="fin_pur_invoice" :param="srcNoPopupParam"
                         org-fields="id,bill_no,invoice_type,invoice_no,supplier_id"
                         dest-fields="srcBillId,srcNo,invoiceType,blueInvoiceNo,supplierId"
                         @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" :disabled="true" dictCode="x_invoice_type"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="蓝字发票号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="blueInvoiceNo">
              <a-input v-model="model.blueInvoiceNo" :readOnly="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="发票号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceNo">
              <a-input v-model="model.invoiceNo" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="开票日期" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceDate">
              <j-date v-model="model.invoiceDate" :readOnly="disabled" placeholder="请选择日期" style="width: 100%"
                      :allowClear="false" :inputReadOnly="true" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="蓝单明细" :key="refKeys[1]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[1]"
              :loading="srcEntryTable.loading"
              :columns="entryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="true"
              :toolbar="!disabled"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :checkbox-config="{checkMethod: srcEntryCheckboxMethod}"
              :edit-config="{showIcon: false}"
              @selectRowChange="({selectedRows}) => this.srcEntryTable.selectedRowCount = selectedRows.length">

              <template v-if="!disabled" v-slot:toolbarSuffix>
                <a-button :disabled="srcEntryTable.selectedRowCount===0" @click="handleAddFromSrcEntry">
                  添加<a-icon type="right"/>
                </a-button>
              </template>
            </j-vxe-table>
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
              :toolbar-config="{btn: ['remove', 'clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="event => {this.entryTable.rowCount++;}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
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
  import { getRefPromise} from '@/components/jeecg/JVxeTable/utils/vxeUtils.js'
  import {BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import {BillVxeTableMixin} from "../../common/mixins/BillVxeTableMixin";
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter";
  import pick from "lodash.pick";

  export default {
    name: 'RubricPurInvoice110Form',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          isReturned: 0,
          srcBillType: 'FinPurInvoice:100',//invoiceType是发票类型不是登记单据类型！
          srcBillId: '',
          srcNo: '',
          supplierId: '',
          invoiceType: '',
          blueInvoiceNo: '',
        },

        validatorRules: {
          srcNo: [{required: true, message: '请选择蓝字单据!'}],
          invoiceNo: [{required: true, message: '请输入发票号!'}],
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', 'srcEntryTable'],
        tableKeys:['entryTable', ],//用于校验和提交子表数据的方法getAllTable(),须与refkeys中位置相同
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          url: {list: '/finance/finPurInvoice/queryEntryByMainId'},
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '源分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"180px",
              align:"center",
              fixed: 'left',
              defaultValue: '',
              disabled: true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"160px",
              fixed: 'left',
              defaultValue: '',
              disabled: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"200px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.input,
              width:"80px",
              align:"center",
              defaultValue: '',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_unit,name,id",
              width:"90px",
              align:"center",
              defaultValue: '',
              options:[],
              disabled: true,
            },
            {
              title: '开票数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, { handler: this.rubricRangeValidator}],
              statistics: ['sum'],
            },
            {
              title: '开票金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width: "100px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, { handler: this.rubricRangeValidator}],
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

        //蓝字明细
        srcEntryTable: {
          loading: false,
          dataSource: [],
          selectedRowCount: 0,
          url: {list: '/finance/finPurInvoice/queryEntryByMainId'},
        },

        url: {
          add: "/finance/finPurInvoice/add",
          edit: "/finance/finPurInvoice/edit",
          check: "/finance/finPurInvoice/check",
          ebpm: "/finance/finPurInvoice/bpm/end",
          execute: "/finance/finPurInvoice/execute",
          void: "/finance/finPurInvoice/void",
        },
      }
    },

    computed: {
      srcNoPopupParam() {
        const v = {is_rubric: 0};
        v.supplier_id = this.model.supplierId;
        return v;
      },
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('fin_cgfp_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id) {
          let params = { id: this.model.id }
          let that = this;
          this.requestSubTableData(this.entryTable.url.list, params, this.entryTable, success)

          function success(){
            that.entryTable.rowCount = that.entryTable.dataSource.length;
            let params = { id: that.model.srcBillId }
            that.requestSubTableData(that.srcEntryTable.url.list, params, that.srcEntryTable);
          }
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          finPurInvoiceEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onSupplierChange(val) {
        this.model.srcNo = '';
        this.model.srcBillId = '';
        this.model.invoiceType = '';
        this.model.blueInvoiceNo = '';
      },

      onSrcNoPopupInput(val, row){
        this.$refs.srcNoFmi.onFieldChange();
        if (this.model.srcBillId === row.srcBillId) return;

        this.model.srcBillId = row.srcBillId;
        this.model.supplierId = row.supplierId;
        this.model.invoiceType = row.invoiceType;
        this.model.blueInvoiceNo = row.blueInvoiceNo;
        this.$refs.supplierIdFmi.onFieldChange();

        // 加载源单分录
        this.activeKey = this.refKeys[1];
        let params = { id: this.model.srcBillId };
        this.requestSubTableData(this.srcEntryTable.url.list, params, this.srcEntryTable);
      },

      handleAddFromSrcEntry(){
        for(let row1 of this.$refs.srcEntryTable.selectedRows) {
          let row0 = pick(row1, 'entryNo','materialId','materialModel','unitId','taxRate');
          row0.srcBillType = this.model.srcBillType;
          row0.srcBillId = this.model.srcBillId;
          row0.srcEntryId = row1.id;
          row0.srcNo = this.model.srcNo + ':' + row1.entryNo;
          row0.qty = -row1.qty;
          row0.amt = -row1.amt;
          this.$refs.entryTable.addRows(row0);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

   }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
