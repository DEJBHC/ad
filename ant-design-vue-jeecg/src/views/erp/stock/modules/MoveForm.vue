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
            <a-form-model-item label="调拨经办" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="handler">
              <j-select-user-by-dep v-model="model.handler" :multi="false" :disabled="disabled"/>
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
              :toolbar-config="{slots: ['prefix', 'suffix'], btn: ['remove','clearSelection']}"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @added="onEntryAdded"
              @valueChange="onEntryValueChange"
              @selectRowChange="refreshAddDisabled">

              <template v-if="!disabled" v-slot:toolbarPrefix>
                <a-button type="primary" icon="plus"  @click="handleAddOut">新增调出</a-button>
                <a-tooltip :title="addDisabled ? '请选择一条有批次的调出分录':''" placement="bottom">
                  <a-button type="primary" icon="plus" @click="handleCopyAndAdd" :disabled="addDisabled">复制新增调入</a-button>
                </a-tooltip>
              </template>
            </j-vxe-table>
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="entryTable.columns" :excluded-cols="disabled ? entryTable.notEditExcludeCols:''"
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
  import pick from "lodash.pick";
  import XEUtils from "xe-utils";

  export default {
    name: 'MoveForm',
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
          stockIoType: '301', //库存调拨
          hasRp: 0,
          hasSwell: 0,
        },

        validatorRules: {},

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', ],
        tableKeys:['entryTable', ],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          url: {
            list: '/stock/stkIo/queryEntryByMainId',
            editingList: '/stock/stkIo/queryEditingEntryByMainId'
          },
          notEditExcludeCols: 'inventoryQty,inventoryCost',
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
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              options:[],
              width:"150px",
              fixed: 'left',
              placeholder: '请输入',
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
              type: JVXETypes.select,
              dictCode:"x_stock_io_direction",
              width:"80px",
              align:"center",
              defaultValue: '',
              options:[],
              disabled: true,
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.selectSearch,
              options:[],
              dictCode:"bas_warehouse,aux_name,id",
              width:"200px",
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.popup,
              popupCode: 'stk_inventory_batch',
              orgFields: "material_id,material_model,warehouse_id,batch_no,unit_id,qty,cost",
              destFields: "materialId,materialModel,warehouseId,batchNo,unitId,inventoryQty,inventoryCost",
              paramFields: "materialId,warehouseId",
              field: 'batchNo',
              width:"230px",
              placeholder: '请选择',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }],
            },
            {
              title: '单位',
              key: 'unitId',
              type: JVXETypes.select,
              dictCode:"bas_unit,name,id",
              disabled:true,
              width:"100px",
              align:"center",
            },
            {
              title: '库存数量',
              key: 'inventoryQty',
              type: JVXETypes.input,
              disabled:true,
              width:"100px",
              align:"right",
              formatter: this.formatQty,
            },
            {
              title: '库存金额',
              key: 'inventoryCost',
              type: JVXETypes.inputNumber,
              disabled:true,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
            },
            {
              title: '调拨数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              placeholder: '请输入',
              defaultValue:'',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator},
                {handler: this.qtyValidator}],
            },
            {
              title: '调拨金额',
              key: 'cost',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatAmt,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }, {handler: this.rubricValidator},
                {handler: this.costValidator}],
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
        addDisabled: true,
      }
    },

    created() {
      if (this.disabled)
        this.hideColumns(this.entryTable.notEditExcludeCols);
      else {
        this.restoreColumnsType(this.entryTable.notEditExcludeCols);
        this.initMaterialRelated();
      }
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_kcdb_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        const that = this;
        if (this.model.id) {
          const url = this.readOnly ? this.entryTable.url.list : this.entryTable.url.editingList;
          this.requestSubTableData(url, { id: this.model.id }, this.entryTable, success);
        }

        function success() {
          for(let rec of that.entryTable.dataSource) {
            if (rec.stockIoDirection==='1') //调入
              rec.unitCost = rec.qty === 0 ? 0 : Number((rec.cost / rec.qty).toFixed(2)); //能保存，说明cost和qty通过了验证
              //调入分录的materialId、batchNo等要与调出分录相同，但batchNo的popup会改变或清空这些列，oldValues用于恢复
              rec.oldValues = pick(rec, 'materialId','materialModel','batchNo','unitId','warehouseId');
          }
          if (that.entryTable.dataSource.length > 0)
            that.entryTable.dataSource = [...that.entryTable.dataSource];
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkIoEntryList: allValues.tablesValue[0].tableData,
        }
      },

      handleAddOut() {
        this.$refs.entryTable.addRows({stockIoDirection: '2'});
      },

      handleCopyAndAdd() {
        const srcRow = this.$refs.entryTable.selectedRows[0];
        const row = pick(srcRow, 'materialId','materialModel','batchNo','unitId','qty','cost');
        row.stockIoDirection = '1';
        row.unitCost = srcRow.inventoryQty === 0 ? 0 : Number((srcRow.inventoryCost / srcRow.inventoryQty).toFixed(2));
        //调入分录的materialId、batchNo等要与调出分录相同，但batchNo的popup会改变或清空这些列，oldValues用于恢复
        row.oldValues = pick(row, 'materialId','materialModel','batchNo','unitId');
        this.$refs.entryTable.addRows(row);
      },

      onEntryValueChange(event) {
        // ·JVXETypes.popup只有当前列会触发valueChange，destFields中其他列不会
        // ·event中的row已为新值（包括popup的destFields各列）
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        // 库存批次batchNo相同，但inventoryId可能不同（不同仓库、不同物料可能同batchNo）
        if (value === oldValue && column.property !== 'batchNo' || isSetValues) return;

        const emptyKeys = 'batchNo,unitId,inventoryQty,inventoryCost,cost';
        switch (column.property) {
          case 'materialId':
            if (row.stockIoDirection === '2')
              this.handleMaterialChange(row, target, emptyKeys);
            else{
              this.$message.warning("调入分录不能改变物料！")
              target.setValues([{rowKey: row.id, values: {materialId: oldValue}}]);
            }
            break;
          case 'warehouseId':
            if (row.stockIoDirection === '2')
              this.emptyColumns(row, emptyKeys, target);
            else
              row.oldValues.warehouseId = row.warehouseId;
            break;
          case 'batchNo':
            if (row.stockIoDirection === '2') {
              this.refreshAddDisabled();
              if (!row.batchNo || row.batchNo.length === 0) {
                this.emptyColumns(row, emptyKeys, target);
                break;
              }
              target.setValues([{rowKey: row.id, values: {cost: this.calcCost(row)}}]);
            }
            else{
              this.$message.warning("调入分录不能改变批次！")
              target.setValues([{rowKey: row.id, values: row.oldValues}]);
            }
            break;
          case 'qty':
            target.setValues([{rowKey: row.id, values: {cost: this.calcCost(row)}}]);
            break;
        }
      },

      refreshAddDisabled() {
        const rows = this.$refs.entryTable.selectedRows;
        this.addDisabled = rows.length !== 1
          || rows[0].stockIoDirection !== '2'
          || !rows[0].batchNo || rows[0].batchNo.length === 0;
      },

      calcCost(row) {
        const inventoryQty = Number(row.inventoryQty);
        if (row.stockIoDirection==='2' && inventoryQty === 0) return 0;

        const inventoryCost = Number(row.inventoryCost);
        const qty = Number(row.qty), unitCost = Number(row.unitCost);
        const cost = row.stockIoDirection==='2' ? inventoryCost * qty / inventoryQty : qty * unitCost;
        return Number(cost.toFixed(2));
      },

      qtyValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (!row.batchNo || row.batchNo.length===0 || isNaN(v)) {
          callback();
          return;
        }

        let rows;
        if (row.stockIoDirection==='2') {
          rows = this.$refs.entryTable.getTableData();
          rows = rows.filter(r => r.stockIoDirection==='2' && r.materialId===row.materialId && r.batchNo===row.batchNo);
          if (XEUtils.sum(rows, 'qty') > row.inventoryQty){
            callback(false, '该仓库批次的调出数量合计不能大于库存数量！');
            return;
          }
        }

        rows = this.$refs.entryTable.getTableData();
        rows = rows.filter(r => r.materialId === row.materialId && r.batchNo === row.batchNo);
        let qty = 0;
        for(let r of rows) {
          qty += r.stockIoDirection==='2' ? r.qty : -r.qty;
        }
        if (Math.abs(qty) > 0.001) {
          callback(false, '该物料批次的调出调入数量不相等！');
          return;
        }

        callback(true); //true：通过验证
      },

      costValidator({cellValue, row, column}, callback, target) {
        const v = Number(cellValue);
        if (!row.batchNo || row.batchNo.length===0 || isNaN(v)) {
          callback();
          return;
        }

        const diff = v - this.calcCost(row);
        if (diff < -0.01001 || diff > 0.01001) {
          callback(false, '${title}的输入值与计算值相差超过0.01元！');
          return;
        }

        let rows;
        if (row.stockIoDirection==='2') {
          rows = this.$refs.entryTable.getTableData();
          rows = rows.filter(r => r.stockIoDirection==='2' && r.materialId===row.materialId && r.batchNo===row.batchNo);
          if (XEUtils.sum(rows, 'cost') > row.inventoryCost){
            callback(false, '该仓库批次的调出金额合计不能大于库存金额！');
            return;
          }
        }

        rows = this.$refs.entryTable.getTableData();
        rows = rows.filter(r => r.materialId === row.materialId && r.batchNo === row.batchNo);
        let cost = 0;
        for(let r of rows) {
          cost += r.stockIoDirection==='2' ? r.cost : -r.cost;
        }
        if (Math.abs(cost) > 0.01) {
          callback(false, '该物料批次的调出调入金额不相等！');
          return;
        }

        callback(true); //true：通过验证
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
