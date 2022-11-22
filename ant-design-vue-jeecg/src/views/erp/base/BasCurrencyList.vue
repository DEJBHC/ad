<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="代码">
              <a-input placeholder="请输入" v-model="queryParam.code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入" v-model="queryParam.name"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
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
      <a-button type="link" @click="myHandleAdd" icon="plus">新增</a-button>
      <a-button type="link" icon="download" @click="handleExportXls('物料')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="link" icon="import">导入</a-button>
      </a-upload>

      <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
      <a v-if="selectedRowKeys.length > 0" style="margin-left: 12px" @click="onClearSelected">清空</a>
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        :components="drag(columns)"
        @change="handleTableChange">

        <a slot="code" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <span slot="action" slot-scope="text, record">
          <a @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">删除</a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <basCurrency-modal ref="modalForm" @ok="modalFormOk"></basCurrency-modal>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import BasCurrencyModal from './modules/BasCurrencyModal'

  export default {
    name: "BasCurrencyList",
    mixins:[JeecgListMixin, ListMixin],
    components: {BasCurrencyModal},

    data () {
      return {
        description: '币种',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender: (t,r,index)=>parseInt(index)+1,
          },
          {
            title:'代码',
            width:80,
            align:"center",
            dataIndex: 'code',
            scopedSlots: { customRender: 'code' }
          },
          {
            title:'名称',
            width:120,
            align:"center",
            dataIndex: 'name'
          },
          {
            title:'本币',
            width:80,
            align:"center",
            dataIndex: 'isFunctional_dictText'
          },
          {
            title:'启用',
            width:80,
            align:"center",
            dataIndex: 'isEnabled_dictText'
          },
          {
            title:'创建时间',
            width:100,
            align:"center",
            dataIndex: 'createTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
          },
          {
            title:'创建人',
            width:100,
            align:"center",
            dataIndex: 'createBy_dictText'
          },
          {
            title:'修改时间',
            width:100,
            align:"center",
            dataIndex: 'updateTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
          },
          {
            title:'修改人',
            width:100,
            align:"center",
            dataIndex: 'updateBy_dictText'
          },
          {},
          {
            title: '操作',
            dataIndex: 'action',
            fixed: "right",
            width:120,
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],

        url: {
          list: "/base/basCurrency/list",
          delete: "/base/basCurrency/delete",
          deleteBatch: "/base/basCurrency/deleteBatch",
          exportXlsUrl: "/base/basCurrency/exportXls",
          importExcelUrl: "bas/basCurrency/importExcel",
        },
        dictOptions:{},
      }
    },

    methods: {
      initDictConfig(){
      },

    }
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../common/less/List.less';
</style>
