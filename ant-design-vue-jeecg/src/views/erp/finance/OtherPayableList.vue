<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单据编号">
              <a-input placeholder="请输入" v-model="queryParam.billNo"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10" :lg="11" :md="12" :sm="24">
            <a-form-item label="单据日期">
              <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.billDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.billDate_end"></j-date>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="单据主题">
                <a-input placeholder="请输入" v-model="queryParam.subject"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="供应商">
                <j-search-select-tag v-model="queryParam.supplierId" :async="true" dict="bas_supplier,aux_name,id" placeholder="请选择"/>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="6" :md="7" :sm="24">
              <a-form-item label="单据阶段">
                <j-dict-select-tag v-model="queryParam.billStage" dictCode="x_bill_stage"/>
              </a-form-item>
            </a-col>
            <a-col :xl="3" :lg="6" :md="7" :sm="24">
              <a-form-item label="已生效">
                <j-dict-select-tag placeholder="" v-model="queryParam.isEffective" dictCode="yn"/>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="6" :md="7" :sm="24">
              <a-form-item label="已关闭">
                <j-dict-select-tag v-model="queryParam.isClosed" dictCode="yn"/>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="6" :md="7" :sm="24">
              <a-form-item label="已作废">
                <j-dict-select-tag v-model="queryParam.isVoided" dictCode="yn"/>
              </a-form-item>
            </a-col>
          </template>
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
      <a-button type="link" icon="download" @click="handleExportXls('其他应付单')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="link" icon="import">导入</a-button>
      </a-upload>

      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item :disabled="!isBatchEnabled('delete')" key="1" @click="batchDel">删除</a-menu-item>
          <a-menu-item :disabled="!isBatchEnabled('close')" key="2" @click="batchClose">关闭</a-menu-item>
          <a-menu-item :disabled="!isBatchEnabled('unclose')" key="3" @click="batchUnclose">反关闭</a-menu-item>
        </a-menu>
        orderedQty<a-button type="link" style="margin-left: 8px">批量操作<a-icon type="down" /></a-button>
      </a-dropdown>
      <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
      <a v-if="selectedRowKeys.length > 0" style="margin-left: 12px" @click="onClearSelected">清空</a>

      <table-columns-setter v-model="columns" style="float: right;"/>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{ x: 2600}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, columnWidth: 40, onChange: onSelectChange}"
        :components="drag(columns)"
        @change="handleTableChange">

        <a slot="billNo" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <span slot="action" slot-scope="text, record">
          <a :disabled="!record.actions.edit" @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item :disabled="!record.actions.delete" key="1">
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">删除</a-popconfirm>
              </a-menu-item>
              <a-menu-item v-if="'check' in record.actions" :disabled="!record.actions.check" key="2" @click="handleCheck(record)">审核</a-menu-item>
              <a-menu-item v-if="'ebpm' in record.actions" :disabled="!record.actions.ebpm" key="3" @click="handleEbpm(record)">结束审批</a-menu-item>
              <a-menu-item v-if="'execute' in record.actions" :disabled="!record.actions.execute" key="4" @click="handleExecute(record)">执行</a-menu-item>
              <a-menu-item v-if="'close' in record.actions" :disabled="!record.actions.close" key="5">
                <a-popconfirm title="确定关闭吗?" @confirm="()=> handleClose(record.id)">关闭</a-popconfirm>
              </a-menu-item>
              <a-menu-item v-if="'unclose' in record.actions" :disabled="!record.actions.unclose" key="6" >
                <a-popconfirm title="确定反关闭吗?" @confirm="() => handleUnclose(record.id)">反关闭</a-popconfirm>
              </a-menu-item>
              <a-menu-item v-if="'void' in record.actions" :disabled="!record.actions.void" key="7" @click="handleVoid(record)">作废</a-menu-item>
              <a-menu-item key="9" @click="handlePrint(record.id)">打印</a-menu-item>
             </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <other-payable-modal ref="modalForm" @ok="modalFormOk" @print="handlePrint"/>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OtherPayableModal from './modules/OtherPayableModal'
  import TableColumnsSetter from '../common/components/TableColumnsSetter'
  import { BillListMixin } from '../common/mixins/BillListMixin'
  import XEUtils from "xe-utils";

  export default {
    name: "OtherPayableList",
    mixins:[JeecgListMixin, BillListMixin],
    components: {TableColumnsSetter, OtherPayableModal},

    data () {
      return {
        description: '其他应付',
        billReportId: '731477317491929088',
        queryParam: {
          isVoided: 0,
        },

        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            fixed: 'left',
            width:40,
            align:"center",
            customRender: (t,r,index)=>parseInt(index)+1
          },
          {
            title:'单据编号',
            fixed:'left',
            width:160,
            align:"center",
            dataIndex: 'billNo',
            scopedSlots: {customRender: 'billNo'},
            sorter: true
          },
          {
            title: '单据日期',
            width: 100,
            align: "center",
            dataIndex: 'billDate',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
            sorter: true
          },
          {
            title:'单据主题',
            align:"left",
            dataIndex: 'subject',
            ellipsis: true,
            sorter: true
          },
          {
            title:'源单号',
            width:160,
            align:"center",
            dataIndex: 'srcNo',
            sorter: true
          },
          {
            title:'供应商',
            align:"left",
            dataIndex: 'supplierId_dictText',
            ellipsis: true,
            sorter: true
          },
          {
            title:'业务部门',
            width:120,
            align:"center",
            dataIndex: 'opDept_dictText',
            ellipsis: true,
            sorter: true
          },
          {
            title:'业务员',
            width: 90,
            align:"center",
            dataIndex: 'operator_dictText',
            sorter: true
          },
          {
            title:'金额',
            width:120,
            align:"right",
            dataIndex: 'amt',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'已核销金额',
            width:120,
            align:"right",
            dataIndex: 'checkedAmt',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title: '单据阶段',
            width: 75,
            align: "center",
            dataIndex: 'billStage_dictText'
          },
          {
            title:'已生效',
            width:60,
            align:"center",
            dataIndex: 'isEffective_dictText'
          },
          {
            title:'已关闭',
            width:60,
            align:"center",
            dataIndex: 'isClosed_dictText'
          },
          {
            title:'已作废',
            width:60,
            align:"center",
            dataIndex: 'isVoided_dictText'
          },
          {
            title:'自动单据',
            width:75,
            align:"center",
            dataIndex: 'isAuto_dictText'
          },
          {
            title:'红字单据',
            width:75,
            align:"center",
            dataIndex: 'isRubric_dictText'
          },
          {
            title:'备注',
            align:"left",
            ellipsis: true,
            dataIndex: 'remark'
          },
          {
            title: '生效时间',
            width: 90,
            align: "center",
            dataIndex: 'effectiveTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
          },
          {
            title: '核批人',
            width: 75,
            align: "center",
            dataIndex: 'approver_dictText'
          },
          {
            title: '制单时间',
            width: 90,
            align: "center",
            dataIndex: 'createTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
          },
          {
            title: '制单人',
            width: 75,
            align: "center",
            dataIndex: 'createBy_dictText'
          },
          {
            title: '制单部门',
            width: 120,
            align: "center",
            dataIndex: 'sysOrgCode_dictText'
          },
          {
            title: '修改时间',
            width: 90,
            align: "center",
            dataIndex: 'updateTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
          },
          {
            title: '修改人',
            width: 75,
            align: "center",
            dataIndex: 'updateBy_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            fixed:"right",
            width:120,
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/finance/finPayable/list/299",
          delete: "/finance/finPayable/delete",
          deleteBatch: "/finance/finPayable/deleteBatch",
          close: "/finance/finPayable/close",
          closeBatch: "/finance/finPayable/closeBatch",
          unclose: "/finance/finPayable/unclose",
          uncloseBatch: "/finance/finPayable/uncloseBatch",
          exportXlsUrl: "/finance/finPayable/exportXls/299",
          importExcelUrl: "finance/finPayable/importExcel/299",
        },
        dictOptions:{},
      }
    },

    methods: {
      initDictConfig() {}
    }

  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../common/less/List.less';
</style>
