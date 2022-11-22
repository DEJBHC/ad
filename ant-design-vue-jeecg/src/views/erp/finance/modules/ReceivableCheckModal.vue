<template>
  <j-modal
    :title="'应收核销 - ' + title"
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
      <a-tooltip :title="canSubmit ? '' : '无核销或上下合计不等，不能提交！'" placement="bottom">
        <a-button v-if="!disableSubmit" key="submit" @click="handleMySubmit" type="primary" :disabled="!canSubmit">提交</a-button>
      </a-tooltip>
      <a-button v-if="action==='check'" key="check" @click="handleCheck" type="primary">审核</a-button>
      <a-button v-if="action==='ebpm'" key="ebpm" @click="handleEbpm" type="primary">结束审批</a-button>
      <a-button v-if="action==='execute'" key="execute" @click="handleExecute" type="primary">执行</a-button>
      <a-popconfirm  title="作废后不可恢复，确定要作废？" @confirm="handleVoid" okText="确定" cancelText="取消">
        <a-button v-if="action==='void'" key="void" type="primary">作废</a-button>
      </a-popconfirm>
    </template>
    <receivable-check-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" :canSubmit.sync="canSubmit"/>
  </j-modal>
</template>

<script>
  import { BillModalMixin } from '../../common/mixins/BillModalMixin'
  import ReceivableCheckForm from "./ReceivableCheckForm";

  export default {
    name: 'ReceivableCheckModal',
    mixins: [BillModalMixin],
    components: {ReceivableCheckForm},

    data() {
      return {
        width:1300,
        canSubmit: false
      }
    },

    methods: {
      handleMySubmit() {
        this.$refs.realForm.handleMySubmit();
      }
    },
  }
</script>

<style scoped>
</style>
