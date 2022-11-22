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
            <a-form-model-item label="退货出库单" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : '“供应商”是本处弹窗查询的参数！'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="stk_in_bill" :param="srcNoPopupParam"
                         org-fields="id,bill_no,supplier_id,invoice_type"
                         dest-fields="srcBillId,srcNo,supplierId,invoiceType"
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
              <a-input v-model="model.blueInvoiceNo" :readOnly="disabled"/>
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
          <a-tab-pane tab="采购退货出库明细" :key="refKeys[1]" :forceRender="true">
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
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange"
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

  export default {
    name: 'RubricPurInvoice111Form',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          isReturned: 1,
          srcBillType: 'StkIo:1011',
          srcBillId: '',
          srcNo: '',
          supplierId: '',
          invoiceType: '',
        },

        validatorRules: {
          srcNo: [{required: true, message: '请选择退货出库单!'}],
          blueInvoiceNo: [{required: true, message: '请输入蓝字发票号!'}],
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
              validateRules: [
                {required: true, message: '${title}不能为空'},
                {handler: this.rubricValidator },
                {handler: this.invoiceQtyValidator}],
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
              validateRules: [
                {required: true, message: '${title}不能为空'},
                {handler: this.rubricValidator },
                {handler: this.invoiceAmtValidator}],
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

        // 源单明细：入库单明细
        srcEntryTable: {
          loading: false,
          selectedRowCount: 0,
          dataSource: [],
          url: {
            list: '/stock/stkIo/queryEntryByMainId'
          },
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
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"160px",
              fixed: 'left',
              sortable: true,
            },
            {
              title: '规格型号',
              key: 'materialModel',
              type: JVXETypes.input,
              width:"200px",
              defaultValue:'',
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
            },
            {
              title: '结算数量',
              key: 'settleQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.selectSearch,
              dictCode:"x_tax_rate",
              width:"120px",
              align:"center",
              defaultValue:'',
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.input,
              width:"120px",
              align:"center",
            },
            {
              title: '税额',
              key: 'tax',
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
              title: '涨吨数量+/-',
              key: 'swellQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:0,
              statistics: ['sum'],
            },
            {
              title: '入库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              statistics: ['sum'],
            },
            {
              title: '采购费用',
              key: 'expense',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:0,
              statistics: ['sum'],
            },
            {
              title: '入库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: 0,
              statistics: ['sum'],
            },
            {
              title: '已开票数量',
              key: 'invoicedQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已开票金额',
              key: 'invoicedAmt',
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
            {
              title: '自定义1',
              key: 'custom1',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
            {
              title: '自定义2',
              key: 'custom2',
              type: JVXETypes.input,
              width:"100px",
              defaultValue:'',
            },
          ]
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
        const v = {stock_io_type: '1011', is_closed: 0};
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
      },

      onSrcNoPopupInput(val, row){
        this.$refs.srcNoFmi.onFieldChange();
        if (this.model.srcBillId === row.srcBillId) return;

        this.model.srcBillId = row.srcBillId;
        this.model.supplierId = row.supplierId;
        this.model.invoiceType = row.invoiceType;
        this.$refs.supplierIdFmi.onFieldChange();

        // 加载源单分录
        this.activeKey = this.refKeys[1];
        let params = { id: this.model.srcBillId };
        this.requestSubTableData(this.srcEntryTable.url.list, params, this.srcEntryTable);
      },

      handleAddFromSrcEntry(){
        for(let srcRow of this.$refs.srcEntryTable.selectedRows) {
          let row = {};
          row.srcBillType = this.model.srcBillType;
          row.srcBillId = srcRow.mid;
          row.srcEntryId = srcRow.id;
          row.srcNo = srcRow.billNo + ':' + srcRow.entryNo;
          row.materialId = srcRow.materialId;
          row.materialModel = srcRow.materialModel;
          row.unitId = srcRow.unitId;
          row.taxRate = srcRow.taxRate;
          row.qty = srcRow.settleQty - srcRow.invoicedQty;
          row.amt = srcRow.settleAmt - srcRow.invoicedAmt;
          this.$refs.entryTable.addRows(row);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        switch (column.property) {
          case "unitId":
            if (!oldValue || oldValue.length === 0 || !value || value.length === 0) break;
            const rate =this.getUnitRate(row.materialId, oldValue, value);
            let values = !!rate ? {qty: (row.qty * rate).toFixed(3)} : {unitId: ''};
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

      invoiceQtyValidator({cellValue, row, column}, callback, target) {
        const qty = Number(cellValue);
        if (isNaN(qty)) {
          callback();
          return;
        }

        let srcRec = this.srcEntryTable.dataSource.find(r => r.id === row.srcEntryId);
        if (!srcRec) {
          callback();
          return;
        }

        let srcSettleQty = Number(srcRec.settleQty);
        let srcInvoicedQty = Number(srcRec.invoicedQty);
        if (isNaN(srcSettleQty) || isNaN(srcInvoicedQty)) {
          callback();
          return;
        }
        const rate =this.getUnitRate(row.materialId, srcRec.unitId, row.unitId);
        if (qty + srcInvoicedQty * rate < srcSettleQty * rate) {
          callback(false, '开票数量不能超出未开票数量！');
        } else{
          callback(true);
        }
      },

      invoiceAmtValidator({cellValue, row, column}, callback, target) {
        const amt = Number(cellValue);
        if (isNaN(amt)) {
          callback();
          return;
        }

        let srcRec = this.srcEntryTable.dataSource.find(r => r.id === row.srcEntryId);
        if (!srcRec) {
          callback();
          return;
        }

        let srcSettleAmt = Number(srcRec.settleAmt);
        let srcInvoicedAmt = Number(srcRec.invoicedAmt);
        if (isNaN(srcSettleAmt) || isNaN(srcInvoicedAmt)) {
          callback();
          return;
        }
        if (amt + srcInvoicedAmt < srcSettleAmt) {
          callback(false, '开票金额不能超出未开票金额！');
        } else{
          callback(true);
        }
      },

   }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
