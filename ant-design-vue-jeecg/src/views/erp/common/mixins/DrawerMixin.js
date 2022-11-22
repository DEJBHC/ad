export const DrawerMixin = {
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
      this.$refs.realForm.submitForm();
    },
    handleCancel () {
      this.close();
    },

  }
}