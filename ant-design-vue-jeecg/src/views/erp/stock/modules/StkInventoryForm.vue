<template>
  <a-spin :spinning="confirmLoading">
    <div>
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="物料编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialCode">
              <a-input v-model="model.materialCode" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="物料名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialId">
              <j-search-select-tag v-model="model.materialId" dict="bas_material,name,id" disabled />
            </a-form-model-item>
          </a-col>
          <a-form-model-item label="规格型号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialModel">
            <a-input v-model="model.materialModel" disabled />
          </a-form-model-item>
          <a-col :span="24">
            <a-form-model-item label="批次" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="batchNo">
              <a-input v-model="model.batchNo" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="warehouseId">
              <j-dict-select-tag v-model="model.warehouseId" dictCode="bas_warehouse,aux_name,id" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unitId">
              <j-dict-select-tag v-model="model.unitId" dictCode="bas_unit,name,id" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="qty">
              <a-input-number
                v-model="model.qty"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :precision="3" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="金额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="cost">
              <a-input-number
                v-model="model.cost"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :precision="2" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="单位金额" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unitCost">
              <a-input-number
                v-model="model.unitCost"
                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                :precision="2" style="width: 100%" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="单个供应商" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isSingleSupplier">
              <j-dict-select-tag v-model="model.isSingleSupplier" dictCode="yn" placeholder="请选择" :disabled="disabled"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="最后供应商" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="supplierId">
              <j-dict-select-tag v-model="model.supplierId" dictCode="bas_supplier,aux_name,id" disabled/>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="已关闭" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isClosed">
              <j-dict-select-tag v-model="model.isClosed" dictCode="yn" placeholder="请选择" :disabled="disabled" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="0" header="操作信息" :style="collapseStyle">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
                  <j-date v-model="model.createTime" :disabled="true" style="width: 100%"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="创建部门" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sysOrgCode">
                  <j-dict-select-tag v-model="model.sysOrgCode" dictCode="sys_depart,depart_name,org_code" :disabled="true"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
                  <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
                </a-form-model-item>
              </a-col>
              <a-col :span="24">
                <a-form-model-item label="修改时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
                  <j-date v-model="model.updateTime" :disabled="true" style="width: 100%"/>
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
  import {FormMixin} from '../../common/mixins/FormMixin'

  export default {
    name: "StkInventoryForm",
    mixins: [FormMixin],

    data () {
      return {
        isSingleSupplier: false,
        model: {qty: 0, cost: 0, isSingleSupplier:0, isClosed:0},
        validatorRules: {
          isSingleSupplier: [{required: true, message: '请选择是否单供应商!'}],
          isClosed: [{required: true, message: '请选择是否关闭!'}],
        },

        url: {
          edit: "/stock/stkInventory/edit",
        }
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Form.less";
</style>
