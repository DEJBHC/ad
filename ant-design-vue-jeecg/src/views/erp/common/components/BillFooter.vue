<template>
  <a-form-model ref="form" :model="model" style="margin-top: 24px">
    <a-row>
      <a-col :span="12">
          <a-row>
            <a-col :span="24">
              <a-form-model-item label="备注" :labelCol="labelCol1" :wrapperCol="wrapperCol2" prop="remark">
                <a-textarea v-model="model.remark" :readOnly="disabled" rows="1" autoSize></a-textarea>
              </a-form-model-item>
            </a-col>
            <a-col :span="24">
              <a-form-model-item label="附件" :labelCol="labelCol1" :wrapperCol="wrapperCol2" prop="attachment">
                <j-upload v-model="model.attachment" :disabled="disabled"></j-upload>
              </a-form-model-item>
            </a-col>
          </a-row>
      </a-col>
      <a-col :span="12" v-show="action!=='add' && action!=='edit'">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="核批意见" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="approvalRemark">
              <a-textarea v-model="model.approvalRemark" :readOnly="action!=='check' && action!=='ebpm'" rows="1" autoSize></a-textarea>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="核批结果" :labelCol="labelCol2" :wrapperCol="wrapperCol2" prop="approvalResultType">
              <j-dict-select-tag v-model="model.approvalResultType" dictCode="x_approval_result_type"
                :disabled="action!=='check' && action!=='ebpm'"placeholder="请选择"/>
            </a-form-model-item>
        </a-col>
        </a-row>
      </a-col>
    </a-row>
  </a-form-model>
</template>

<script>
  import { BillFormGridMixin } from '../mixins/BillFormMixin'

  export default {
    name: "BillHeader",
    mixins: [BillFormGridMixin],
    props: {
      model: {
        type: Object,
        required: true
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      action: {
        type: String,
        required: true
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
