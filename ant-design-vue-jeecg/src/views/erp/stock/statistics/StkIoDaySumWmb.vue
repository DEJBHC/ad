<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="8" :lg="9" :md="10" :sm="24">
            <a-form-item label="日期范围">
              <j-date placeholder="请选择开始" class="query-group-cust" v-model="queryParam.beginDate" />
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束" class="query-group-cust" v-model="queryParam.endDate"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="6" :md="7" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select v-model="queryParam.warehouseId" dict="bas_warehouse,aux_name,id"
                             pidValue="0" hasChildField="has_child" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="6" :md="7" :sm="24">
            <a-form-item label="物料">
              <j-search-select-tag v-model="queryParam.materialId" :async="true" dict="bas_material,aux_name,id" placeholder="请选择"/>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :xl="5" :lg="6" :md="7" :sm="24">
              <a-form-item label="批次">
                <a-input placeholder="请输入" v-model="queryParam.batchNo"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="4" :lg="6" :md="7" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="link"  icon="download" @click="handleExportXls('物料出入库明细')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{x: true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :components="drag(columns)"
        class="j-table-force-nowrap"
        @change="handleTableChange">
      </a-table>
    </div>

  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../../common/mixins/ListMixin'
  import XEUtils from "xe-utils";

  export default {
    name: "StkIoDaySumWmb",
    mixins:[JeecgListMixin, ListMixin],

    data () {
      return {
        description: '出入库日汇总-仓库物料批次',

        queryParam: {
          beginDate: '',
          endDate: ''
        },

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
            title:'日期',
            width:100,
            align:"center",
            dataIndex: 'date',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
          },
          {
            title:'仓库',
            align:"left",
            dataIndex: 'warehouseId_dictText',
          },
          {
            title:'物料编码',
            dataIndex: 'materialCode',
          },
          {
            title:'物料名称',
            dataIndex: 'materialId_dictText',
          },
          {
            title:'规格型号',
            dataIndex: 'materialModel',
          },
          {
            title:'批次',
            align:"center",
            dataIndex: 'batchNo',
          },
          {
            title:'单位',
            width:80,
            align:"center",
            dataIndex: 'unitId_dictText'
          },
          {
            title:'入库数量',
            width:120,
            align:"right",
            dataIndex: 'inQty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'入库金额',
            width:120,
            align:"right",
            dataIndex: 'inCost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'出库数量',
            width:120,
            align:"right",
            dataIndex: 'outQty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'出库金额',
            width:120,
            align:"right",
            dataIndex: 'outCost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'结存数量',
            width:120,
            align:"right",
            dataIndex: 'balQty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'结存金额',
            width:120,
            align:"right",
            dataIndex: 'balCost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/stock/stkStatistics/day/sum/wmb/list",
          exportXlsUrl: "/stock/stkStatistics/day/sum/wmb/exportXls",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = '';
      this.isorter.order = '';
      this.queryParam.beginDate = new Date().format('yyyy-MM-dd');
      this.queryParam.endDate = this.queryParam.beginDate;
      this.loadData();
    },

  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../../common/less/List.less';
</style>
