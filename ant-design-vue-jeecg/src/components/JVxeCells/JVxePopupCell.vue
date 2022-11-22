<template>
  <j-popup
    v-bind="popupProps"
    @input="handlePopupInput"
  />
</template>

<script>
  import JVxeCellMixins, { dispatchEvent, vModel } from '@/components/jeecg/JVxeTable/mixins/JVxeCellMixins'

  export default {
    name: 'JVxePopupCell',
    mixins: [JVxeCellMixins],
    computed: {
      popupProps() {
        //20220412 cfm modi： 增加paramFields
        //const {innerValue, originColumn: col, caseId, cellProps} = this
        const {innerValue, originColumn: col, row, caseId, cellProps} = this
        let param = {...col.param}
        if (col.paramFields) {
          let paramFieldArr = col.paramFields.split(',')
          if (paramFieldArr.length > 0) {
            let destFieldArr = col.destFields.split(',')
            let orgFieldArr = col.orgFields.split(',')
            for(let i = 0; i < paramFieldArr.length; i++) {
              for(let j = 0; j < destFieldArr.length; j++) {
                if (destFieldArr[j] === paramFieldArr[i])  param[orgFieldArr[j]] = row[paramFieldArr[i]]
              }
            }
          }
        }

        return {
          ...cellProps,
          value: innerValue,
          field: col.field || col.key,
          code: col.popupCode,
          orgFields: col.orgFields,
          destFields: col.destFields,
          groupId: caseId,
          param: param, //20220412 cfm modi: col.param --> param
          sorter: col.sorter,
        }
      },
    },
    methods: {
      /** popup回调 */
      handlePopupInput(value, others) {
        const {row, originColumn: col} = this
        // 存储输入的值
        let popupValue = value
        if (others && Object.keys(others).length > 0) {
          Object.keys(others).forEach(key => {
            let currentValue = others[key]
            // 当前列直接赋值，其他列通过vModel赋值
            if (key === col.key) {
              popupValue = currentValue
            } else {
              vModel.call(this, currentValue, row, key)
            }
          })
        }
        this.handleChangeCommon(popupValue)
      },
    },
    // 【组件增强】注释详见：JVxeCellMixins.js
    enhanced: {
      aopEvents: {
        editActived(event) {
          // 【issues/3854】附表控件类型为popup必填时未选择值提交表单会报错
          if (event.$event && event.$event.type === 'valid-error') {
            return;
          }
          dispatchEvent.call(this, event, 'ant-input')
        },
      },
    },
  }
</script>

<style scoped>

</style>