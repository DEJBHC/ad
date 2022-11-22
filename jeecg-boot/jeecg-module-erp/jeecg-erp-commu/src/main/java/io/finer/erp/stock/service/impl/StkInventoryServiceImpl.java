package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.base.service.IBasUnitService;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.mapper.StkInventoryMapper;
import io.finer.erp.stock.service.IStkInventoryService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 库存
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class StkInventoryServiceImpl
        extends ServiceImpl<StkInventoryMapper, StkInventory>
        implements IStkInventoryService {

    @Autowired
    IBasMaterialService basMaterialService;
    @Autowired
    IBasUnitService basUnitService;

    @Override
    public StkInventory getInventory(String batchNo, String materialId, String warehouseId) {
        StkInventory inv = new StkInventory();
        inv.setBatchNo(batchNo);
        inv.setMaterialId(materialId);
        inv.setWarehouseId(warehouseId);
        QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<StkInventory>(inv);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInventory(StkIo stkIo, List<StkIoEntry> stkIoEntryList, boolean reverse) {
        String stockIoType = stkIo.getStockIoType();
        for(StkIoEntry entry : stkIoEntryList) {
            if (reverse) {
                //取负
                entry.setQty(entry.getQty().negate());
                entry.setCost(entry.getCost().negate());
            }

            if (stockIoType.equals("801")) { //成本调整
                this.changeCost(entry);
            }
            else if (entry.getStockIoDirection().equals("1")){ //入
                this.increase(entry);
            }
            else if (entry.getStockIoDirection().equals("2")) { //出
                this.decrease(entry);
            } else {
              throw new JeecgBootException(entry.getBillNo() + ":" + entry.getEntryNo() + "：出入库方向非法！");
            }

            if (reverse) {
                //恢复
                entry.setQty(entry.getQty().negate());
                entry.setCost(entry.getCost().negate());
            }
        }
    }

    private void increase(StkIoEntry stkIoEntry) {
        StkInventory inv_new = new StkInventory();
        inv_new.setWarehouseId(stkIoEntry.getWarehouseId());
        inv_new.setMaterialId(stkIoEntry.getMaterialId());
        inv_new.setBatchNo(stkIoEntry.getBatchNo());

        QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<StkInventory>(inv_new);
        StkInventory inv = this.baseMapper.selectOne(queryWrapper);
        String supplierId = stkIoEntry.getSupplierId();
        if (inv == null) {
            BasMaterial basMaterial = basMaterialService.getById(stkIoEntry.getMaterialId());
            if (basMaterial == null) {
                throw new JeecgBootException(String.format("【分录号：%s】物料在物料表中不存在！", stkIoEntry.getEntryNo()));
            }
            inv_new.setUnitId(basMaterial.getUnitId());

            int isSingleSupplier = stkIoEntry.getBatchNo().equals("0") || StringUtils.isEmpty(supplierId) ? 0 : 1;
            inv_new.setIsSingleSupplier(isSingleSupplier);
            inv_new.setSupplierId(supplierId);

            BigDecimal qty = stkIoEntry.getQty();
            if (!stkIoEntry.getUnitId().equals(inv_new.getUnitId())) {
                qty = basUnitService.convert(qty, stkIoEntry.getUnitId(), basMaterial.getUnitId());
            }
            inv_new.setQty(qty);

            inv_new.setCost(stkIoEntry.getCost());
            this.baseMapper.insert(inv_new);
        } else {
            if (inv.getIsSingleSupplier() == 1 && !inv.getSupplierId().equals(supplierId)) {
                throw new JeecgBootException(String.format("【分录号：%s】供应商与实时库存中“仓库+物料+批次号”的供应商不同！",
                        stkIoEntry.getEntryNo()));
            }

            if (!StringUtils.isEmpty(supplierId)) {
                inv.setSupplierId(supplierId); //保存该批次最后一次入库的供应商
            }

            BigDecimal qty = stkIoEntry.getQty();
            if (!stkIoEntry.getUnitId().equals(inv.getUnitId())) {
                qty = basUnitService.convert(qty, stkIoEntry.getUnitId(), inv.getUnitId());
            }
            inv.setQty(inv.getQty().add(qty));
            if (inv.getQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new JeecgBootException(String.format("【分录号：%s】“仓库+物料+批次号”的库存数不足！",
                        stkIoEntry.getEntryNo()));
            }

            inv.setCost(inv.getCost().add(stkIoEntry.getCost()));
            this.baseMapper.updateById(inv);
        }
    }

    private void decrease(StkIoEntry stkIoEntry) {
        StkInventory inv_new = new StkInventory();
        inv_new.setWarehouseId(stkIoEntry.getWarehouseId());
        inv_new.setMaterialId(stkIoEntry.getMaterialId());
        inv_new.setBatchNo(stkIoEntry.getBatchNo());

        QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<StkInventory>(inv_new);
        StkInventory inv = this.baseMapper.selectOne(queryWrapper);
        if (inv == null) {
            throw new JeecgBootException(String.format("【分录号：%s】“仓库+物料+批次号”在实时库存中不存在！",
                    stkIoEntry.getEntryNo()));
        }

        BigDecimal qty = stkIoEntry.getQty();
        if (!stkIoEntry.getUnitId().equals(inv.getUnitId())) {
            qty = basUnitService.convert(qty, stkIoEntry.getUnitId(), inv.getUnitId());
        }
        inv.setQty(inv.getQty().subtract(qty));
        if (inv.getQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new JeecgBootException(String.format("【分录号：%s】“仓库+物料+批次号”的库存数不足！",
                    stkIoEntry.getEntryNo()));
        }

        inv.setCost(inv.getCost().subtract(stkIoEntry.getCost()));
        this.baseMapper.updateById(inv);
    }

    private void changeCost(StkIoEntry stkIoEntry) {
        StkInventory inv_new = new StkInventory();
        inv_new.setWarehouseId(stkIoEntry.getWarehouseId());
        inv_new.setMaterialId(stkIoEntry.getMaterialId());
        inv_new.setBatchNo(stkIoEntry.getBatchNo());

        QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<StkInventory>(inv_new);
        StkInventory inv = this.baseMapper.selectOne(queryWrapper);
        if (inv == null) {
            throw new JeecgBootException(String.format("【分录号：%s】“仓库+物料+批次号”在实时库存中不存在！",
                    stkIoEntry.getEntryNo()));
        }

        BigDecimal cost = inv.getCost().add(stkIoEntry.getCost());
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new JeecgBootException(String.format("【分录号：%s】“仓库+物料+批次号”调整后成本不能为负！",
                    stkIoEntry.getEntryNo()));
        }

        inv.setCost(cost);
        this.baseMapper.updateById(inv);
    }

}
