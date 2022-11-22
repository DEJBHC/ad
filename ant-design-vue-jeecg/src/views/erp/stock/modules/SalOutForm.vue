<template>
  <a-spin :spinning="confirmLoading">
    <!-- 主表单区域 -->
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <bill-header ref="billHeader" :model="model" :disabled="disabled" :moreStatus.sync="moreStatus" :isRubricDisabled="true"/>

        <a-row v-show="moreStatus">
          <a-col :span="8">
            <a-form-model-item label="有应收" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasRp">
              <j-dict-select-tag v-model="model.hasRp" dictCode="yn" :disabled="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-tooltip title="销售出库：出库数量+涨吨数量=结算数量" placement="top">
              <a-form-model-item label="有涨吨" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="hasSwell">
                <j-dict-select-tag v-model="model.hasSwell" dictCode="yn" @change="onHasSwellChange" :disabled="disabled"/>
              </a-form-model-item>
            </a-tooltip>
          </a-col>
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
          <a-col :span="8" >
            <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-select-user-by-dep v-model="model.operator" :multi="false" :disabled="disabled || entryTable.rowCount>0"
                                      @change="val =>{this.resetSrc(); this.onOperatorChange(val); }"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="业务部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="opDept" ref="opDeptFmi">
              <j-dict-select-tag v-if="disabled"  v-model="model.opDept"
                                 dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : model.operator && model.operator.length>0 ? '' : '请先选择业务员！'" placement="bottom">
                <j-dict-select-tag ref="opDept"  v-model="model.opDept" placeholder="请选择"
                                   :dictCode="`sys_depart,depart_name,org_code,(id IN (SELECT dept_id FROM sys_user_dept WHERE username='${model.operator}'))` "
                                   :disabled="entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8" >
            <a-form-model-item label="客户" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="customerId" ref="customerIdFmi">
              <a-tooltip :title="!disabled && entryTable.rowCount>0 ? '有明细时不能改变！' : ''" placement="bottom">
                <j-search-select-tag v-model="model.customerId" :async="true" dict="bas_customer,aux_name,id"
                                     :disabled="disabled || entryTable.rowCount>0" @change="val => resetSrc()"/>
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="销售订单" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="srcNo" ref="srcNoFmi">
              <a-input v-if="disabled" v-model="model.srcNo" :readOnly="true"/>
              <a-tooltip v-else :title="entryTable.rowCount>0 ? '有明细时不能改变！' : '业务员、业务部门和客户是弹窗查询参数'" placement="bottom">
                <j-popup v-model="model.srcNo" :disabled="entryTable.rowCount > 0"
                         field="srcNo" code="sal_order" :param="srcNoPopupParam"
                         org-fields="id,bill_no,customer_id,op_dept,operator,invoice_type"
                         dest-fields="srcBillId,srcNo,customerId,opDept,operator,invoiceType"
                @input="onSrcNoPopupInput" />
              </a-tooltip>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="发票类型" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="invoiceType">
              <j-dict-select-tag v-model="model.invoiceType" dictCode="x_invoice_type"
                                 :disabled="!!model.srcNo && model.srcNo.length > 0"/>
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
          <a-tab-pane tab="销售订单明细" :key="refKeys[1]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[1]"
              :loading="srcEntryTable.loading"
              :columns="srcEntryTable.columns"
              :dataSource="srcEntryTable.dataSource"
              :maxHeight="300"
              :rowNumber="false"
              :rowSelection="true"
              :toolbar="!disabled"
              :resizable="true"
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['clearSelection']}"
              :checkbox-config="{checkMethod: srcEntryCheckboxMethod}"
              :edit-config="{enabled: false, showIcon: false}"
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
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove','clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.entryTable)"
              @added="event => {this.entryTable.rowCount++; this.onEntryAdded(event);}"
              @selectRowChange="({selectedRows}) => this.entryTable.selectedRowCount = selectedRows.length"
              @remove="event => this.entryTable.rowCount = this.$refs.entryTable.getTableData().length"
              @valueChange="onEntryValueChange">

              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-tooltip :title="tipTitleOfAdd" placement="bottom">
                  <a-button :disabled="addDisabled" type="primary" icon="plus"
                            @click="() => $refs.entryTable.addRows({})">新增</a-button>
                </a-tooltip>
                <a-tooltip :title="entryTable.selectedRowCount!==1 ? '请选择一行明细!':''" placement="bottom">
                  <a-button @click="handleCopyAndAdd" icon="plus" :disabled="entryTable.selectedRowCount!==1">复制新增</a-button>
                </a-tooltip>
              </template>
              <template v-if="!disabled" v-slot:toolbarSuffix>
                <p style="float: right;">提示：“物料、仓库”是“批次”查询的参数！</p>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter v-show="activeKey==='entryTable'"
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns"
              :excluded-cols="disabled ? entryTable.notEditExcludeCols:entryTable.editExcludeCols"
              ignored-cols="swellQty" style="float: right;"/>
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
    name: 'SalOutForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {BillHeader, BillFooter, VxeTableColumnsSetter},

    data() {
      return {
        model: {//设置初始值的属性、程序赋值的响应式属性
          billNo: '',
          billDate: new Date().format('yyyy-MM-dd'),
          isAuto: 0,
          isRubric: 0,
          srcBillType: '',
          srcBillId:  '',
          srcNo:'',
          stockIoType: '201', //销售出库
          hasRp: 1,
          hasSwell: 0,
          customerId: '',
          operator: '',
          opDept: '',
          invoiceType: '',
        },

        validatorRules: {
          customerId: [{required: true, message: '请选择客户!'}],
          invoiceType: [{required: true, message: '请选择发票类型！'}]
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
          selectedRowCount: 0,
          url: {
            list: '/stock/stkIo/queryEntryByMainId',
            editingList: '/stock/stkIo/queryEditingEntryByMainId'
          },
          editExcludeCols: 'invoicedQty,invoicedAmt',
          notEditExcludeCols: 'inventoryUnitId,inventoryQty,inventoryCost',
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
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"180px",
              fixed: 'left',
              defaultValue: '',
              disabled:true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              options:[],
              width:"150px",
              fixed: 'left',
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
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
              defaultValue: '2',
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"200px",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.popup,
              popupCode: 'stk_inventory_batch',
              orgFields: "material_id,material_model,warehouse_id,batch_no,unit_id,qty,cost",
              destFields: "materialId,materialModel,warehouseId,batchNo,inventoryUnitId,inventoryQty,inventoryCost",
              paramFields: "materialId,warehouseId",
              field: 'batchNo',
              width:"230px",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '库存单位',
              key: 'inventoryUnitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              options:[],
              disabled:true,
              width:"90px",
              align:"center",
            },
            {
              title: '库存数量',
              key: 'inventoryQty',
              type: JVXETypes.input,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              statistics: ['sum'],
            },
            {
              title: '库存金额',
              key: 'inventoryCost',
              type: JVXETypes.inputNumber,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              statistics: ['sum'],
            },
            {
              title: '出库单位',
              key: 'unitId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_unit,name,id",
              options:[],
              width:"90px",
              align:"center",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '出库数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator},
                {handler: this.stkOutQtyValidator}],
              statistics: ['sum'],
            },
            {
              title: '出库金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              disabled: true,
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
              title: '结算数量',
              key: 'settleQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              disabled: true,
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.selectSearch,
              dictCode:"x_tax_rate",
              width:"80px",
              align:"center",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"90px",
              align:"center",
              placeholder: '请输入',
              defaultValue:100,
              validateRules: [{required: true, message: '${title}不能为空'}, {pattern: /^[1-9]\d*$/, message: '${title}须为正整数'}],
            },
            {
              title: '税额',
              key: 'tax',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatAmt,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{required: true, message: '${title}不能为空'}, {handler: this.taxValidator}],
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
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.settleAmtValidator}],
              statistics: ['sum'],
            },
            {
              title: '已开票数量',
              key: 'invoicedQty',
              type: JVXETypes.inputNumber,
              disabled:true,
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
              disabled:true,
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

        //源单明细：销售订单明细
        srcEntryTable: {
          loading: false,
          dataSource: [],
          selectedRowCount: 0,
          url: {list: '/sale/salOrder/queryEntryByMainId'},
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"70px",
              align:"center",
              fixed: 'left',
              sortable: true,
              defaultValue:'',
            },
            {
              title: '源单类型',
              key: 'srcBillType',
              type: JVXETypes.select,
              dictCode:"x_bill_type",
              width:"120px",
              defaultValue:'',
              fixed: 'left',
              sortable: true,
            },
            {
              title: '源单分录号',
              key: 'srcNo',
              type: JVXETypes.input,
              width:"160px",
              defaultValue:'',
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
              defaultValue: '',
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
              title: '数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: '',
              statistics: ['sum'],
            },
            {
              title: '税率%',
              key: 'taxRate',
              type: JVXETypes.inputNumber,
              width:"80px",
              align:"center",
              defaultValue: '',
            },
            {
              title: '含税单价',
              key: 'price',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
            },
            {
              title: '折扣率%',
              key: 'discountRate',
              type: JVXETypes.inputNumber,
              width:"90px",
              align:"center",
              defaultValue:100,
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
              title: '含税金额',
              key: 'amt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              statistics: ['sum'],
            },
            {
              title: '已出库数量',
              key: 'outQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue:'',
              statistics: ['sum'],
            },
            {
              title: '已出库金额',
              key: 'outCost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
              statistics: ['sum'],
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
              title: '结算金额',
              key: 'settledAmt',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue:'',
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
        const v = {is_closed: 0};
        v.customer_id = this.model.customerId;
        v.operator = this.model.operator;
        v.op_dept = this.model.opDept;
        return v;
      },

      tipTitleOfAdd() {
        if (!this.model.customerId || this.model.customerId.length === 0) return "请先选择客户！";
        //!!model.srcNo: 须使用!!, 将表达式强制转化为bool
        if (!!this.model.srcNo && this.model.srcNo.length > 0) return "请从订单明细中选择添加！";
        return "";
      },

      addDisabled() {
        return !this.model.customerId || this.model.customerId.length === 0
          || !!this.model.srcNo && this.model.srcNo.length > 0;
      },
    },

    created() {
      if (this.disabled) {
        this.hideColumns(this.entryTable.notEditExcludeCols);
        this.restoreColumnsType(this.entryTable.editExcludeCols);
      }
      else {
        this.hideColumns(this.entryTable.editExcludeCols);
        this.restoreColumnsType(this.entryTable.notEditExcludeCols);
        this.initMaterialRelated();
      }

    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.srcEntryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_xsck_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        setTimeout(()=>this.onHasSwellChange(this.model.hasSwell.toString()), 1500);

        // 加载子表数据
        if (this.model.id) {
          let that = this;
          let url = this.disabled ? this.entryTable.url.list : this.entryTable.url.editingList;
          this.requestSubTableData(url, {id: this.model.id}, this.entryTable, success);

          function success(){
            that.entryTable.rowCount = that.entryTable.dataSource.length;
            if (!!that.model.srcBillId && that.model.srcBillId.length>0)
              that.requestSubTableData(that.srcEntryTable.url.list, {id: that.model.srcBillId}, that.srcEntryTable);
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
        if (Number(this.model.hasSwell) === 1) {
          xTable.showColumn(xTable.getColumnByField('swellQty'));
        } else {
          xTable.hideColumn(xTable.getColumnByField('swellQty'));

          let rows = jxTable.getTableData(); //新增行无id
          rows = rows.concat(jxTable.getNewDataWithId()); //新增行有临时id
          for (let row of rows) {
            if (row.id) jxTable.setValues([{rowKey: row.id, values: {swellQty: 0, settleQty: row.qty}}]);
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

        this.model.srcBillType = 'SalOrder';
        //JPopup的v-model模式只支持一个值返回给当前组件
        this.model.srcBillId = row.srcBillId;
        this.model.operator = row.operator;
        this.model.opDept = row.opDept;
        this.model.customerId = row.customerId;
        this.model.invoiceType = row.invoiceType;
        this.$refs.customerIdFmi.onFieldChange();

        // 加载源单分录
        this.activeKey = this.refKeys[1];
        this.requestSubTableData(this.srcEntryTable.url.list, {id: row.srcBillId}, this.srcEntryTable)
      },

      resetSrc() {
        this.model.srcBillType = '';
        this.model.srcBillId = '';
        this.model.srcNo = '';
        this.model.invoiceType = '';
        this.srcEntryTable.dataSource = [];
        this.activeKey = this.refKeys[0];
      },

      handleAddFromSrcEntry(){
        for(let srcRow of this.$refs.srcEntryTable.selectedRows) {
          let row = pick(srcRow, 'materialId','materialModel','unitId','taxRate','price','discountRate');
          row.srcBillType = this.model.srcBillType;
          row.srcBillId = srcRow.mid;
          row.srcEntryId = srcRow.id;
          row.srcNo = srcRow.billNo + ':' + srcRow.entryNo;

          row.qty = srcRow.qty - srcRow.settleQty;

          row.swellQty = 0;
          row.settleQty = row.qty;
          row.tax = Number((srcRow.tax * row.settleQty / srcRow.qty).toFixed(2));
          row.settleAmt = srcRow.amt - srcRow.settleAmt;
          this.$refs.entryTable.addRows(row);
        }
        this.$refs.srcEntryTable.clearSelection();
      },

      handleCopyAndAdd() {
        let jxTable = this.$refs.entryTable;
        let row = pick(jxTable.selectedRows[0], 'srcBillType','srcBillId','srcEntryId','srcNo',
          'materialId','materialModel','unitId','taxRate','price','discountRate',
          'remark','custom1','custom2');
        jxTable.addRows(row);
      },

      onEntryValueChange(event) {
        // ·JVXETypes.popup只有当前列会触发valueChange，destFields中其他列不会
        // ·event中的row已为新值（包括popup的destFields各列）
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        // 库存批次batchNo相同，但inventoryId可能不同（不同仓库、不同物料可能同batchNo）
        if (value === oldValue && column.property !== 'batchNo' || isSetValues) return;

        const emptyKeys = 'batchNo,inventoryUnitId,inventoryQty,inventoryCost,cost';
        let values = {};
        switch (column.property) {
          case 'materialId':
            if (row.srcNo && row.srcNo.length > 0 ) {
              this.$message.warning("有“源单分录号”不能改变物料！")
              target.setValues([{rowKey: row.id, values: {materialId: oldValue}}]);
            }
            else {
              this.handleMaterialChange(row, target, emptyKeys);
            }
            break;
          case 'warehouseId':
            this.emptyColumns(row, emptyKeys, target);
            break;
          case 'batchNo':
            if (!row.batchNo || row.batchNo.length === 0) {
              this.emptyColumns(row, emptyKeys, target);
              break;
            }

            if (!row.unitId || row.unitId.length === 0) {
              values.unitId = row.inventoryUnitId;
              values.cost = this.calcCost(row, 1);
            }
            else {
              values.cost = this.calcCost(row);
              //batchNo改变带出的inventoryUnitId变化，可能会导致与unitId不能转

              if (values.cost === null) values.unitId = ''; //注意：不能!values.cost，因为!0 = true
            }
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case 'unitId':
            //unitId变化需重新计算cost（使用unitId与inventoryUnitId的rate），如：
            //  unitId开始为千克，计算出cost；然后清空unitId，此时qty和cost未变；unitId设为吨，此时就应重新计算。
            if (value && value.length > 0) {
              values.cost = this.calcCost(row);
              if (values.cost === null) {//注意：不能!values.cost，因为!0 = true
                //unitId新值不合法：与 inventoryUnitId不能转换，恢复原值，cost保持上次的不变
                target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
                break;
              }
            }
            else values.cost = '';

            if (!oldValue || oldValue.length === 0 || !value || value.length === 0) {
              target.setValues([{rowKey: row.id, values: values}]);
              break;
            }

            const rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate)
              //unitId新值不合法：与原值不能转换，恢复原值，cost及其他联动列保持上次的不变
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else {
              values = {};
              values.qty = (row.qty * rate).toFixed(3);
              values.swellQty = (row.swellQty * rate).toFixed(3);
              values.settleQty = Number(values.qty) + Number(values.swellQty);
              values.price = (row.price / rate).toFixed(2);
              target.setValues([{rowKey: row.id, values: values}]);
            }
            break;
          case 'qty':
            values = {};
            values.cost = this.calcCost(row);
            values.settleQty = Number(row.qty) + Number(row.swellQty);
            values.tax = this.calcTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "swellQty":
            values = {};
            values.settleQty = Number(row.qty) + Number(row.swellQty);
            values.tax = this.calcTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "price":
          case "discountRate":
            values = {};
            values.tax = this.calcTax(row);
            values.settleAmt = this.calcSettleAmt(row);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
          case "taxRate":
            target.setValues([{rowKey: row.id, values: {tax: this.calcTax(row)}}]);
            break;
        }
      },

      calcCost(row, rate) {
        if (!rate) rate = this.getUnitRate(row.materialId, row.unitId, row.inventoryUnitId);
        if (!rate) return null;

        const inventoryQty = Number(row.inventoryQty);
        if (isNaN(inventoryQty) || inventoryQty === 0) return 0;

        const inventoryCost = Number(row.inventoryCost), qty = Number(row.qty);
        const cost = inventoryCost * qty * rate / inventoryQty;
        return Number(cost.toFixed(2));
      },

      //ValueChange事件中调用的函数calcXxx，不能直接使用计算属性的原因：
      // 1、计算属性ValueChange时一般不进行处理（避免循环触发）
      // 2、计算属性的基础属性ValueChange时，调用calcXxx（row）的row中计算属性还是oldValue
      calcTax(row) {
        const qty = Number(row.qty), swellQty = Number(row.swellQty);
        const settleQty = qty + swellQty;
        const price = Number(row.price), discountRate = Number(row.discountRate), taxRate = Number(row.taxRate);
        const tax = settleQty * (price * discountRate / 100) * taxRate / (100 + taxRate);
        return Number(tax.toFixed(2));
      },

      calcSettleAmt(row) {
        const qty = Number(row.qty), swellQty = Number(row.swellQty);
        const settleQty = qty + swellQty;
        const price = Number(row.price), discountRate = Number(row.discountRate);
        const settleAmt = settleQty * (price * discountRate) / 100;
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
