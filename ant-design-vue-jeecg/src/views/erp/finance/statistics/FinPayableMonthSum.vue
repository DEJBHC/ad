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
            <a-form-item label="供应商">
              <j-search-select-tag v-model="queryParam.supplier_id" :async="true" dict="bas_supplier,aux_name,id" placeholder="请选择"/>
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
      <a-button type="link"  icon="download" @click="handleExportXls('应付月汇总')">导出</a-button>
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
    name: "FinPayableMonthSum",
    mixins: [JeecgListMixin, ListMixin],
    components: {JYearPicker},

    data () {
      return {
        description: '应付月汇总',

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
            title:'供应商',
            width:200,
            align:"left",
            dataIndex: 'supplier_name',
            ellipsis: true,
          },
          {
            title:'期初贷方余额',
            width:120,
            align:"right",
            dataIndex: 'begin_credit_bal',
            customRender: t => XEUtils.commafy(t,{digits: 2})
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
            title:'期末贷方余额',
            width:120,
            align:"right",
            dataIndex: 'credit_bal',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
          },
        ],
        url: {
          list: "/wrapper/cgreport/getData/1578658883720790018",
          exportXlsUrl: "/online/cgreport/api/exportManySheetXls/1578658883720790018",
        },

        disableMixinCreated: true
      }
    },

    created() {
      this.isorter.column = 'year,month,supplier_name';
      this.isorter.order = 'asc';

      const d = new Date();
      let y = d.getFullYear(), m = d.getMonth();
      if (m === 0) {
        y--;
        m = 12;
      }
      else {
        m++;
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
