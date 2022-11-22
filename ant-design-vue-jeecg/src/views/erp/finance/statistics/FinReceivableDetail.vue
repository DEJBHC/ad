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
          <a-col :xl="8" :lg="9" :md="10" :sm="24">
            <a-form-item label="单据日期">
              <j-date placeholder="请选择开始" class="query-group-cust" v-model="queryParam.bill_date_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束" class="query-group-cust" v-model="queryParam.bill_date_end"></j-date>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :xl="5" :lg="6" :md="7" :sm="24">
              <a-form-item label="业务部门">
                <j-select-depart v-model="queryParam.op_dept" customReturnField="orgCode" :multi="true" placeholder="请选择"/>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="6" :md="7" :sm="24">
              <a-form-item label="业务员">
                <j-select-user-by-dep v-model="queryParam.operator" :multi="true" placeholder="请选择"/>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="6" :md="7" :sm="24">
              <a-form-item label="单据编号">
                <a-input placeholder="请输入" v-model="queryParam.bill_no"/>
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
      <a-button type="link"  icon="download" @click="handleExportXls('应收明细')">导出</a-button>
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
    name: "FinReceivableDetail",
    mixins:[JeecgListMixin, ListMixin],

    data () {
      return {
        description: '应收明细',
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
            dataIndex: 'customer_id_dictText',
            ellipsis: true,
          },
          {
            title:'业务部门',
            width:120,
            align:"center",
            dataIndex: 'op_dept_dictText',
            ellipsis: true,
          },
          {
            title: '业务员',
            width: 90,
            align: "center",
            dataIndex: 'operator_dictText',
          },
          {
            title:'单据类型',
            width:120,
            align:"center",
            dataIndex: 'bill_type_dictText',
          },
          {
            title:'单据编号',
            width:160,
            align:"center",
            dataIndex: 'bill_no',
          },
          {
            title:'单据日期',
            width:100,
            align:"center",
            dataIndex: 'bill_date',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
            sorter: true
          },
          {
            title:'借方金额',
            width:120,
            align:"right",
            dataIndex: 'debit_amt',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'贷方金额',
            width:120,
            align:"right",
            dataIndex: 'credit_amt',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/wrapper/cgreport/getData/1578557943487479809",
          exportXlsUrl: "/online/cgreport/api/exportManySheetXls/1578557943487479809",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = 'effective_time';
      this.isorter.order = 'desc';
      this.loadData();
    },
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../../common/less/List.less';
</style>
