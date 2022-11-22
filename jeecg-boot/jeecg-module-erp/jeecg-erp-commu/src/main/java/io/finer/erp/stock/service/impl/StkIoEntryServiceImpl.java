package io.finer.erp.stock.service.impl;

import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.mapper.StkIoEntryMapper;
import io.finer.erp.stock.service.IStkInventoryService;
import io.finer.erp.stock.service.IStkIoEntryService;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class StkIoEntryServiceImpl
		extends ServiceImpl<StkIoEntryMapper, StkIoEntry>
		implements IStkIoEntryService {
	@Autowired
	private IStkInventoryService stkInventoryService;

	@Override
	public List<StkIoEntry> selectByMainId(String mainId) {
		return this.baseMapper.selectByMainId(mainId);
	}

	@Override
	public List<StkIoEntry> selectEditingByMainId(String mainId) {
		List<StkIoEntry> list = selectByMainId(mainId);
		for(StkIoEntry entry : list) {
			StkInventory inv = stkInventoryService.getInventory(entry.getBatchNo(),
					entry.getMaterialId(), entry.getWarehouseId());
			if (inv != null) {
				entry.setInventoryUnitId(inv.getUnitId());
				entry.setInventoryQty(inv.getQty());
				entry.setInventoryCost(inv.getCost());
			}
		}
		return list;
	}

}
