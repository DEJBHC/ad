import { JVXETypes } from '@/components/jeecg/JVxeTable'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import Vue from "vue";

export function printReport(id, params) {
  const domain = window._CONFIG['domianURL'];
  const token = Vue.ls.get(ACCESS_TOKEN);

  let s = '';
  for(let k in params) {
    const v =  encodeURIComponent(params[k]);
    s += `&${k}=${v}`;
  }

  window.open(`${domain}/jmreport/view/${id}?token=${token}${s}`);
}

export function emitColumnsChange(columns){
  //产生JVxeTable.columns被watch到的变化
  if (columns[columns.length - 1].key !== '_tmp_')
    columns.push({key: '_tmp_', type: JVXETypes.hidden});
  else
    columns.pop();
}


String.prototype.format = function () {
  var values = arguments;
  return this.replace(/\{(\d+)\}/g, function (match, index) {
    return (values.length > index) ? values[index] : "";
  });
};

Date.prototype.format = function(fmt)
{
  var o = {
    "M+" : this.getMonth()+1,                 //月份
    "d+" : this.getDate(),                    //日
    "h+" : this.getHours(),                   //小时
    "m+" : this.getMinutes(),                 //分
    "s+" : this.getSeconds(),                 //秒
    "q+" : Math.floor((this.getMonth()+3)/3), //季度
    "S"  : this.getMilliseconds()             //毫秒
  };

  if(/(y+)/.test(fmt))
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));

  for(var k in o)
    if(new RegExp("("+ k +")").test(fmt))
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));

    return fmt;
};