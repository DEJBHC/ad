<template>
  <a-card :bordered="false" :body-style="{ padding: 0}">
    <h3>核销明细-{{title}}</h3>
    <j-vxe-table
      keep-source
      ref="table"
      :loading="loading"
      :columns="columns"
      :dataSource="dataSource"
      :disabled="disabled"
      :rowNumber="false"
      :rowSelection="!disabled"
      :toolbar="false"
      :resizable="true"
      :edit-config="{trigger: 'click', mode: 'cell', showIcon: false}"
      @selectRowChange="({selectedRows}) => this.$emit('update:selectedRowCount', selectedRows.length)"
      @valueChange="onEntryValueChange"/>
   </a-card>
</template>

<script>
  import { JVXETypes } from '@/components/jeecg/JVxeTable'
  import { BillVxeTableMixin } from '../../../common/mixins/BillVxeTableMixin'
  import XEUtils from "xe-utils";

  export default {
    name: "RpCheckEntryTable",
    mixins: [BillVxeTableMixin],
    props: {
      checkType: {type: String, default: ""},// 核销类型
      checkSide: {type: String, default: ""},// 核销方向: 1-应收应付， 2-收款付款
      disabled: {type: Boolean, default: false},
    },

    data () {
      return {
        addDefaultRowNum: 0,// 新增时子表默认添加几行空数据
        loading:false,
        dataSource:[],
        columns: [
          {
            title: '分录号',
            key: 'entryNo',
            type: JVXETypes.input,
            width:"90px",
            align:"center",
            fixed: 'left',
            disabled: true,
            sortable: true,
          },
          {
            title:'源单号',
            key: 'srcNo',
            type: JVXETypes.input,
            width:"180px",
            disabled: true,
            sortable: true,
          },
          {
            title: '源单类型',
            key: 'srcBillType',
            type: JVXETypes.select,
            dictCode:"x_bill_type",
            width:"120px",
            defaultValue:'',
            disabled: true,
          },
          {
            title:'未核销金额',
            key: 'uncheckedAmt',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            disabled: true,
            statistics: ['sum'],
          },
          {
            title:'核销金额',
            key: 'amt',
            type: JVXETypes.inputNumber,
            width:"120px",
            align:"right",
            formatter: this.formatAmt,
            validateRules: [{ required: true, message: '${title}不能为空' },{handler: this.amtValidator}],
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
            width:"90px",
            defaultValue: '',
          },
          {
            title: '自定义2',
            key: 'custom2',
            type: JVXETypes.input,
            width:"90px",
            defaultValue: '',
          },
        ],
      }
    },

    computed: {
      title() {
        switch (this.checkType) {
          case "1":
            return this.checkSide === "1" ? "应收" : "收款"
          case "2":
            return this.checkSide === "1" ? "应付" : "付款"
        }
      },

      tableData() {
        return this.$refs.table.getTableData();
      }
    },

    watch:{
      dataSource: function () {
        this.$emit('update:selectedRowCount', 0);
        //dataSource尚未更新到tableData
        this.$emit("update:rowCount", this.dataSource.length);
        setTimeout(()=>{
          this.triggerTotalAmtChange();
        }, 1000);
      },

    },

    methods: {
      addRows(rows){
        const table = this.$refs.table;
        table.addRows(rows).then(({row}) => table.$refs.vxe.$refs.xTable.setActiveCell(row, 'amt'));

        this.$emit("update:rowCount", this.tableData.length);
        this.triggerTotalAmtChange();
      },

      removeSelectedRows() {
        const table = this.$refs.table;
        if (table.selectedRows.length === 0) return;

        table.removeRows(table.selectedRows);
        table.clearSelection();
        this.$emit('update:selectedRowCount', 0);
        this.$emit("update:rowCount", this.tableData.length);
        this.triggerTotalAmtChange();
      },

      onEntryValueChange(event) {
        const { type, value, oldValue, row, column, target, isSetValues } = event;
        if (value === oldValue || isSetValues) return;

        switch (column.property) {
          case "amt":
            this.triggerTotalAmtChange();
            break;
        }
      },

      triggerTotalAmtChange() {
        const totalAmt = XEUtils.sum(this.tableData, 'amt');
        this.$emit("update:totalAmt", totalAmt);
      },

      amtValidator({cellValue, row, column}, callback, target) {
        const amt = Number(cellValue), uncheckedAmt = Number(row.uncheckedAmt);
        if (isNaN(amt) || isNaN(uncheckedAmt)) {
          callback();
          return;
        }

        if (uncheckedAmt === 0 && amt !== 0 || uncheckedAmt * amt < 0
          || uncheckedAmt > 0 && amt > uncheckedAmt  || uncheckedAmt < 0 && amt < uncheckedAmt) {
          callback(false, '核销金额不能超出未核金额！');
        }
        else {
          callback(true);
        }
      },

    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
  /deep/ .vxe-table.size--medium .vxe-body--column.col--ellipsis,
  /deep/ .vxe-table.size--medium .vxe-footer--column.col--ellipsis,
  /deep/ .vxe-table.size--medium .vxe-header--column.col--ellipsis,
  /deep/ .vxe-table.vxe-editable.size--medium .vxe-body--column {
    height: 38px
  }
</style>
