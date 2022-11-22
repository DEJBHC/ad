import { putAction } from '@/api/manage'
import {} from "../../common/utils/util"; //执行Date.prototype.format等

export const BillFormGridMixin = {
  data() {
    return {
      labelCol1: {span: 2},
      wrapperCol1: {span: 22},
      //1_5: 分为1.5列（相当于占了2/3）
      labelCol1_5: { span: 3 },
      wrapperCol1_5: { span: 21 },
      labelCol2: {span: 4},
      wrapperCol2: {span: 20},
      labelCol3: {span: 6},
      wrapperCol3: {span: 18},
      labelCol6: {span: 12},
      wrapperCol6: {span: 12},
    };
  },
}

export const BillFormMixin = {
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },

  data() {
    return {
      action: '',
      moreStatus: false,
    };
  },

  methods: {
    handleSave() {
      if (this.validateHFSubForm()) {
        this.switchUrl('save')
        this.handleOk()
      }
    },

    handleSubmit() {
      if (this.validateHFSubForm()) {
        this.switchUrl('submit')
        this.handleOk()
      }
    },

    handleCheck() {
      if (!this.model.approvalResultType || this.model.approvalResultType.length === 0 ) {
        this.$warning({ title: '审核', content: "请选择核批结果！" })
        return;
      }
      const that = this;
      putAction(that.url.check,
        {
          id: that.model.id,
          approvalResultType: that.model.approvalResultType,
          approvalRemark: that.model.approvalRemark
        }).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '审核单据', content: res.message});
        }
      })
    },

    handleEbpm() {
      if (!this.model.approvalResultType || this.model.approvalResultType.length === 0 ) {
        this.$warning({ title: '审核', content: "请选择核批结果！" })
        return;
      }
      const that = this;
      putAction(that.url.ebpm,
        {
          id: that.model.id,
          approvalResultType: that.model.approvalResultType,
          approvalRemark: that.model.approvalRemark
        }).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '结束审批流程', content: res.message});
        }
      })
    },

    handleExecute() {
      const that = this;
      putAction(that.url.execute, {id: that.model.id}).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '执行单据', content: res.message});
        }
      })
    },


    handleVoid() {
      const that = this;
      putAction(that.url.void, {id: that.model.id}).then((res) => {
        if (res.success) {
          that.$message.success(res.message);
          that.$emit('ok')
          that.close()
        } else {
          that.$warning({title: '作废单据', content: res.message});
        }
      })
    },

    switchUrl(target) {
      if ((this.url._add || '') === '') {
        this.url._add = this.url.add
      }
      if ((this.url._edit || '') === '') {
        this.url._edit = this.url.edit
      }
      this.url.add = this.url._add + '/' + target
      this.url.edit = this.url._edit + '/' + target
    },

    onOperatorChange(val) {
      if (this.$refs.operatorFmi) this.$refs.operatorFmi.onFieldChange();
      setTimeout(()=>{
        const dictOptions = this.$refs.opDept.dictOptions;
        this.model.opDept = dictOptions.length === 1 ? dictOptions[0].value : '';
        if (this.$refs.opDeptFmi) this.$refs.opDeptFmi.onFieldChange();
      }, 1000);
    },

    //校验BillHeader和BillFooter子表单
    validateHFSubForm() {
      let ok = true;
      if (this.$refs.billHeader) {
        this.$refs.billHeader.$refs.form.validate((valid, obj) => {
          ok = valid;
        });
        if (!ok) return false;
      }

      if (this.$refs.billFooter) {
        this.$refs.billFooter.$refs.form.validate((valid, obj) => {
          ok = valid;
        });
      }
      return ok;
    },

    rubricFormModelValidator(rule, value, callback) {
      const v = Number(value);
      if (isNaN(v)) {
        callback();
        return;
      }

      const isRubric = Number(this.model.isRubric);
      if (isRubric === 0 && v < 0) {
        callback('蓝字单据不能为负数');
      } else if (isRubric === 1 && v > 0) {
        callback('红字单据不能为正数');
      } else {
        callback();
      }
    },

  }
 }

