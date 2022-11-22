<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入" @blur="onCodeNameBlur" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="助记名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="auxName">
              <a-input v-model="model.auxName" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="分类" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="categoryId">
              <j-tree-select
                ref="treeSelect"
                placeholder="请选择"
                v-model="model.categoryId"
                dict="bas_material_category,name,id"
                pidValue="0"
                hasChildField="has_child"
                :disabled="disabled"
              >
              </j-tree-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="规格型号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="model">
              <a-input v-model="model.model" placeholder="请输入" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="计量单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unitId">
              <j-dict-select-tag v-model="model.unitId" dictCode="bas_unit,name,id" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="销售价格" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="salePrice">
              <a-input-number
                v-model="model.salePrice"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                :precision="2" style="width: 100%" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="税控编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="taxCoce">
              <a-input v-model="model.taxCoce" placeholder="请输入" :disabled="disabled"/>
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
    name: "BasMaterialForm",
    mixins: [FormMixin],

    data () {
      return {
        model: {
          code: '',
          name: '',
          auxName: '',
          isEnabled: 1,
        },

        validatorRules: {
          code: [
            { required: true, message: '请输入编码!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_material', 'code', value, this.model.id, callback) }
          ],
          name: [
            { required: true, message: '请输入名称!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_material', 'name', value, this.model.id, callback) }
          ],
          auxName: [
            { required: true, message: '请输入助记名!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_material', 'aux_name', value, this.model.id, callback) }
          ],
          categoryId: [{ required: true, message: '请选择分类!'},],
          unitId: [{ required: true, message: '请选择计量单位!'},],
          isEnabled: [{ required: true, message: '请选择是否启用!'},],
        },

        url: {
          add: "/base/basMaterial/add",
          edit: "/base/basMaterial/edit",
        }
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
