<template>
  <a-popover title="自定义列" trigger="click" placement="leftBottom">
    <template slot="content">
      <a-checkbox-group @change="onShowedColsChange" v-model="showedCols" style="width: 400px;">
        <a-row>
          <template v-for="(column,index) in columnDefs_">
            <a-col :span="12">
              <a-checkbox :value="column.dataIndex  || column.key" :disabled="isIgnored(column)">{{ column.title }}</a-checkbox>
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

  export default {
    name: "TableColumnsSetter",

    model: {
      prop: 'columnDefs',
      event: 'change'
    },

    props: {
      tableKey:{//一个页面有多个table自定义列时，需设置本属性
        type:String,
        default: '',
      },
      columnDefs:{
        type:Array,
        required: true
      },
      ignoredCols:{//column[i].key字符串'key1,key2'
        type: String,
        default: 'rowIndex,action',
      },
    },

    data() {
      return {
        columnDefs_:[], //保存列定义
        showedCols:[] //columnDefs[i].key字符串数组
      }
    },

    computed: {
      ls_key() { return this.$route.name + '/' + this.key + ':showedCols'},
    },

    mounted() {
      this.columnDefs_.push(...this.columnDefs);
      this.init();
    },

    methods: {
      init(){
        //权限过滤（列权限控制时打开，修改第二个参数为授权码前缀）
        //this.columnDefs_ = colAuthFilter(this.columnDefs_,'testdemo:');

        this.showedCols = Vue.ls.get(this.ls_key);
        let columns = [];
        if(!this.showedCols){
          this.showedCols = [];
          this.columnDefs_.forEach((column,i,array ) => this.showedCols.push(column.dataIndex || column.key));
          columns = this.columnDefs_;
        } else {
          columns = this.columnDefs_.filter(column => {
            if(this.isIgnored(column)) return true;
            if (this.showedCols.includes(column.dataIndex || column.key)) return true;
            return false;
          })
        }
        this.$emit('change', columns);
      },

      //列设置更改事件
      onShowedColsChange (checkedValues) {
        Vue.ls.set(this.ls_key, this.showedCols, 7*24*60*60*1000)
        const columns = this.columnDefs_.filter(column => {
          if (this.isIgnored(column)) return true;
          return this.showedCols.includes(column.dataIndex || column.key);
        })
        this.$emit('change', columns);
      },

      isIgnored(column) {
        return this.ignoredCols.indexOf(column.dataIndex || column.key) >= 0;
      }
    },

  }
</script>
