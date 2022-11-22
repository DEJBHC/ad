<template>
  <j-modal
    :title="'采购应付 - ' + title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    draggable
    switchFullscreen
    :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
    @cancel="handleCancel">

    <template slot="footer">
      <!--只有一个<a-button>不能替换“取消”和“确定”？-->
      <a-button v-if="disableSubmit" key="print" @click="handlePrint" style="margin-right: 48px">打印</a-button>
      <a-button v-if="action!=='detail'" key="cancel" @click="handleCancel">取消</a-button>
      <a-button v-if="action==='detail'" key="cancel"  @click="handleCancel" type="primary">关闭</a-button>
    </template>
    <pur-payable-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"/>
  </j-modal>
</template>

<script>
  import { BillModalMixin } from '../../common/mixins/BillModalMixin';
  import PurPayableForm from "./PurPayableForm";

  export default {
    name: "PurPayableModal",
    mixins: [ BillModalMixin ],
    components: {PurPayableForm},

    data() {
      return {
        width:1300,
      }
    },

  }
</script>

<style scoped>
</style>
