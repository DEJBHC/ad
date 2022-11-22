<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select v-model="queryParam.warehouseId" dict="bas_warehouse,aux_name,id" pidValue="0"
                             hasChildField="has_child" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="物料">
              <j-search-select-tag v-model="queryParam.materialId" :async="true" dict="bas_material,aux_name,id" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="10" :lg="11" :md="12" :sm="24">
              <a-form-item label="批次">
                <a-input placeholder="请输入最小值" class="query-group-cust" v-model="queryParam.batchNo_begin"/>
                <span class="query-group-split-cust"></span>
                <a-input placeholder="请输入最大值" class="query-group-cust" v-model="queryParam.batchNo_end"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="供应商">
                <j-search-select-tag v-model="queryParam.supplierId" :async="true" dict="bas_supplier,aux_name,id" placeholder="请选择"/>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="6" :md="7" :sm="24">
              <a-form-item label="已关闭">
                <j-dict-select-tag placeholder="请选择" v-model="queryParam.isClosed" dictCode="yn"/>
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
      <a-button type="link"  icon="download" @click="handleExportXls('详细即时库存')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="link" icon="import">导入</a-button>
      </a-upload>

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

        <a slot="materialCode" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <span slot="action" slot-scope="text, record">
          <a @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
           </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <stkInventory-modal ref="modalForm" @ok="modalFormOk"></stkInventory-modal>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import TableColumnsSetter from '../common/components/TableColumnsSetter'
  import StkInventoryModal from './modules/StkInventoryModal'
  import XEUtils from "xe-utils";

  export default {
    name: "StkInventoryList",
    mixins:[JeecgListMixin, ListMixin],
    components: {TableColumnsSetter, StkInventoryModal},

    data () {
      return {
        description: '详细库存',
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
            title:'物料编码',
            width:120,
            align:"left",
            fixed: 'left',
            dataIndex: 'materialCode',
            scopedSlots: { customRender: 'materialCode' },
            sorter: true
          },
          {
            title:'物料名称',
            width:160,
            align:"left",
            fixed: 'left',
            dataIndex: 'materialId_dictText',
            sorter: true
          },
          {
            title:'规格型号',
            width:200,
            align:"left",
            dataIndex: 'materialModel',
          },
          {
            title:'批次',
            width:240,
            align:"center",
            dataIndex: 'batchNo',
            sorter: true
          },
          {
            title:'仓库',
            width:160,
            align:"left",
            dataIndex: 'warehouseId_dictText',
            sorter: true
          },
          {
            title:'单位',
            width:80,
            align:"center",
            dataIndex: 'unitId_dictText'
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
            dataIndex: 'unitCost',
            customRender: t => XEUtils.commafy(t,{digits: 2})
          },
          {
            title:'单个供应商',
            width:90,
            align:"center",
            dataIndex: 'isSingleSupplier_dictText'
          },
          {
            title:'最后供应商',
            align:"left",
            dataIndex: 'supplierId_dictText',
            sorter: true
          },
          {
            title:'已关闭',
            width:80,
            align:"center",
            dataIndex: 'isClosed_dictText'
          },
          {
            title:'创建时间',
            width:100,
            align:"center",
            dataIndex: 'createTime',
            customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
          },
          {
            title:'创建部门',
            width:120,
            align:"center",
            dataIndex: 'sysOrgCode_dictText'
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
          {
            title:'操作',
            dataIndex:'action',
            fixed:'right',
            width: 120,
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/stock/stkInventory/list",
          delete: "/stock/stkInventory/delete",
          deleteBatch: "/stock/stkInventory/deleteBatch",
          exportXlsUrl: "/stock/stkInventory/exportXls",
          importExcelUrl: "stock/stkInventory/importExcel",
        },
        dictOptions:{},
      }
    },

    methods: {
      initDictConfig(){},
    }
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../common/less/List.less';
</style>
