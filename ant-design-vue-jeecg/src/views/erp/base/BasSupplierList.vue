<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="编码">
              <a-input placeholder="请输入" v-model="queryParam.code"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入" v-model="queryParam.name"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="供应商分类">
                <j-dict-select-tag placeholder="请选择" v-model="queryParam.supplierCategory" dictCode="x_supplier_category"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="供应商等级">
                <j-dict-select-tag placeholder="请选择" v-model="queryParam.supplierLevel" dictCode="x_supplier_level"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="纳税规模">
                <j-dict-select-tag placeholder="请选择" v-model="queryParam.taxScale" dictCode="x_tax_scale"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="所属地区">
                <j-area-linkage type="cascader" v-model="queryParam.area" placeholder="请选择"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="业务区域">
                <j-area-linkage type="cascader" v-model="queryParam.bizArea" placeholder="请选择"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <j-super-query v-show="toggleSearchStatus || superQueryFlag" style="margin-left: 16px"
                             :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
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
      <a-button type="link" icon="download" @click="handleExportXls('供应商')">导出</a-button>
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
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        :components="drag(columns)"
        @change="handleTableChange">

        <a slot="code" @click="myHandleDetail(record)" slot-scope="text, record">{{text}}</a>

        <template slot="pcaSlot" slot-scope="text">
          <div>{{ getPcaText(text) }}</div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="myHandleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多<a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">删除</a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <bas-supplier-modal ref="modalForm" @ok="modalFormOk"></bas-supplier-modal>
  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { ListMixin } from '../common/mixins/ListMixin'
  import BasSupplierModal from './modules/BasSupplierModal'
  import TableColumnsSetter from "../common/components/TableColumnsSetter";
  import Area from '@/components/_util/Area'

  export default {
    name: 'BasSupplierList',
    mixins:[JeecgListMixin, ListMixin],
    components: {TableColumnsSetter, BasSupplierModal},

    data () {
      return {
        description: '供应商',
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
            title:'编码',
            align:"center",
            sorter: true,
            dataIndex: 'code',
            scopedSlots: { customRender: 'code' },
          },
          {
            title:'名称',
            align:"left",
            sorter: true,
            dataIndex: 'name'
          },
          {
            title:'助记名',
            align:"left",
            dataIndex: 'auxName',
            sorter: true
          },
          {
            title:'分类',
            width:120,
            align:"center",
            dataIndex: 'supplierCategory_dictText',
            sorter: true
          },
          {
            title:'等级',
            width:75,
            align:"center",
            dataIndex: 'supplierLevel_dictText'
          },
          {
            title:'纳税规模',
            width:120,
            align:"center",
            dataIndex: 'taxScale_dictText'
          },
          {
            title:'所属总公司',
            align:"center",
            sorter: true,
            dataIndex: 'headquarters'
          },
          {
            title:'所属地区',
            align:"center",
            sorter: true,
            dataIndex: 'area',
            scopedSlots: {customRender: 'pcaSlot'}
          },
          {
            title:'业务区域',
            align:"center",
            sorter: true,
            dataIndex: 'bizArea',
            scopedSlots: {customRender: 'pcaSlot'}
          },
          {
            title:'供应商地址',
            align:"left",
            dataIndex: 'address'
          },
          {
            title:'供应商网站',
            align:"left",
            dataIndex: 'website'
          },
          {
            title:'备选供应商',
            align:"left",
            dataIndex: 'alterSuppliers'
          },
          {
            title:'启用',
            width:75,
            align:"center",
            dataIndex: 'isEnabled_dictText'
          },
          {
            title:'备注',
            align:"left",
            ellipsis: true,
            dataIndex: 'remark'
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
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:120,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/base/basSupplier/list",
          delete: "/base/basSupplier/delete",
          deleteBatch: "/base/basSupplier/deleteBatch",
          exportXlsUrl: "/base/basSupplier/exportXls",
          importExcelUrl: "bas/basSupplier/importExcel",

        },
        dictOptions:{},
        pcaData:'',
        superFieldList:[],
      }
    },
    created() {
      this.pcaData = new Area()
    this.getSuperFieldList();
    },

    methods: {
      getPcaText(code){
        return this.pcaData.getText(code);
      },
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'code',text:'编码',dictCode:''})
        fieldList.push({type:'string',value:'name',text:'名称',dictCode:''})
        fieldList.push({type:'string',value:'shortName',text:'简称',dictCode:''})
        fieldList.push({type:'string',value:'auxName',text:'辅助名称',dictCode:''})
        fieldList.push({type:'string',value:'supplierCategory',text:'供应商分类',dictCode:'x_supplier_category'})
        fieldList.push({type:'string',value:'supplierLevel',text:'供应商等级',dictCode:'x_supplier_level'})
        fieldList.push({type:'string',value:'taxScale',text:'纳税规模',dictCode:'x_tax_scale'})
        fieldList.push({type:'string',value:'headquarters',text:'所属总公司',dictCode:''})
        fieldList.push({type:'pca',value:'area',text:'所属地区'})
        fieldList.push({type:'pca',value:'bizArea',text:'业务区域'})
        fieldList.push({type:'string',value:'address',text:'客户地址',dictCode:''})
        fieldList.push({type:'string',value:'website',text:'客户网站',dictCode:''})
        fieldList.push({type:'string',value:'legalPerson',text:'法人代表',dictCode:''})
        fieldList.push({type:'string',value:'legalPersonPhone',text:'法人电话',dictCode:''})
        fieldList.push({type:'string',value:'financialContacts',text:'财务信息联系人',dictCode:''})
        fieldList.push({type:'string',value:'financialPhone',text:'财务信息联系电话',dictCode:''})
        fieldList.push({type:'string',value:'invoiceCompany',text:'开票信息单位名称',dictCode:''})
        fieldList.push({type:'string',value:'invoiceTaxCode',text:'开票信息税号',dictCode:''})
        fieldList.push({type:'string',value:'invoiceBankName',text:'开票信息开户行',dictCode:''})
        fieldList.push({type:'string',value:'invoiceBankCode',text:'开票信息银行账号',dictCode:''})
        fieldList.push({type:'string',value:'invoiceAccount',text:'开票信息账号',dictCode:''})
        fieldList.push({type:'string',value:'invoicePhone',text:'开票信息联系电话',dictCode:''})
        fieldList.push({type:'string',value:'invoiceAddress',text:'开票地址',dictCode:''})
        fieldList.push({type:'string',value:'receiptCompany',text:'办款资料单位名称',dictCode:''})
        fieldList.push({type:'string',value:'receiptBankName',text:'办款资料开户行',dictCode:''})
        fieldList.push({type:'string',value:'receiptBankCode',text:'办款资料行号',dictCode:''})
        fieldList.push({type:'string',value:'receiptAccount',text:'办款资料账号',dictCode:''})
        fieldList.push({type:'string',value:'recvName',text:'收件信息收件人',dictCode:''})
        fieldList.push({type:'string',value:'recvPhone',text:'收件信息联系电话',dictCode:''})
        fieldList.push({type:'string',value:'recvFax',text:'收件信息传真',dictCode:''})
        fieldList.push({type:'string',value:'recvEmail',text:'收件信息Email',dictCode:''})
        fieldList.push({type:'string',value:'recvAddress',text:'收件信息地址',dictCode:''})
        fieldList.push({type:'string',value:'recvPostcode',text:'收件信息邮编',dictCode:''})
        fieldList.push({type:'string',value:'alterSuppliers',text:'备选供应商',dictCode:''})
        fieldList.push({type:'int',value:'isEnabled',text:'启用',dictCode:'yn'})
        fieldList.push({type:'string',value:'remark',text:'备注',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>

<style lang="less" scoped>
  @import '~@assets/less/common.less';
  @import '../common/less/List.less';
</style>
