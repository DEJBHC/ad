<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
           <a-col :xl="5" :lg="6" :md="7" :sm="24">
            <a-form-item label="客户">
              <j-search-select-tag v-model="queryParam.customer_id" :async="true" dict="bas_customer,aux_name,id" placeholder="请选择"/>
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
      <a-button type="link"  icon="download" @click="handleExportXls('应收即时余额')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="customer_id"
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
    name: "FinReceivableBal",
    mixins:[JeecgListMixin, ListMixin],

    data () {
      return {
        description: '应收即时余额',
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
            title:'客户',
            width:200,
            align:"left",
            dataIndex: 'customer_name',
            ellipsis: true,
          },
          {
            title:'借方余额',
            width:120,
            align:"right",
            dataIndex: 'debit_bal',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/wrapper/cgreport/getData/1579838441006305281",
          exportXlsUrl: "/online/cgreport/api/exportManySheetXls/1579838441006305281",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = 'customer_name';
      this.isorter.order = 'asc';
      this.loadData();
    },
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../../common/less/List.less';
</style>
