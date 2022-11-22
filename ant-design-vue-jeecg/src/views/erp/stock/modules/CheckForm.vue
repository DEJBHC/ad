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
            <a-form-model-item label="盘点经办" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="checker">
              <j-select-user-by-dep v-model="model.checker" :multi="false" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-divider orientation="left" style="font-size: 14px; color: #bfbfbf;">盘点范围</a-divider>
        <a-row>
          <a-col :span="8">
            <a-form-model-item label="仓库" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="warehouseId">
              <j-tree-select v-model="model.warehouseId" dict="bas_warehouse,aux_name,id" pidValue="0"
                             hasChildField="has_child" placeholder="请选择" :disabled="action!=='add'"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="物料分类" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="materialCategoryId">
              <j-tree-select v-model="model.materialCategoryId" dict="bas_material_category,name,id" pidValue="0"
                             hasChildField="has_child" placeholder="请选择" :disabled="action!=='add'"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-button v-show="action==='add'" @click="handleCheckRangeOk" :disabled="!isCheckRangeChange" type="primary" style="margin: 5px 0 0 36px">确定</a-button>
          </a-col>
        </a-row>

        <!-- 子表单区域 -->
        <a-tabs v-model="activeKey" @change="handleChangeTabs">
          <a-tab-pane tab="账存批次" :key="refKeys[0]" :forceRender="true">
            <j-vxe-table
              v-show="!isCheckRangeChange"
              keep-source
              :ref="refKeys[0]"
              :loading="entryTable.loading"
              :columns="entryTable.columns"
              :dataSource="entryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="false"
              :toolbar="false"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @valueChange="onEntryValueChange"
            />
          </a-tab-pane>

          <a-tab-pane tab="新批次" :key="refKeys[1]" :forceRender="true">
            <j-vxe-table
              keep-source
              :ref="refKeys[1]"
              :loading="newEntryTable.loading"
              :columns="newEntryTable.columns"
              :dataSource="newEntryTable.dataSource"
              :maxHeight="300"
              :disabled="disabled"
              :rowNumber="false"
              :rowSelection="!disabled"
              :toolbar="!disabled"
              :resizable="true"
              :edit-config="{trigger: 'click', mode: 'row', showIcon: false}"
              @edit-actived="({row}) => setMaterialUnitOptions(row, $refs.newEntryTable)"
              @added="onInEntryAdded"
              @valueChange="onEntryValueChange"
            />
          </a-tab-pane>

          <template slot="tabBarExtraContent">
            <vxe-table-columns-setter
              :table-key="activeKey + (disabled ? '1':'0')"
              :column-defs="activeKey==='entryTable' ? entryTable.columns : newEntryTable.columns"
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
  import JFormContainer from '@/components/jeecg/JFormContainer'
  import { BillFormMixin, BillFormGridMixin} from '../../common/mixins/BillFormMixin'
  import { BillVxeTableMixin } from '../../common/mixins/BillVxeTableMixin'
  import BillHeader from "../../common/components/BillHeader";
  import BillFooter from "../../common/components/BillFooter";
  import VxeTableColumnsSetter from "../../common/components/VxeTableColumnsSetter";

  export default {
    name: 'CheckForm',
    mixins: [JVxeTableModelMixin, BillFormMixin, BillFormGridMixin, BillVxeTableMixin],
    components: {JFormContainer, BillHeader, BillFooter, VxeTableColumnsSetter},

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
          warehouseId: '',
          materialCategoryId: '',
        },

        validatorRules: {
           warehouseId: [ { required: true, message: '请选择仓库!'} ],
        },

        //盘点范围
        checkRange: {
          warehouseId: '',
          materialCategoryId: '',
        },
        //上次盘点范围
        lastCheckRange: {
          warehouseId: '',
          materialCategoryId: '',
        },

        entryNoStep: 10,
        addDefaultRowNum: 0,
        refKeys: ['entryTable', 'newEntryTable',],
        tableKeys:['entryTable', 'newEntryTable',],
        activeKey: 'entryTable',

        // 明细
        entryTable: {
          loading: false,
          dataSource: [],
          url: {
            list: '/stock/stkCheck/queryEntryByMainId',
            listByRange: '/stock/stkCheck/queryEntryByRange'
          },
          columns: [
            {
              title: '#',
              key: 'entryNo',
              type: JVXETypes.inputNumber,
              width:"60px",
              align:"center",
              fixed: 'left',
              sortable: true,
              defaultValue: '',
              disabled:true,
            },
            {
              title: '是否新批次',
              key: 'isNewBatch',
              type: JVXETypes.hidden,
              width:"1px",
              defaultValue: '0',
              disabled:true,
            },
            {
              title: '仓库',
              key: 'warehouseId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_warehouse,aux_name,id",
              width:"200px",
              defaultValue: '',
              options:[],
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '物料',
              key: 'materialId',
              type: JVXETypes.selectSearch,
              dictCode:"bas_material,aux_name,id",
              width:"150px",
              defaultValue: '',
              options:[],
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
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
              title: '批次',
              key: 'batchNo',
              type: JVXETypes.input,
              width:"230px",
              sortable: true,
              defaultValue: '',
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '供应商',
              key: 'supplierId',
              type: JVXETypes.select,
              dictCode:"bas_supplier,aux_name,id",
              width:"150px",
              defaultValue: '',
              disabled:true,
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
              validateRules: [{ required: true, message: '${title}不能为空' }],
              disabled:true,
            },
            {
              title: '账存数量',
              key: 'bookQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
              disabled:true,
            },
            {
              title: '实存数量',
              key: 'qty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: 0,
              validateRules: [{ required: true, message: '${title}不能为空' }],
              statistics: ['sum'],
            },
            {
              title: '盈亏',
              key: 'profitQty',
              type: JVXETypes.inputNumber,
              width:"120px",
              align:"right",
              formatter: this.formatQty,
              defaultValue: '',
              disabled:true,
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
          ],
        },

        //明细：新批次
        newEntryTable: {
          loading: false,
          dataSource: [],
          columns: []
        },

        url: {
          add: "/stock/stkCheck/add",
          edit: "/stock/stkCheck/edit",
          check: "/stock/stkCheck/check",
          ebpm: "/stock/stkCheck/bpm/end",
          execute: "/stock/stkCheck/execute",
          void: "/stock/stkCheck/void",
        }
      }
    },

    computed: {
       isCheckRangeChange() {
        return this.model.warehouseId !== this.lastCheckRange.warehouseId ||
          this.model.materialCategoryId !== this.lastCheckRange.materialCategoryId;
      }
    },

    created() {
      for(let col of this.entryTable.columns){
        let col2 = Object.assign({}, col);
        if (!['entryNo', 'isNewBatch', 'bookQty', 'profitQty'].includes(col2.key)) col2.disabled = false;
        if (col2.key==='isNewBatch') col2.defaultValue = '1';
        if (col2.key==='bookQty') col2.defaultValue = '0';
        if (col2.key==='qty') col2.defaultValue = '';
        this.newEntryTable.columns.push(col2);
      }

      if (!this.disabled) this.initMaterialRelated(this.newEntryTable);
    },

    methods: {
      addBefore(){
        this.entryTable.dataSource=[];
        this.newEntryTable.dataSource=[];
      },

      addAfter() {
        this.$refs.billHeader.fillBillNo('stk_kcpd_bill_no');
      },

      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },

      editAfter() {
        if (this.model.id) {
          this.lastCheckRange.warehouseId = this.model.warehouseId;
          this.lastCheckRange.materialCategoryId = this.model.materialCategoryId;

          const that = this;
          let params = { id: this.model.id }
          this.requestSubTableData(this.entryTable.url.list, params, this.entryTable, splitData);

          function splitData(){
            let ds1 = [], ds2 = [];
            for(let row of that.entryTable.dataSource) row.isNewBatch===0 ? ds1.push(row):ds2.push(row);
            that.entryTable.dataSource = ds1;
            that.newEntryTable.dataSource = ds2;
          }
        }
      },

      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          stkCheckEntryList: allValues.tablesValue[0].tableData.concat(allValues.tablesValue[1].tableData),
        }
      },

      handleCheckRangeOk(){
        const that = this;
        this.lastCheckRange.warehouseId = this.model.warehouseId;
        this.lastCheckRange.materialCategoryId = this.model.materialCategoryId;
        this.requestSubTableData(this.entryTable.url.listByRange, this.lastCheckRange, this.entryTable, success);
        function success() {
          const ds = that.entryTable.dataSource;
          that.entryTable.dataSource = [];
          setTimeout(()=>{
            that.$refs.entryTable.addRows(ds);
          }, 100);
        }
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        let values = {};
        switch (column.property) {
          case "entryNo": //联动：分录号 --> 批次
            if (!row.batchNo || row.batchNo === this.model.billNo + "-" + oldValue) {
              target.setValues([{rowKey: row.id, values: {batchNo: this.model.billNo + "-" + value}}]);
            }
            break;
          case "materialId":
            this.handleMaterialChange(row, target);
            break;
          case "unitId":
            if (!oldValue || oldValue.length === 0 || !value || value.length === 0) break;
            const rate = this.getUnitRate(row.materialId, oldValue, value);
            if (!rate)
              //unitId新值不合法，恢复原值
              target.setValues([{rowKey: row.id, values: {unitId: oldValue} }]);
            else {
              values = {};
              values.qty = (row.qty * rate).toFixed(3);
              values.bookQty = (row.bookQty * rate).toFixed(3);
              values.profitQty =  Number(values.qty) - Number(values.bookQty)
              target.setValues([{rowKey: row.id, values: values}]);
            }
            break;
          case "qty":
            values = {};
            values.profitQty =  Number(row.qty) - Number(row.bookQty);
            target.setValues([{rowKey: row.id, values: values}]);
            break;
        }
      },

    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
