<template>
  <j-modal
    :title="'其他出库 - ' + title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    draggable
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">

    <template slot="footer">
      <a-button v-if="disableSubmit" key="print" @click="handlePrint" style="margin-right: 48px">打印</a-button>
      <a-button @click="handleCancel" :type="action==='detail'?'primary':''">{{action==='detail'?'关闭':'取消'}}</a-button>
      <a-button v-if="!disableSubmit" key="save" @click="handleSave" type="primary">保存</a-button>
      <a-button v-if="!disableSubmit" key="submit" @click="handleSubmit" type="primary">提交</a-button>
      <a-button v-if="action==='check'" key="check" @click="handleCheck" type="primary">审核</a-button>
      <a-button v-if="action==='ebpm'" key="ebpm" @click="handleEbpm" type="primary">结束审批</a-button>
      <a-button v-if="action==='execute'" key="execute" @click="handleExecute" type="primary">执行</a-button>
      <a-popconfirm  title="作废后不可恢复，确定要作废？" @confirm="handleVoid" okText="确定" cancelText="取消">
        <a-button v-if="action==='void'" key="void" type="primary">作废</a-button>
      </a-popconfirm>
    </template>
    <other-out-bill-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
   </j-modal>
</template>

<script>

  import { BillModalMixin } from '../../common/mixins/BillModalMixin'
  import OtherOutBillForm from "./OtherOutForm";

  export default {
    name: 'PurInModal',
    mixins: [ BillModalMixin ],
    components: { OtherOutBillForm },

    data() {
      return {
        width:1300,
      }
    },

  }
</script>

<style scoped>
</style>