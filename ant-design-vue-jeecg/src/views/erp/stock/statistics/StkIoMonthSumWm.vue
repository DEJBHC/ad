<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="3" :lg="4" :md="5" :sm="24">
            <a-form-item label="年">
              <j-year-picker placeholder="请选择" v-model="queryParam.year" :allow-clear="false"/>
            </a-form-item>
          </a-col>
          <a-col :xl="3" :lg="4" :md="5" :sm="24">
            <a-form-item label="月">
              <a-select placeholder="请选择" v-model="queryParam.month" :allow-clear="true"
                        :options="[{value:1,label:1},{value:2,label:2},{value:3,label:3},{value:4,label:4},
                        {value:5,label:5},{value:6,label:6},{value:7,label:7},{value:8,label:8},
                        {value:9,label:9},{value:10,label:10},{value:11,label:11},{value:12,label:12}]"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="6" :md="7" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select v-model="queryParam.warehouse_id" dict="bas_warehouse,aux_name,id"
                             pidValue="0" hasChildField="has_child" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="6" :md="7" :sm="24">
            <a-form-item label="物料">
              <j-search-select-tag v-model="queryParam.material_id" :async="true" dict="bas_material,aux_name,id" placeholder="请选择"/>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
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
      <a-button type="link"  icon="download" @click="handleExportXls('出入库月汇总')">导出</a-button>
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
  import JYearPicker from '../../common/components/JYearPicker'
  import XEUtils from "xe-utils";

  export default {
    name: "StkIoMonthSumWm",
    mixins: [JeecgListMixin, ListMixin],
    components: {JYearPicker},

    data () {
      return {
        description: '出入库月汇总-仓库物料',

        queryParam: {
          year: '',
          month: ''
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
            title:'年',
            width:60,
            align:"center",
            dataIndex: 'year',
          },
          {
            title:'月',
            width:40,
            align:"center",
            dataIndex: 'month',
          },
          {
            title:'仓库',
            align:"left",
            dataIndex: 'warehouse_id_dictText',
          },
          {
            title:'物料编码',
            dataIndex: 'material_code',
          },
          {
            title:'物料名称',
            dataIndex: 'material_id_dictText',
          },
          {
            title:'规格型号',
            dataIndex: 'material_model',
          },
          {
            title:'单位',
            width:80,
            align:"center",
            dataIndex: 'unit_id_dictText'
          },
          {
            title:'期初数量',
            width:120,
            align:"right",
            dataIndex: 'begin_qty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'期初金额',
            width:120,
            align:"right",
            dataIndex: 'begin_cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'入库数量',
            width:120,
            align:"right",
            dataIndex: 'in_qty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'入库金额',
            width:120,
            align:"right",
            dataIndex: 'in_cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'出库数量',
            width:120,
            align:"right",
            dataIndex: 'out_qty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'出库金额',
            width:120,
            align:"right",
            dataIndex: 'out_cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'结存数量',
            width:120,
            align:"right",
            dataIndex: 'bal_qty',
            customRender: t => XEUtils.commafy(t,{digits: 3})
          },
          {
            title:'结存金额',
            width:120,
            align:"right",
            dataIndex: 'bal_cost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/wrapper/cgreport/getData/1575380033020686337",
          exportXlsUrl: "/online/cgreport/api/exportManySheetXls/1575380033020686337",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = 'year,month,material_code';
      this.isorter.order = 'asc';

      const d = new Date();
      let y = d.getFullYear(), m = d.getMonth(); //d.getMonth()：0~11
      if (m === 0) {
        y--;
        m = 12;
      }
      this.queryParam.year = y.toString();
      this.queryParam.month = m.toString();

      this.loadData();
    },

  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../../common/less/List.less';
</style>
