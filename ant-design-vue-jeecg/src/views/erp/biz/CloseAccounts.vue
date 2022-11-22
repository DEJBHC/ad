<template>
  <a-card :bordered="false">
    <a-spin :spinning="loading">
      <div style="margin-top: 24px">
        <a-form-model ref="form" :model="model">
          <a-form-model-item label="当前年度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="year">
            <a-input v-model="model.year" :readOnly="true" />
          </a-form-model-item>
          <a-form-model-item label="当前月度" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="month">
            <a-input v-model="model.month" :readOnly="true" />
          </a-form-model-item>
          <a-form-model-item :wrapperCol="{span: 24}" style="text-align: center">
            <a-button @click="handleBackPeriod" :disabled="disabled" >月度回退</a-button>
            <a-button @click="handleCloseAccount" :disabled="disabled" type="primary" style="margin-left: 24px">月度结账</a-button>
          </a-form-model-item>
          <a-form-model-item :wrapperCol="{span: 24}" style="text-align: center">
            <a-textarea v-model="helpMsg" :read-only="true"  :auto-size="{minRows: 5, maxRows: 5}" style="width:540px; border: none"/>
          </a-form-model-item>
        </a-form-model>
      </div>
    </a-spin>
  </a-card>
</template>

<script>
  import { getAction, putAction } from '@/api/manage'

  export default {
    name: "CloseAccounts",
    inject:['closeCurrent'],
    components: {},
    data () {
      return {
        model: {year: 0, month: 0},

        helpMsg: '说明\n' +
          '· 当前年月之前的月度为已结账，不能新建和作废单据日期在已结账期间的单据。\n' +
          '· 当前年月及之后的月度为未结账，可以新建和作废单据日期在未结账期间的单据。\n' +
          '· 月度结账：结账成功后，当前年月增加一个月。\n' +
          '· 月度回退：回退成功后，当前年月减少一个月。',

        labelCol: {
          xl: { span: 10 },
          lg: { span: 10 },
          md: { span: 10 },
          sm: { span: 10 },
          xs: { span: 24 },
        },
        wrapperCol: {
          xl: { span: 5 },
          lg: { span: 6 },
          md: { span: 8 },
          sm: { span: 8 },
          xs: { span: 24 },
        },

        loading: false,
        disabled: false,
        url: {
          query: "/base/basBizPeriod/query",
          backPeriod: "/base/basBizPeriod/backPeriod",
          closeAccount: "/biz/closeAccounts/month",
        }
      }
    },

    created () {
      this.loading = true;
      getAction(this.url.query, {}).then(res => {
        if (res.success) {
          this.model.year = res.result.year;
          this.model.month = res.result.month;
        }
        else{
          this.$message.warning(res.message)
        }
      }).finally(() => {
        this.loading = false
      })
    },

    methods: {
      handleCloseAccount() {
        this.$confirm({
          title:'月度结账',
          content: `当前业务期间为${this.model.year}年${this.model.month}月, 确定进行结账？`,
          onOk: () => this.closeAccount(),
        });
      },

      closeAccount() {
        this.loading = true;
        let params = {year: this.model.year, month: this.model.month};
        putAction(this.url.closeAccount, params).then((res)=>{
          if(res.success){
            this.$store.commit('SET_BIZ_PERIOD', res.result);
            this.$info({
              title: '月度结账',
              content: '月度结账成功！',
              okText: '确定',
              onOk: () => this.closeCurrent(), //closeCurrent 参考：src/views/jeecg/SelectDemo.vue
            });
          }else{
            this.$warning({
              title: '月度结账',
              content: res.message,
            });
          }
        }).finally(() => {
          this.loading = false;
        })
      },

      handleBackPeriod(){
        this.$confirm({
          title:'当前月度回退',
          content: `当前业务期间为${this.model.year}年${this.model.month}月，, 确定进行回退？`,
          onOk: () => this.backPeriod(),
        });
      },

      backPeriod() {
        this.loading = true;
        let params = {year: this.model.year, month: this.model.month};
        putAction(this.url.backPeriod, params).then((res)=>{
          if(res.success){
            this.$store.commit('SET_BIZ_PERIOD', res.result);
            this.$info({
              title: '月度回退',
              content: '月度回退成功！',
              okText: '确定',
              onOk: () => this.closeCurrent(),
            });
          }else{
            this.$warning({
              title:'月度回退',
              content: res.message,
            });
          }
        }).finally(() => {
          this.loading = false;
        })
      },

    }
  }
</script>