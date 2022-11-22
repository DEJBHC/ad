export const BillModalMixin = {
  data() {
    return {
      action: '',
      title:'',
      visible: false,
      disableSubmit: false
    };
  },

  methods: {
    add () {
      this.visible=true
      this.$nextTick(()=>{
        this.$refs.realForm.action = this.action;
        this.$refs.realForm.add();
      })
    },
    edit (record) {
      this.visible=true
      this.$nextTick(()=>{
        this.$refs.realForm.action = this.action;
        this.$refs.realForm.edit(record);
      })
    },
    close () {
      this.$emit('close');
      this.visible = false;
    },
    submitCallback(){
      this.$emit('ok');
      this.visible = false;
    },
    handleOk () {
      this.$refs.realForm.handleOk();
    },
    handleCancel () {
      this.close()
    },
    handleSave() {
      this.$refs.realForm.handleSave();
    },

    handleSubmit() {
      this.$refs.realForm.handleSubmit();
    },
    handleCheck() {
      this.$refs.realForm.handleCheck();
    },
    handleEbpm() {
      this.$refs.realForm.handleEbpm();
    },
    handleExecute() {
      this.$refs.realForm.handleExecute();
    },
    handleClose() {
      this.$refs.realForm.handleClose();
    },
    handleUnclose() {
      this.$refs.realForm.handleUnclose();
    },
    handleVoid() {
      this.$refs.realForm.handleVoid();
    },
    handlePrint(){
      this.$emit('print', this.$refs.realForm.model.id);
    },

  }
}