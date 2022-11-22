<template>
  <a-card :bordered="false" :body-style="{ padding: 0}">
    <h3>应收单</h3>
    <a-table
      ref="table"
      rowKey="billNo"
      bordered
      size="small"
      :scroll="{ x:800}"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :locale="{emptyText: '暂无数据'}"
      :rowSelection="{selectedRowKeys: selectedRowKeys, columnWidth: 40, getCheckboxProps: getCheckboxProps, onChange: onMySelectChange}"
      :components="drag(columns)"
      @change="handleTableChange">
    </a-table>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import ATableDragResize from '../../../common/mixins/ATableDragResize'
  import XEUtils from "xe-utils";

  export default {
    name: "RpCheckReceivableList",
    mixins:[JeecgListMixin, ATableDragResize],
    props: {
      //对应的核销明细组件
      checkEntryTableRefs: {type: Object, required: true},
      checkEntryTableRefKey: {type: String, required: true},
    },

    data () {
      return {
        //用于JeecgListMixin
        disableMixinCreated: true,
        // 表头
        columns: [
          {
            title:'单据号',
            width:180,
            align:"center",
            dataIndex: 'billNo',
            sorter: (a, b) => a.srcNo === b.srcNo ? 0 : (a.srcNo > b.srcNo ? 1 : -1),
            defaultSortOrder:'ascend'
          },
          {
            title:'源单号',
            width:160,
            align:"center",
            dataIndex: 'srcNo',
            sorter: (a, b) => a.srcNo === b.srcNo ? 0 : (a.srcNo > b.srcNo ? 1 : -1)
          },
          {
            title:'未核销金额',
            width:120,
            align:"right",
            dataIndex: 'uncheckedAmt',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'备注',
            align:"left",
            ellipsis: true,
            dataIndex: 'remark'
          },
        ],

        url: {
          list: "/finance/finReceivable/checkableList",
        },

        dictOptions:{},
      }
    },

    watch:{
      dataSource: function () {
        this.$emit("update:rowCount", this.dataSource.length);
      }
    },

    created() {
      this.ipagination.pageSize = 5;
      this.ipagination.pageSizeOptions.unshift('5');
    },

    methods: {
      init() {
        //dataSource、selectedRowKeys、selectionRows在JeecgListMixin中定义
        this.dataSource = [];
        this.selectedRowKeys = [];
        this.selectionRows = [];
      },

      getCheckboxProps(record) {
        let rows = this.checkEntryTableRefs[this.checkEntryTableRefKey].tableData.filter(row => record.billNo === row.srcNo);
        return {props: {disabled: rows.length > 0}};
      },

      onMySelectChange(selectedRowKeys, selectionRows) {
        this.onSelectChange(selectedRowKeys, selectionRows);
        this.$emit("update:selectedRowCount", selectionRows.length);
      },

    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';

  //ATableDragResize.js中vue-draggable-resizable所用样式
  /deep/.table-draggable-handle {
    border:1px solid red;
    height: 100% !important;
    left: auto !important;
    right: -5px;
    cursor: col-resize;
    touch-action: none;
    border: none;
    position: absolute;
    transform: none !important;
    bottom: 0;
  }
  /deep/.resize-table-th {
    position: relative;
  }
</style>
