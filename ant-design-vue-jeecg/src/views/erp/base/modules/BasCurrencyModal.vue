<template>
  <a-drawer
    :title="'币种 - ' + title"
    :width="width"
    placement="right"
    :closable="disableSubmit"
    :maskClosable="disableSubmit"
    @close="close"
    destroyOnClose
    :visible="visible">
    <bas-currency-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit" normal/>
    <div class="drawer-footer">
      <a-button v-if="!disableSubmit" @click="handleOk" type="primary" style="margin-bottom: 0;">确定</a-button>
      <a-button @click="handleCancel" style="margin-bottom: 0;">{{disableSubmit ? '关闭':'取消'}}</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import BasCurrencyForm from "./BasCurrencyForm";
  import {DrawerMixin} from '../../common/mixins/DrawerMixin'

  export default {
    name: "BasCurrencyModal",
    components: {BasCurrencyForm},
    mixins: [DrawerMixin],

    data() {
      return { width:600}
    },
  }
</script>

<style lang="less" scoped>
  @import "../../common/less/Drawer.less";
</style>
