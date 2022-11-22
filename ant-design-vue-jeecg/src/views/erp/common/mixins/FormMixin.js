import { httpAction } from '@/api/manage'
import {} from "../../common/utils/util"; //执行Date.prototype.format等

export const FormMixin = {
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
      model: {},

      confirmLoading: false,
      collapseActiveKey: '',
      collapseStyle: 'background: #ffffff; border-radius: 4px; margin-bottom: 8px; border: 0; overflow: hidden',

      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
  },

  created () {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model));
  },

  methods: {
    add () {
      this.edit(this.modelDefault);
    },
    edit (record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    submitForm () {
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

    onCodeNameBlur(val) {
      if (!this.model.code || this.model.code.length === 0 || !this.model.name || this.model.name.length === 0)
        return;
      if (!this.model.auxName || this.model.auxName.length === 0)
        this.model.auxName = this.model.code + ' ' + this.model.name;
    }
  }
 };

