import ATableDragResize from './ATableDragResize'
import {} from "../../common/utils/util"; //执行Date.prototype.format等

export const ListMixin = {
  mixins: [ATableDragResize],

  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },
  },

  methods: {
    myHandleAdd() {
      this.$refs.modalForm.action = "add";
      this.handleAdd();
    },
    myHandleEdit(record) {
      this.$refs.modalForm.action = "edit";
      this.handleEdit(record);
    },
    myHandleDetail(record) {
      this.$refs.modalForm.action = "detail";
      this.handleDetail(record);
    },
  }
}