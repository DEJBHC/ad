<template>
  <j-modal
    :title="'销售退货退款(红字收款)申请单 - ' + title"
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
    <rubric-sal-receipt1031-req-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </j-modal>
</template>

<script>
  import RubricSalReceipt1031ReqForm from "./RubricSalReceipt1031ReqForm";
  import { BillModalMixin } from '../../common/mixins/BillModalMixin';

  export default {
    name: 'RubricSalReceipt1031ReqModal',
    mixins: [BillModalMixin],
    components: {RubricSalReceipt1031ReqForm},

    data() {
      return {
        width:1300,
      }
    },
  }
</script>

<style scoped>
</style>