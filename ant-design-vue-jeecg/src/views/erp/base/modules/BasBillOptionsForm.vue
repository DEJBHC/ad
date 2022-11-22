<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="单据类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billType">
              <j-dict-select-tag v-model="model.billType" dictCode="x_bill_type" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="手动单据核批类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="mbillApprovalType">
              <j-dict-select-tag v-model="model.mbillApprovalType" dictCode="x_approval_type" placeholder="请选择":disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="自动单据核批类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="abillApprovalType">
              <j-dict-select-tag v-model="model.abillApprovalType" dictCode="x_approval_type" placeholder="请选择":disabled="disabled" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="审批BPM类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bpmType">
              <j-dict-select-tag v-model="model.bpmType" dictCode="x_bpm_type" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="生效后自动执行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isAutoExecute">
              <j-dict-select-tag v-model="model.isAutoExecute" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="自动结束执行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isAutoEndExecute">
              <j-dict-select-tag v-model="model.isAutoEndExecute" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="执行完后自动关闭" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isAutoClose">
              <j-dict-select-tag v-model="model.isAutoClose" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
       </a-row>

        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="0" header="操作信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
                  <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
                  <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="修改时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
                  <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" style="width: 100%" disabled/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="修改人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
                  <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </div>
  </a-spin>
</template>

<script>
  import { validateDuplicateValue } from '@/utils/util'
  import {FormMixin} from '../../common/mixins/FormMixin'

  export default {
    name: 'BasBillOptionsForm',
    mixins: [FormMixin],

    data () {
      return {
        model: {
          mbillApprovalType: '1',
          abillApprovalType: '0',
          bpmType: '',
          isAutoExecute: 1,
          isAutoEndExecute: 1,
          isAutoClose: 1
        },

        validatorRules: {
          billType: [
            { required: true, message: '请选择!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_bill_options', 'bill_type', value, this.model.id, callback) }
          ],
          mbillApprovalType: [{ required: true, message: '请选择!'},],
          abillApprovalType: [{ required: true, message: '请选择!'},],
          isAutoExecute: [{ required: true, message: '请选择!'},],
          isAutoEndExecute: [{ required: true, message: '请选择!'},],
          isAutoClose: [{ required: true, message: '请选择!'},],
        },
        url: {
          add: "/base/basBillOptions/add",
          edit: "/base/basBillOptions/edit",
        }
      }
    },

    watch: {
      'model.mbillApprovalType'() {
        if (this.model.mbillApprovalType !== '2' && this.model.abillApprovalType !== '2') this.model.bpmType = '';
      },

      'model.abillApprovalType'() {
        if (this.model.mbillApprovalType !== '2' && this.model.abillApprovalType !== '2') this.model.bpmType = '';
      }
    }

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
