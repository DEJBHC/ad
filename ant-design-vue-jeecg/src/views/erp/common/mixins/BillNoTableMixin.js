import {httpAction} from "@/api/manage";

/** 单表Form的mixin
 *  单表Form和一对多Form的mixin：，实现
 *   ·单表：   BillFormMixin, BillFormGridMixin, BillNoTableMixin
 *   ·一对多： BillFormMixin, BillFormGridMixin, JVxeTableModelMixin, BillVxeTableMixin
 *   其中BillNoTableMixin类似一对多的JVxeTableModelMixin，实现add、edit、handleOk等方法。
 */
export const BillNoTableMixin = {
  data() {
    return {
      model:{},
      confirmLoading: false,
    }
  },

  methods: {
    add () {
      if (typeof this.addAfter === 'function') this.addAfter(this.model)
      this.edit(this.model);
    },

    edit (record) {
      this.visible = true;
      this.$refs.form.resetFields();
      this.model = Object.assign({}, record);
      if (typeof this.editAfter === 'function') this.editAfter(this.model);
    },

    handleOk () {
      const that = this;
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if(!this.model.id){
            httpurl+=this.url.add;
            method = 'post';
          }else{
            httpurl+=this.url.edit;
            method = 'put';
          }
          httpAction(httpurl,this.model,method).then((res)=>{
            if(res.success){
              that.$message.success(res.message);
              that.$emit('ok');
            }else{
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          })
        }
      })
    },
  }
}