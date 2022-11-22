<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="代码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="本币" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isFunctional">
              <j-dict-select-tag v-model="model.isFunctional" dictCode="yn" placeholder="请选择":disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="启用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isEnabled">
              <j-dict-select-tag v-model="model.isEnabled" dictCode="yn" placeholder="请选择":disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" placeholder="请输入" :disabled="disabled"/>
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
    name: "BasCurrencyForm",
    mixins: [FormMixin],

    data () {
      return {
        model: {
          isEnabled: 1
        },
        validatorRules: {
          code: [
            { required: true, message: '请输入编码!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_currency', 'code', value, this.model.id, callback) }
          ],
          name: [
            { required: true, message: '请输入名称!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_currency', 'name', value, this.model.id, callback) }
          ],
          isFunctional: [{required: true, message: '请选择是否本币!'},],
          isEnabled: [{ required: true, message: '请选择是否启用!'},],
        },
        url: {
          add: "/base/basCurrency/add",
          edit: "/base/basCurrency/edit",
        }
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
