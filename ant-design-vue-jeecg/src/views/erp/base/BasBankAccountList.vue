<template>
  <a-card :bordered="false">
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="link" @click="myHandleAdd"  icon="plus">新增</a-button>
      <a-button type="link" icon="download" @click="handleExportXls('银行账户')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="link" icon="import">导入</a-button>
      </a-upload>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{ x: 2000}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, fixed: true}"
        :components="drag(columns)"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <a slot="accountNo" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

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

    <basBankAccount-modal ref="modalForm" @ok="modalFormOk"></basBankAccount-modal>
  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import BasBankAccountModal from './modules/BasBankAccountModal'

  export default {
    name: "BasBankAccountList",
    mixins:[JeecgListMixin, ListMixin],
    components: {BasBankAccountModal},

    data () {
      return {
        description: '银行账户',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            fixed:"left",
            width:60,
            align:"center",
            customRender: (t,r,index)=>parseInt(index)+1,
          },
          {
            title:'账号',
            fixed:"left",
            width:220,
            align:"center",
            dataIndex: 'accountNo',
            scopedSlots: { customRender: 'accountNo' }
          },
          {
            title:'账户名',
            fixed:"left",
            width:160,
            align:"left",
            dataIndex: 'name'
          },
          {
            title:'币种',
            width:100,
            align:"center",
            dataIndex: 'currency_dictText'
          },
          {
            title:'行号',
            width:150,
            align:"center",
            dataIndex: 'bankNo'
          },
          {
            title:'账户管理员',
            width:100,
            align:"center",
            dataIndex: 'manager_dictText'
          },
          {
            title:'启用',
            width:80,
            align:"center",
            dataIndex: 'isEnabled_dictText'
          },
          {
            title:'备注',
            align:"left",
            ellipsis: true,
            ellipsis: true,
            dataIndex: 'remark'
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
          list: "/base/basBankAccount/list",
          delete: "/base/basBankAccount/delete",
          deleteBatch: "/base/basBankAccount/deleteBatch",
          exportXlsUrl: "/base/basBankAccount/exportXls",
          importExcelUrl: "bas/basBankAccount/importExcel",
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
