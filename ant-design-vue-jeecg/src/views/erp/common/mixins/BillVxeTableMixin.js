import XEUtils from 'xe-utils'
import {getAction} from '@/api/manage'
import {JVXETypes} from '@/components/jeecg/JVxeTable';
import {emitColumnsChange} from "../../common/utils/util";

export const BillVxeTableMixin = {

  data() {
    return {
      materialTable: {
        loading: false,
        dataSource: [],
        selectOptions:[],
        url: {list: '/base/basMaterial/list/enabled'},
      },
    };
  },

  methods: {
    hideColumns(keys, columns) {
      if (!keys || keys.length === 0) return;
      columns = columns || this.entryTable.columns;

      for(let key of keys.split(',')) {
        const column = columns.find(column => column.key === key);
        if (!!column) {
          if (!column.type_s) column.type_s = column.type;
          column.type = JVXETypes.hidden;
        }
      }
      emitColumnsChange(columns);
    },

    restoreColumnsType(keys, columns) {
      if (!keys || keys.length === 0) return;
      columns = columns || this.entryTable.columns;

      for(let key of keys.split(',')) {
        const column = columns.find(column => column.key === key);
        if (!!column && !!column.type_s) column.type = column.type_s;
      }
      emitColumnsChange(columns);
    },

    //vue created时，删除物料列的dictCode，加载相关数据，设置物料列的options
    initMaterialRelated(entryTable) {
      if (this.disabled) return;
      entryTable = entryTable || this.entryTable;
      const col = entryTable.columns.find(c => c.key === 'materialId');
      if (!col || col.disabled) return;
      delete col.dictCode;

      if (this.materialTable.dataSource.length > 0) return;
      const that = this;
      this.requestSubTableData(this.materialTable.url.list, {}, this.materialTable, success);
      function success() {
        that.materialTable.selectOptions = that.materialTable.dataSource.map(m => ({value: m.id, text: m.auxName}));
        col.options = that.materialTable.selectOptions;
        emitColumnsChange(entryTable.columns);
      }
    },

    //JVxeTable editActived时，删除单位列的dictCode，设置单位列的options
    setMaterialUnitOptions(row, entryJVxeTable) {
      if (this.disabled) return;
      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;

      let material = null;
      if (row.materialId && row.materialId.length > 0)
        material = this.materialTable.dataSource.find(m => m.id === row.materialId);

      const col = entryJVxeTable.vxeColumns.find(c => c.key === 'unitId');
      if (!!col && !col.disabled) {
        delete col.dictCode;
        col.options = material && material.validUnitList ?
          material.validUnitList.map(u => ({value: u.id, text: u.name})) : []; //计量单位的可选项列表
      }
     },

    //物料列valueChange时，设置单位列的options，设置物料规格型号, 清空其他关联列值
    handleMaterialChange(row, entryJVxeTable, emptyColumnKeys) {
      entryJVxeTable = entryJVxeTable || this.$refs.entryTable;

      let material = null;
      if (!!row.materialId && row.materialId.length > 0)
        material = this.materialTable.dataSource.find(m => m.id === row.materialId);

      //设置物料规格型号
      const values = {};
      values.materialModel = material ? material.model : '';
      entryJVxeTable.setValues([{rowKey: row.id, values: values}]);

      //设置单位列的options
      const col = entryJVxeTable.vxeColumns.find(c => c.key === 'unitId');
      if (!!col && !col.disabled)
        col.options = material && material.validUnitList ?
          material.validUnitList.map(u => ({value: u.id, text: u.name})) : [];

      //清空其他关联列值
      if (emptyColumnKeys && emptyColumnKeys.length > 0) this.emptyColumns(row, emptyColumnKeys, entryJVxeTable);
    },

    emptyColumns(row, columnKeys, jvxeTable) {
      if (!columnKeys || columnKeys.length === 0) return;
      jvxeTable = jvxeTable || this.$refs.entryTable;

      const values = {};
      const keys = Array.isArray(columnKeys) ? columnKeys : columnKeys.split(',');
      for(let key of keys) values[key] = '';
      jvxeTable.setValues([{rowKey: row.id, values: values}]);
    },

    getUnitRate(materialId, fromUnitId, toUnitId) {
      if (!materialId || !fromUnitId || !toUnitId
        || materialId.length===0 || fromUnitId.length===0 || toUnitId.length===0) return null;
      if (fromUnitId === toUnitId) return 1;

      const material = this.materialTable.dataSource.find(m => m.id === materialId);
      if (!material) {
        this.$message.warning(materialId + "：物料不存在或被禁用！");
        return null;
      }

      const fromUnit = material.validUnitList.find(u => u.id === fromUnitId);
      const msg = "：非该物料的合法单位！";
      if (!fromUnit) {
        this.$message.warning(fromUnitId + msg);
        return null;
      }
      const toUnit = material.validUnitList.find(u => u.id === toUnitId);
      if (!toUnit) {
        this.$message.warning(toUnitId + msg);
        return null;
      }
      return fromUnit.factor / toUnit.factor;
    },

    convertUnit(qty, materialId, fromUnitId, toUnitId) {
      if (!qty) return null;
      if (qty === 0) return 0;

      const rate = this.getUnitRate(materialId, fromUnitId, toUnitId);
      return !rate ? null : qty * rate;
    },

    // 加载子表数据：明细、源单据、源明细
    requestSubDatas(entryTable, srcTable, srcEntryTable, success, srcBillType, srcBillIds) {
      let that = this;
      if (entryTable)
        that.requestSubTableData(entryTable.url.list, {id: this.model.id}, entryTable, success1);
      else{
        entryTable = this.entryTable;
        success1();
      }

      function success1(){
        let recs = entryTable.dataSource;
        entryTable.rowCount = recs.length;

        if (!srcBillIds && recs.length > 0) {
          if (srcBillType && srcBillType.length > 0) recs = recs.filter(r => r.srcBillType === srcBillType);
          srcBillIds = that.getIds(recs, 'srcBillId');
        }

        if (srcTable && srcBillIds && srcBillIds.length > 0)
          that.requestSubTableData(srcTable.url.list, {ids: srcBillIds}, srcTable, success2);
        else
          success2();
      }

      function success2(){
        if (srcEntryTable && srcBillIds && srcBillIds.length > 0)
          that.requestSubTableData(srcEntryTable.url.list, {ids: srcBillIds}, srcEntryTable, success);
        else
          typeof success === 'function' ? success() : '';
      }
    },

    // 加载子表增量数据：源单据、源明细
    requestSrcDeltas(srcBillIds, srcTable, srcEntryTable, success) {
      if(!srcBillIds || srcBillIds.length ===0) return;

      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable; //this.srcEntryTable也可会undefined

      let ids0 = [];
      for(let rec of srcTable.dataSource) ids0.push(rec.id);

      //去掉已存在的id
      srcBillIds = srcBillIds.split(',');
      let ids1 = [];
      for(let id of srcBillIds) {
        if (!ids0.includes(id)) ids1.push(id);
      }
      srcBillIds = ids1.toString();

      let that = this;
      let deltas = {};
      if (srcTable && srcBillIds && srcBillIds.length > 0)
        that.requestAppendData(srcTable, {ids: srcBillIds}, success1);
      else
        success1(srcTable ? [] : undefined);

      function success1(srcBills){
        deltas.srcBills = srcBills;
        if (srcEntryTable && srcBillIds && srcBillIds.length > 0)
          that.requestAppendData(srcEntryTable, {ids: srcBillIds}, success2);
        else
          success2(srcEntryTable ? [] : undefined);
      }

      function success2(srcEntries){
        deltas.srcEntries = srcEntries;
        typeof success === 'function' ? success(deltas) : '';
      }
    },

    requestDelta(ids, tab, success) {
      //去掉已存在的id
      let ids1 = [];
      for(let id of ids.split(',')) {
        if (!tab.dataSource.find(r => r.id === id)) ids1.push(id);
      }
      ids = ids1.toString();

      this.requestAppendData(tab, {ids: ids}, success);
    },

    requestAppendData(tab, params, success) {
      tab.loading = true;
      getAction(tab.url.list, params).then(res => {
        let { result } = res;
        let recs = [];
        if (result) {
          if (Array.isArray(result)) {
            recs = result;
          } else if (Array.isArray(result.records)) {
            recs = result.records;
          }
        }
        if (recs.length > 0)  tab.dataSource = [...tab.dataSource, ...recs];

        typeof success === 'function' ? success(recs) : '';
      }).finally(() => {
        tab.loading = false
      })
    },

    getIds(records, idName = 'id') {
      let ids = [];
      for(let r of records) if (!ids.includes(r[idName])) ids.push(r[idName]);
      return ids.toString();
    },

    removeFreeSrcBills(referrers, srcTable, srcEntryTable) {
      referrers = referrers || this.$refs.entryTable.getTableData();
      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable;

      let ds = [];
      if (srcTable) {
        for (let b of srcTable.dataSource){
          if (referrers.find(row => row.srcBillId === b.id)) ds.push(b);
        }
        srcTable.dataSource = ds;
      }

      ds = [];
      if (srcEntryTable) {
        for (let e of srcEntryTable.dataSource){
          if (srcTable.dataSource.find(b => b.id === e.mid)) ds.push(e);
        }
        srcEntryTable.dataSource = ds;
      }
    },

    onEntryAdded(event) {
      const {row, target} = event
      let rows = target.getTableData();
      let maxEntryNo = 0;
      for (let row of rows) {
        let entryNo = Number(row['entryNo']);
        if (!isNaN(entryNo) && entryNo > maxEntryNo) maxEntryNo = entryNo;
      }

      maxEntryNo += this.entryNoStep || 10;
      target.setValues([{rowKey: row.id, values: {entryNo: maxEntryNo}}]);
    },

    onInEntryAdded(event){
      this.onEntryAdded(event);

      const { row, target } = event
      let batchNo =  this.model.billNo + "-" + row.entryNo;
      target.setValues([{rowKey: row.id, values: {batchNo: batchNo}}]);
    },

    removeFromSrcTable(jxSrcTable, srcTable, srcEntryTable) {
      jxSrcTable = jxSrcTable || this.$refs.srcTable;
      srcTable = srcTable || this.srcTable;
      srcEntryTable = srcEntryTable || this.srcEntryTable;
      let ids = jxSrcTable.selectedRowIds;
      let dstRows = this.$refs.entryTable.getTableData();

      //id被引用的记录不能移除
      let ids2 = [];
      for (let id of ids)
        if (!dstRows.find(r => r.srcBillId === id)) ids2.push(id);

      //留下主表记录未删除的
      if (ids2.length > 0 && srcEntryTable) {
        let ds = [];
        for(let r of srcEntryTable.dataSource)
          if (!ids2.includes(r.mid)) ds.push(r);
        srcEntryTable.dataSource = ds;
      }

      //留下未删除的
      if (ids2.length > 0) {
        let ds = [];
        for(let r of srcTable.dataSource)
          if (!ids2.includes(r.id)) ds.push(r);
        srcTable.dataSource = ds;
      }

      jxSrcTable.clearSelection();
    },

    srcBillCheckboxMethod({row}) {
      let dstRows = this.$refs.entryTable.getTableData();
      return !dstRows.find(dstRow => dstRow.srcBillId === row.id);
    },

    srcEntryCheckboxMethod({row}) {
      let dstRows = this.$refs.entryTable.getTableData();
      return !dstRows.find(dstRow => dstRow.srcEntryId === row.id);
    },

    //红蓝单都可使用
    rubricValidator({cellValue, row, column}, callback, target) {
      const v = Number(cellValue);
      if (isNaN(v)) {
        callback();
        return;
      }

      const isRubric = Number(this.model.isRubric);
      if (isRubric === 0 && v < 0) {
        callback(false, '蓝字单${title}不能为负数');
      } else if (isRubric === 1 && v > 0) {
        callback(false, '红字单${title}不能为正数');
      } else {
        callback(true); //true：通过验证
      }
    },

    //只能用于红单
    rubricRangeValidator({cellValue, row, column}, callback, target) {
      const isRubric = Number(this.model.isRubric);
      if (isNaN(isRubric) || isRubric === 0) {
        this.$message.warning('rubricRangeValidator只能用于红字单据！');
        callback();
        return;
      }

      const rubric = Number(cellValue);
      if (isNaN(rubric)) {
        callback();
        return;
      }

      if (rubric > 0) {
        callback(false, '红字单${title}不能为正数');
        return;
      }

      let rows1 = this.$refs[this.refKeys[1]].getTableData();
      rows1 = rows1.filter(row0 => row0.id === row.srcEntryId);
      let blue = rows1[0][column.key]
      if (isNaN(blue)) {
        callback();
        return;
      }
      if (-rubric > blue) {
        callback(false, '不能超出蓝字单！');
        return;
      }

      callback(true); //true：通过验证
    },

    stkOutQtyValidator({cellValue, row, column}, callback, target) {
      let materialId = row.materialId, unitId = row.unitId, qty = Number(row.qty),
        inventoryUnitId = row.inventoryUnitId, inventoryQty = Number(row.inventoryQty);
      if (!materialId || !unitId || isNaN(qty) || !inventoryUnitId || isNaN(inventoryQty)) {
        callback();
        return;
      }

      if (unitId !== inventoryUnitId) {
        let rate = this.getUnitRate(materialId, unitId, inventoryUnitId);
        if (!rate) {
          callback(false, '出库单位不合法！');
          return;
        }
        qty *= rate;
      }

      if (qty > inventoryQty) {
        callback(false, '不能大于库存数量！');
        return;
      }

      let rows = target.getTableData();
      if (rows.length < 2) {
        callback(true);
        return;
      }

      let batchNo = row.batchNo, warehouseId = row.warehouseId;
      let qtySum = 0;
      for(let r of rows) {
        qty = Number(r.qty);
        if (!isNaN(qty) && r.unitId &&
          batchNo === r.batchNo && warehouseId === r.warehouseId && materialId === r.materialId) {
          let rate = this.getUnitRate(r.materialId, r.unitId, inventoryUnitId);
          if (rate) {
            qtySum += qty * rate;
          }
        }
      }
      if (qtySum > inventoryQty) {
        callback(false, '该仓库批次的出库数量合计不能大于库存数量！');
      } else {
        callback(true);
      }
    },

    // JVxeTable column formatter
    // 参考:
    //  1) src/views/jeecg/JVxeDemo/layout-demo/Template1.vue
    //  2) https://xuliangzhan_admin.gitee.io/vxe-table/#/table/base/format

    // 四舍五入金额，每隔3位逗号分隔，默认2位小数
    formatAmt ({ cellValue, row, column}, digits = 2) {
      return Number(cellValue) ? XEUtils.commafy(Number(cellValue), { digits }) : cellValue;
    },

    // 四舍五入数量，每隔3位逗号分隔，默认3位小数
    formatQty ({ cellValue, row, column}, digits = 3) {
      return this.formatAmt({ cellValue, row, column}, digits);
    },

  }
}