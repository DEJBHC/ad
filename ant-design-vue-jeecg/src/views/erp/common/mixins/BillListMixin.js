import { putAction } from '@/api/manage'
import {printReport} from "../../common/utils/util";
import ATableDragResize from './ATableDragResize'

export const BillListMixin = {
  mixins: [ATableDragResize],

  data(){
    return {
      //单据高级查询字段
      superFieldList: [
        {type:"string",value:"billNo",text:"单据编号"},
        {type:"date",value:"billDate",text:"单据日期"},
        {type:'string',value:'billStage',text:'单据阶段',dictCode:'x_bill_stage'},
        {type:'int',value:'isEffective',text:'已生效',dictCode:'yn'},
        {type:'int',value:'isClosed',text:'已关闭',dictCode:'yn'},
        {type:'int',value:'isVoided',text:'已作废',dictCode:'yn'},
        {type:'int',value:'isRubric',text:'红字单据',dictCode:'yn'},
        {type:'string',value:'srcBillType',text:'源单类型',dictCode:'x_bill_type'},
        {type:'string',value:'srcNo',text:'源单号'},
        {type:'string',value:'remark',text:'备注'},
        {type:'date',value:'createTime',text:'制单时间'},
        {type:'string',value:'createBy',text:'制单人',dictCode:"sys_user,realname,username"},
        {type:'string',value:'sysOrgCode',text:'制单部门',dictCode:"sys_depart,depart_name,org_code"},
        {type:'date',value:'updateTime',text:'修改时间'},
        {type:'string',value:'updateBy',text:'修改人',dictCode:"sys_user,realname,username"},
        {type:'date',value:'effectiveTime',text:'生效时间'},
        {type:'string',value:'approver',text:'核批人',dictCode:"sys_user,realname,username"},
        {type:'string',value:'approvalResultType',text:'核批结果类型',dictCode:"x_approval_result_type"},
      ]
    }
  },

  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },

    isBatchEnabled() {
      return function (action) {
        for (let i = 0; i < this.selectedRowKeys.length; i++) {
          if (!this.selectionRows[i].actions[action]) return false;
        }
        return true;
      };
    },
  },

  methods: {
    myHandleAdd() {
      this.$refs.modalForm.action = "add";
      this.handleAdd();
    },
    myHandleEdit(record) {
      this.$refs.modalForm.action = "edit";
      this.handleEdit(record);
    },
    myHandleDetail(record) {
      this.$refs.modalForm.action = "detail";
      this.handleDetail(record);
    },
    handleCheck(record) {
      this.$refs.modalForm.action = "check";
      record.approvalRemark = null;
      record.approvalResultType = null;
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "审核";
      this.$refs.modalForm.disableSubmit = true;
    },
    handleEbpm(record) {
      this.$refs.modalForm.action = "ebpm";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "结束审批";
      this.$refs.modalForm.disableSubmit = true;
    },
    handleExecute(record) {
      this.$refs.modalForm.action = "execute";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "执行";
      this.$refs.modalForm.disableSubmit = true;
    },
    batchClose() {
      if (this.selectedRowKeys.length <= 0) return;
      let ids = "";
      for (let i = 0; i < this.selectedRowKeys.length; i++)  ids += this.selectedRowKeys[i] + ",";

      const that = this;
      this.$confirm({
        title: "确认关闭",
        content: "是否关闭选中单据?",
        onOk: function () {
          that.loading = true;
          putAction(that.url.closeBatch, {ids: ids}).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.loadData();
              that.onClearSelected(); //触发重新计算 isBatchEnabled
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.loading = false;
          });
        }
      });
    },
    handleClose(id) {
      const that = this;
      putAction(that.url.close, {id: id}).then(res => {
        if (res.success) {
          that.$message.success(res.message);
          that.loadData();
        } else {
          that.$message.warning(res.message);
        }
      })
    },
    batchUnclose() {
      if (this.selectedRowKeys.length <= 0) return;
      let ids = "";
      for (let i = 0; i < this.selectedRowKeys.length; i++)  ids += this.selectedRowKeys[i] + ",";

      const that = this;
      this.$confirm({
        title: "确认反关闭",
        content: "是否反关闭选中单据?",
        onOk: function () {
          that.loading = true;
          putAction(that.url.uncloseBatch, {ids: ids}).then(res => {
            if (res.success) {
              that.$message.success(res.message);
              that.loadData();
              that.onClearSelected(); //触发重新计算 isBatchEnabled
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.loading = false;
          });
        }
      });
    },
    handleUnclose(id) {
      const that = this;
      putAction(that.url.unclose, {id: id}).then(res => {
        if (res.success) {
          that.$message.success(res.message);
          that.loadData();
        } else {
          that.$message.warning(res.message);
        }
      })
    },
    handleVoid(record) {
      this.$refs.modalForm.action = "void";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "作废";
      this.$refs.modalForm.disableSubmit = true;
    },
    handlePrint(id) {
      printReport(this.billReportId,{id: id});
    },
  }
}