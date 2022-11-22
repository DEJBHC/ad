<template>
  <a-popover title="自定义列" trigger="click" placement="leftBottom">
    <template slot="content">
      <a-checkbox-group @change="onShowedColsChange" v-model="showedCols" style="width: 400px;">
        <a-row>
          <template v-for="(column,index) in columnDefs">
            <a-col :span="12" v-if="!isExcluded(column)">
              <a-checkbox :value="column.key" :disabled="isIgnored(column)">{{column.title}}</a-checkbox>
            </a-col>
          </template>
        </a-row>
      </a-checkbox-group>
    </template>
    <a-button type="link" icon="setting">自定义列</a-button>
  </a-popover>
</template>

<script>
  import Vue from "vue";
  import { JVXETypes } from '@/components/jeecg/JVxeTable';
  import {emitColumnsChange} from "../../common/utils/util";

  export default {
    name: "VxeTableColumnDefsSetter",

    props: {
      tableKey:{//一个页面有多个独立自定义列的JVxeTable时，设置本属性
        type: String,
        default: '',
      },
      columnDefs:{ //列定义
        type: Array,
        required: true
      },
      excludedCols:{//columnDefs[i].key字符串'key1,key2'，不列出
        type: String,
        default: '',
      },
      ignoredCols:{//columnDefs[i].key字符串'key1,key2'，列出但disabled
        type: String,
        default: '',
      },
      ignoreRequired:{//录入时必填列不能隐藏，否则无法校验（JVxeTable的列隐藏时，validateRules无效）
        type: Boolean,
        default: true
      }
    },

    data() {
      return {
        showedCols: [],//columnDefs[i].key字符串数组
        requiredCols:[],//columnDefs[i].key字符串数组
      }
    },

    computed: {
      ls_key() { return this.$route.name + '/' + this.tableKey + ':showedCols'},
      /**
       * @return {string}
       */
      JVXETypes_hidden() { return JVXETypes.hidden },
    },

    watch: {
      tableKey: {
        immediate: true,
        handler() {
          this.init();
        }
      },

    },

    methods: {
      init() {
        let cols = [];
        this.columnDefs.forEach(column => {
          cols.push(column.key)
          if (column.validateRules)
            column.validateRules.forEach(rule => { if (rule.required) this.requiredCols.push(column.key) });
        });
        this.showedCols = Vue.ls.get(this.ls_key);
        if(!this.showedCols || this.showedCols.length === 0) this.showedCols = cols;
        this.showColumns();
      },

      //列设置更改事件
      onShowedColsChange (checkedValues) {
        Vue.ls.set(this.ls_key, this.showedCols, 7*24*60*60*1000)
        this.showColumns();
      },

      showColumns(){
        if (!this.columnDefs) return;

        this.columnDefs.forEach(column => {
          if (!this.isIgnored(column) && !this.isExcluded(column)) {
            if (this.showedCols.includes(column.key)) {
              if (column.type === JVXETypes.hidden && !!column.type_s) column.type = column.type_s;
            }
            else {
              column.type_s = column.type;
              column.type = JVXETypes.hidden;
            }
          }
        });
        emitColumnsChange(this.columnDefs);
      },

      isExcluded(column) {
        return this.excludedCols.indexOf(column.key) >= 0
          || column.type === JVXETypes.hidden && !column.type_s;
      },

      isIgnored(column) {
        return this.ignoredCols.indexOf(column.key) >= 0
          || this.ignoreRequired && this.requiredCols.includes(column.key);
      }
    },

  }
</script>
