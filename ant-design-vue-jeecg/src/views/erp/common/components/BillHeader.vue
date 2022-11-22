<template>
  <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-row>
        <a-col :span="8">
          <a-form-model-item label="单据编号" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="billNo">
            <a-input v-model="model.billNo" :readOnly="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="6">
          <a-form-model-item label="单据日期" :labelCol="{span:8}" :wrapperCol="{span:10}" prop="billDate">
            <!--JDate 清空或键盘删空，即使输入了合法日期，必填校验提示仍一直显示，故设置allowClear=false和inputReadOnly=true-->
            <j-date v-model="model.billDate" :readOnly="disabled" placeholder="请选择日期" style="width: 100%"
                    :allowClear="false" :inputReadOnly="true" />
          </a-form-model-item>
        </a-col>
        <a-col :span="2">
          <a-form-model-item label="阶段" :labelCol="{span:0}" :wrapperCol="{span:24}" prop="billStage">
            <j-dict-select-tag v-model="model.billStage" dictCode="x_bill_stage" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="3">
          <a-form-model-item label="已生效" :labelCol="{span:16}" :wrapperCol="{span:6}" prop="isEffective">
            <a-select v-model="model.isEffective" :options="ynOptions" :show-arrow="false" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="2">
          <a-form-model-item label="已关闭" :labelCol="{span:15}" :wrapperCol="{span:9}" prop="isClosed">
            <a-select v-model="model.isClosed" :options="ynOptions" :show-arrow="false" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="3">
          <a-row>
            <a-col :span="20">
              <a-form-model-item label="已作废" :labelCol="{span:14}" :wrapperCol="{span:7}" prop="isVoided">
                <a-select v-model="model.isVoided" :options="ynOptions" :show-arrow="false" :disabled="true"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="4">
              <a @click="handleMoreStatus" style="float: right; margin-top: 10px">
                <a-icon :type="moreStatus_ ? 'up' : 'down'"/>
              </a>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
      <a-row v-show="moreStatus">
        <a-col :span="8">
          <a-form-model-item label="生效时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="effectiveTime">
            <j-date v-model="model.effectiveTime" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="核批人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="approver">
            <j-dict-select-tag v-model="model.approver" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="审批实例" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="bpmiInstanceId">
            <a-input v-model="model.bpmiInstanceId" :readOnly="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="制单时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createTime">
            <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="制单部门" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="sysOrgCode">
            <j-dict-select-tag v-model="model.sysOrgCode" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="制单人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="createBy">
            <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="修改时间" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateTime">
            <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="8">
          <a-form-model-item label="修改人" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="updateBy">
            <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="4">
          <a-form-model-item label="自动单据" :labelCol="labelCol6" :wrapperCol="wrapperCol6" prop="isAuto">
            <j-dict-select-tag v-model="model.isAuto" dictCode="yn" :disabled="true"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="4">
          <a-form-model-item label="红字单据" :labelCol="labelCol6" :wrapperCol="wrapperCol6" prop="isRubric">
            <j-dict-select-tag v-model="model.isRubric" dictCode="yn" :disabled="disabled || isRubricDisabled"/>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
</template>

<script>
  import { BillFormGridMixin } from '../mixins/BillFormMixin'
  import { putAction } from '@/api/manage'

  export default {
    name: "BillHeader",
    mixins: [BillFormGridMixin],
    props: {
      model: {
        type: Object,
        required: true
      },
      disabled: {//表单禁用
        type: Boolean,
        default: false,
      },
      isRubricDisabled: {//isRubric是否禁止修改
        type: Boolean,
        default: true,
      },
      moreStatus: {
        type: Boolean,
        default: false
      }
    },

    data() {
      return {
        moreStatus_: false,

        validatorRules: {
          billNo: [{required: true, message: '请输入单据编号!'}],
          billDate: [{required: true, message: '请选择单据日期!'}],
        },

        ynOptions:[{value: 0, label: '否'}, {value: 1, label: '是'}]
      }
    },

    mounted() {
      this.moreStatus_ = this.moreStatus;
    },

    methods: {
      //生成billNo：在单据的addAfter()中调用
      fillBillNo(ruleCode, callback) {
        // 请求后台的填值规则接口地址
        const url = '/sys/fillRule/executeRuleByCode/';
        putAction(url + ruleCode, {}).then(res => {
          if (res.success) {
            this.model.billNo = res.result;

            if (typeof callback === 'function') callback(this.model.billNo);
          }
        })
      },

      handleMoreStatus() {
        this.moreStatus_ = !this.moreStatus_;
        this.$emit("update:moreStatus", this.moreStatus_);
      },

    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
