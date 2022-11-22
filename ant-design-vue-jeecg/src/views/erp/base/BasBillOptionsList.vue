<template>
  <a-card :bordered="false">
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="link" @click="myHandleAdd" icon="plus">新增</a-button>
      <a-button type="link" icon="download" @click="handleExportXls('单据选项')">导出</a-button>
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

        <a slot="billType_dictText" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

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

    <bas-bill-options-modal ref="modalForm" @ok="modalFormOk"></bas-bill-options-modal>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import BasBillOptionsModal from './modules/BasBillOptionsModal'

  export default {
    name: 'BasBillOptionsList',
    mixins:[JeecgListMixin, ListMixin],
    components: {BasBillOptionsModal},

    data () {
      return {
        description: '单据选项',
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
            title:'单据类型',
            align:"center",
            sorter: true,
            dataIndex: 'billType_dictText',
            scopedSlots: { customRender: 'billType_dictText' }
          },
          {
            title:'手动单据核批类型',
            align:"center",
            dataIndex: 'mbillApprovalType_dictText'
          },
          {
            title:'自动单据核批类型',
            align:"center",
            dataIndex: 'abillApprovalType_dictText'
          },
          {
            title:'审批BPM类型',
            align:"center",
            dataIndex: 'bpmType_dictText'
          },
          {
            title:'生效后自动执行',
            align:"center",
            dataIndex: 'isAutoExecute_dictText'
          },
          {
            title:'自动结束执行',
            align:"center",
            dataIndex: 'isAutoEndExecute_dictText'
          },
          {
            title:'执行完后自动关闭',
            align:"center",
            dataIndex: 'isAutoClose_dictText'
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
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/base/basBillOptions/list",
          delete: "/base/basBillOptions/delete",
          deleteBatch: "/base/basBillOptions/deleteBatch",
          exportXlsUrl: "/base/basBillOptions/exportXls",
          importExcelUrl: "bas/basBillOptions/importExcel",

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
