<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :isRubricDisabled="true"/>

        <a-row v-show="moreStatus">
          <a-col :span="8">
            <a-form-model-item label="有应付" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="有涨吨" :labelCol="{span:15}" :wrapperCol="{span:9}" prop="hasSwell">
              <j-dict-select-tag v-model="model.hasSwell" dictCode="yn" @change="onHasSwellChange" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="源单类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcBillType">
              <j-dict-select-tag v-model="model.srcBillType" dictCode="x_bill_type"  :disabled="true"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="8" >
            <a-form-model-item label="单据主题" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="subject">
              <a-input v-model="model.subject" placeholder="请输入" :readOnly="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="源单业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有退货明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="disabled || entryTable.rowCount>0"
                                      @change="val => {this.resetSrc(); this.onOperatorChange(val); }"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有退货明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_depart,depart_name,org_code,(id IN (SELECT dept_id FROM sys_user_dept WHERE username='${model.operator}'))` "
                                   :disabled="entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="供应商" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="supplierId" ref="supplierIdFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有退货明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.supplierId" :async="true" dict="bas_supplier,aux_name,id"
                                     :disabled="disabled || entryTable.rowCount>0"  @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="采购入库单" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true" />
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有退货明细时不能改变！' : '“业务员、业务部门和供应商”是弹窗查询的参数！'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="stk_in_bill" :param="srcNoPopupParam"
                         org-fields="id,bill_no,supplier_id,operator,op_dept,has_swell,invoice_type"
                         dest-fields="srcBillId,srcNo,supplierId,operator,opDept,hasSwell,invoiceType"
                         @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" dictCode="x_invoice_type" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="出库经办" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="handler">
              <j-select-user-by-dep v-model="model.handler" :multi="false" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" v-if="action==='detail'">
            <a-form-model-item label="已结算金额" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="settledAmt">
              <a-input-number v-model="model.settledAmt" :disabled="true"
                              :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                              :precision="2" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="采购入库明细" :key="refKeys[1]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[1]"
              :loading="srcEntryTable.loading"
              :columns="srcEntryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :disabled="true"
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

          <a-tab-pane tab="退货出库明细" :key="refKeys[0]" :forceRender="true">
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
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? '':entryTable.editExcludeCols" ignored-cols="swellQty"
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

  export default {
    name: 'PurInForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo:'',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 1,
          srcBillType:'StkIo:101', //采购入库
          srcBillId:  '',
          stockIoType: '1011', //采购退货出库
          hasRp: 1,
          hasSwell: 0,
          srcNo:'',
          supplierId:'',
          operator: '',
          opDept: '',
          invoiceType: '',
        },

        validatorRules: {
          supplierId: [{required: true, message: '请选择供应商!'}],
          srcNo: [{required: true, message: '请选择采购入库单!'}]
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', 'srcEntryTable', ],
        tableKeys:['entryTable', ], //用于校验和提交子表数据的方法getAllTable(),须与refkeys中位置相同
        activeKey: 'entryTable',

        // 采购退货出库明细
        entryTable: {
          loading: false,
          dataSource: [],
          rowCount: 0,
          url: {list: '/stock/stkIo/queryEntryByMainId'},
          editExcludeCols: 'invoicedQty,invoicedAmt',
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
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"180px",
              fixed: 'left',
              defaultValue: '',
              disabled: true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"150px",
              fixed: 'left',
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
              title: '出入方向',
              key: 'stockIoDirection',
              type: JVXETypes.hidden,
              defaultValue: '1',
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.select,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"150px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.input,
              width:"230px",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_unit,name,id",
              width:"100px",
              align:"center",
              disabled: true,
            },
            {
              title: '结算数量',
              key: 'settleQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, { handler: this.rubricRangeValidator}],
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.select,
              dictCode:"x_tax_rate",
              width:"80px",
              align:"center",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              disabled: true,
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"center",
              defaultValue:'',
              disabled: true,
            },
            {
              title: '税额',
              key: 'tax',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{required: true, message: '${title}不能为空'}, { handler: this.rubricRangeValidator},
                {handler: this.taxValidator}],
              statistics: ['sum'],
            },
            {
              title: '结算金额',
              key: 'settleAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, { handler: this.rubricRangeValidator},
                { handler: this.settleAmtValidator}],
              statistics: ['sum'],
            },
            {
              title: '涨吨数量+/-',
              key: 'swellQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
            },
            {
              title: '入库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              disabled: true,
              statistics: ['sum'],
            },
            {
              title: '采购费用',
              key: 'expense',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
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
              disabled: true,
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
              disabled:true,
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
              disabled:true,
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

        //原采购入库单
        srcEntryTable: {
          loading: false,
          dataSource: [],
          selectedRowCount: 0,
          url: {list: '/stock/stkIo/queryEntryByMainId'},
          columns: [],
        },

        url: {
          add: "/stock/stkIo/add",
          edit: "/stock/stkIo/edit",
          check: "/stock/stkIo/check",
          ebpm: "/stock/stkIo/bpm/end",
          execute: "/stock/stkIo/execute",
          void: "/stock/stkIo/void",
        },

      }
    },

    computed: {
      srcNoPopupParam() {
        const v = {stock_io_type: '101'};
        v.supplier_id = this.model.supplierId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },
    },

    created() {
      for(let col of this.entryTable.columns) {
        let col2 = Object.assign({}, col)
        delete col2.validateRules;
        this.srcEntryTable.columns.push(col2);
      }

      if (!this.disabled)
        this.hideColumns(this.entryTable.editExcludeCols);
      else
        this.restoreColumnsType(this.entryTable.editExcludeCols);
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource=[];
      },

       addAfter() {
        this.$refs.billHeader.fillBillNo('stk_cgrk_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key));
        return Promise.all(values);
      },

      editAfter() {
        setTimeout(()=>this.onHasSwellChange(this.model.hasSwell.toString()), 1500);
        this.activeKey = this.refKeys[!this.model.id ? 1 : 0];

        if (this.model.id) {
          let params = { id: this.model.id };
          let that = this;
          this.requestSubTableData(this.entryTable.url.list, params, this.entryTable, success);

          function success(){
            that.entryTable.rowCount = that.entryTable.dataSource.length;
            let params = { id: that.model.srcBillId };
            that.requestSubTableData(that.srcEntryTable.url.list, params, that.srcEntryTable);
          }
        }
      },

       classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      onHasSwellChange(val) {
        const jxTable = this.$refs.entryTable;
        const xTable = jxTable.$refs.vxe.$refs.xTable;
        const xTable2 = this.$refs.srcEntryTable.$refs.vxe.$refs.xTable;
        if (Number(this.model.hasSwell) === 1) {
          xTable.showColumn(xTable.getColumnByField('swellQty'));
          xTable2.showColumn(xTable2.getColumnByField('swellQty'));
        } else {
          xTable.hideColumn(xTable.getColumnByField('swellQty'));
          xTable2.hideColumn(xTable2.getColumnByField('swellQty'));

          let rows = jxTable.getTableData(); //新增行无id
          rows = rows.concat(jxTable.getNewDataWithId()); //新增行有临时id
          for (let row of rows) {
            if (row.id) jxTable.setValues([{rowKey: row.id, values: {swellQty: 0, qty: row.settleQty}}]);
          }
        }
      },

      onSrcNoPopupInput(val, row){
        this.$refs.srcNoFmi.onFieldChange();

        if (this.model.srcBillId === row.srcBillId) return;
        if (!row.srcNo || row.srcNo.length===0) {
          this.resetSrc();
          return;
        }

        //JPopup的v-model模式只支持一个值返回给当前组件
        this.model.srcBillId = row.srcBillId;
        this.model.supplierId = row.supplierId;
        this.model.invoiceType = row.invoiceType;
        this.model.operator = row.operator;
        this.model.opDept = row.opDept;
        this.model.hasSwell = row.hasSwell;
        this.$refs.supplierIdFmi.onFieldChange();
        this.onHasSwellChange(this.model.hasSwell.toString());

        // 加载源单分录
        this.activeKey = this.refKeys[1];
        this.requestSubTableData(this.srcEntryTable.url.list, { id: this.model.srcBillId }, this.srcEntryTable);
      },

      resetSrc(val) {
        this.model.srcBillId = '';
        this.model.srcNo = '';
        this.model.invoiceType = '';
        this.srcEntryTable.dataSource = [];
      },

      handleAddFromSrcEntry(){
        for(let row1 of this.$refs.srcEntryTable.selectedRows) {
          let row0 = pick(row1, 'entryNo','materialId','materialModel','warehouseId','batchNo','unitId','taxRate','price','discountRate');
          row0.srcBillType = this.model.srcBillType;
          row0.srcBillId = this.model.srcBillId;
          row0.srcEntryId = row1.id;
          row0.srcNo = this.model.srcNo + ':' + row1.entryNo;
          row0.settleQty = -row1.settleQty;
          row0.tax = -row1.tax;
          row0.settleAmt = -row1.settleAmt;
          row0.swellQty = -row1.swellQty;
          row0.qty = -row1.qty;
          row0.expense = -row1.expense;
          row0.cost = -row1.cost;
          this.$refs.entryTable.addRows(row0);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        let values = {};
        switch (column.property) {
          case "settleQty":
            values = {};
            values.tax = this.calcTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            values.qty = Number(row.settleQty) + Number(row.swellQty);
            values.cost = Number(values.settleAmt) + Number(row.expense);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "settleAmt":
          case "expense":
            values = {};
            values.cost = Number(row.settleAmt) + Number(row.expense);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "swellQty":
            values = {};
            values.qty = Number(row.settleQty) + Number(row.swellQty);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

      //ValueChange事件中调用的函数calcXxx，不能直接使用计算属性的原因：
      // 1、计算属性ValueChange时一般不进行处理（避免循环触发）
      // 2、计算属性的基础属性ValueChange时，调用calcXxx（row）的row中计算属性还是oldValue
      calcTax(row) {
        const settleQty = Number(row.settleQty), price = Number(row.price),
          discountRate = Number(row.discountRate), taxRate = Number(row.taxRate);
        const tax = settleQty * (price * discountRate / 100) * taxRate / (100 + taxRate);
        return Number(tax.toFixed(2));
      },

      calcSettleAmt(row) {
        const settleQty = Number(row.settleQty), price = Number(row.price), discountRate = Number(row.discountRate);
        let settleAmt = settleQty * (price * discountRate / 100);
        return Number(settleAmt.toFixed(2));
      },

      taxValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (isNaN(v)) {
          callback();
          return;
        }

        let diff = v - this.calcTax(row);
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！');
        } else {
          callback(true); //true：通过验证
        }
      },

      settleAmtValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (isNaN(v)) {
          callback();
          return;
        }

        let diff = v - this.calcSettleAmt(row);
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！');
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
