<template>
  <a-drawer
    :title="'计量单位 - ' + title"
    :width="width"
    placement="right"
    :closable="disableSubmit"
    :maskClosable="disableSubmit"
    @close="close"
    destroyOnClose
    :visible="visible">
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-form-model-item label="基准单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="pid">
          <j-tree-select
            ref="treeSelect"
            placeholder=""
            v-model="model.pid"
            dict="bas_unit,name,id"
            pidField="pid"
            pidValue="0"
            hasChildField="has_child"
            disabled>
          </j-tree-select>
        </a-form-model-item>
        <a-form-model-item label="有从属" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="hasChild">
          <j-dict-select-tag v-model="model.hasChild"  dictCode="yn" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
          <a-input v-model="model.name" placeholder="请输入" :disabled="disableSubmit"/>
        </a-form-model-item>
        <a-form-model-item label="符号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="symbol">
          <a-input v-model="model.symbol" placeholder="请输入" :disabled="disableSubmit"/>
        </a-form-model-item>
        <a-form-model-item label="换算系数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="factor">
          <a-input-number v-model="model.factor" placeholder="请输入" style="width: 100%" :disabled="disableSubmit"/>
        </a-form-model-item>
        <a-form-model-item label="启用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isEnabled">
          <j-dict-select-tag v-model="model.isEnabled"  dictCode="yn" placeholder="请选择" :disabled="disableSubmit"/>
        </a-form-model-item>

        <a-collapse v-model:activeKey="collapseActiveKey" :bordered="false">
          <a-collapse-panel key="0" header="操作信息" :style="collapseStyle">
            <a-form-model-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
              <j-date v-model="model.createTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
            </a-form-model-item>
            <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
              <j-dict-select-tag v-model="model.createBy" dictCode="sys_user,realname,username" :disabled="true"/>
            </a-form-model-item>
            <a-form-model-item label="修改时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
              <j-date v-model="model.updateTime" :showTime="true" dateFormat="YYYY-MM-DD HH:mm:ss" :disabled="true" style="width: 100%"/>
            </a-form-model-item>
            <a-form-model-item label="修改人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
              <j-dict-select-tag v-model="model.updateBy" dictCode="sys_user,realname,username" :disabled="true"/>
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-spin>

    <div class="drawer-footer">
      <a-button v-if="!disableSubmit" @click="handleOk" type="primary" style="margin-bottom: 0;">确定</a-button>
      <a-button @click="handleCancel" style="margin-bottom: 0;">{{disableSubmit ? '关闭':'取消'}}</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import { validateDuplicateValue } from '@/utils/util'
  import { httpAction } from '@/api/manage'

  export default {
    name: "BasUnitModal",
    components: {
    },
    data () {
      return {
        title:"",
        width:800,
        visible: false,
        disableSubmit: false,
        model:{},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        collapseActiveKey: '',
        collapseStyle: 'background: #ffffff; border-radius: 4px; margin-bottom: 24px; border: 0; overflow: hidden',

        model: {
          isEnabled: 1
        },
        validatorRules: {
          name: [
            { required: true, message: '请输入名称!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_unit', 'name', value, this.model.id, callback) }
          ],
          symbol: [
            { required: true, message: '请输入符号!'},
            { validator: (rule, value, callback) => validateDuplicateValue('bas_unit', 'symbol', value, this.model.id, callback) }
          ],
          factor: [{ required: true, message: '请输入换算系数!'}, { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},],
          isEnabled: [{ required: true, message: '请输入是否启用!'},],
        },
        url: {
          add: "/base/basUnit/add",
          edit: "/base/basUnit/edit",
        },
        expandedRowKeys:[],
        pidField:"pid"

      }
    },
    created () {
      //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods: {
      add (obj) {
        this.modelDefault.pid=''
        this.edit(Object.assign(this.modelDefault , obj));
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.$refs.form.clearValidate()
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
            if(this.model.id && this.model.id === this.model[this.pidField]){
              that.$message.warning("父级节点不能选择自己");
              that.confirmLoading = false;
              return;
            }
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                this.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }else{
            return false
          }
        })
      },
      handleCancel () {
        this.close()
      },
      submitSuccess(formData,flag){
        if(!formData.id){
          let treeData = this.$refs.treeSelect.getCurrTreeData()
          this.expandedRowKeys=[]
          this.getExpandKeysByPid(formData[this.pidField],treeData,treeData)
          this.$emit('ok',formData,this.expandedRowKeys.reverse());
        }else{
          this.$emit('ok',formData,flag);
        }
      },
      getExpandKeysByPid(pid,arr,all){
        if(pid && arr && arr.length>0){
          for(let i=0;i<arr.length;i++){
            if(arr[i].key==pid){
              this.expandedRowKeys.push(arr[i].key)
              this.getExpandKeysByPid(arr[i]['parentId'],all,all)
            }else{
              this.getExpandKeysByPid(pid,arr[i].children,all)
            }
          }
        }
      }


    }
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Drawer.less";
  @import "../../common/less/Form.less";
</style>