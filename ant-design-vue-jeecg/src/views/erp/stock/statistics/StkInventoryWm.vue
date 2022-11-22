<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select v-model="queryParam.warehouse_id" dict="bas_warehouse,aux_name,id"
                             pidValue="0" hasChildField="has_child" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="物料">
              <j-search-select-tag v-model="queryParam.material_id" :async="true" dict="bas_material,aux_name,id" placeholder="请选择"/>
            </a-form-item>
          </a-col>

          <a-col :xl="4" :lg="6" :md="7" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="link"  icon="download" @click="handleExportXls('物料仓库库存')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{ x: true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :components="drag(columns)"
        @change="handleTableChange">
      </a-table>
    </div>

  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../../common/mixins/ListMixin'
  import XEUtils from "xe-utils";

  export default {
    name: "StkInventoryWm",
    mixins:[JeecgListMixin, ListMixin],

    data () {
      return {
        description: '即时库存-仓库物料',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            fixed: 'left',
            width:60,
            align:"center",
            customRender: (t,r,index)=>parseInt(index)+1
          },
          {
            title:'物料编码',
            width:120,
            dataIndex: 'material_code',
            sorter: true
          },
          {
            title:'物料名称',
            width:160,
            dataIndex: 'material_name',
            sorter: true
          },
          {
            title:'规格型号',
            width:200,
            dataIndex: 'material_model',
          },
          {
            title:'仓库',
            width:120,
            align:"left",
            dataIndex: 'warehouse_name',
          },
          {
            title:'单位',
            width:80,
            align:"center",
            dataIndex: 'unit_id_dictText'
          },
          {
            title:'数量',
            width:120,
            align:"right",
            dataIndex: 'qty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'金额',
            width:120,
            align:"right",
            dataIndex: 'cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'单位金额',
            width:120,
            align:"right",
            dataIndex: 'unit_cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/wrapper/cgreport/getData/1573320640724779009",
          exportXlsUrl: "/online/cgreport/api/exportManySheetXls/1573320640724779009",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = 'material_code';
      this.isorter.order = 'asc';
      this.loadData();
    },
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../../common/less/List.less';
</style>
