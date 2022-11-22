<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="取消">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="启用年度" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input :readOnly="true" v-decorator="['beginYear']" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="启用月度" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input :readOnly="true" v-decorator="['beginMonth']" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="当前年度" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input :readOnly="true" v-decorator="['year']" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="当前月度" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input :readOnly="true" v-decorator="['month']" style="width: 100%"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
  import { getAction, postAction } from '@/api/manage'
  import pick from 'lodash.pick'


  export default {
    name: "BasMonthlyCloseAccount",
    inject:['closeCurrent'],
    components: {
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"月度结账",
        width:600,
        visible: true,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        url: {
          list: "/base/basBizPeriod/list",
          closeAccount: "/base/basBizPeriod/closeAccount/month",
        }
      }
    },
    created () {
      this.loading = true;
      getAction(this.url.list, {}).then((res) => {
        if (res.success) {
          this.edit(res.result.records[0])
        }
        if(res.code===510){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },

    methods: {
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'beginYear','beginMonth','year','month'))
        })
      },

      close () {
        this.$emit('close');
        this.visible = false;
        // 关闭当前选项卡页面，参看：src/views/jeecg/SelectDemo.vue
        this.closeCurrent();
      },

      handleOk () {
        const that = this;
        that.confirmLoading = true;
        let formData = {year: this.model.year, month: this.model.month};
        postAction(this.url.closeAccount, formData).then((res)=>{
          if(res.success){
            that.$confirm({
              title:"月度结账",
              content: res.message,
              onOk: () => {that.$emit('ok'); that.close();},
            });
          }else{
            that.$warning({
              title:"月度结账",
              content: res.message,
            });
          }
        }).finally(() => {
          that.confirmLoading = false;
        })
      },

      handleCancel () {
        this.close()
      },

      popupCallback(row){
        this.form.setFieldsValue(pick(row,'beginYear','beginMonth','year','month'))
      },

    }
  }
</script>