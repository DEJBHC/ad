package io.finer.erp.stock.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.stock.entity.StkCheck;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.mapper.StkCheckEntryMapper;
import io.finer.erp.stock.mapper.StkCheckMapper;
import io.finer.erp.stock.service.IStkCheckService;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 盘点卡
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class StkCheckServiceImpl
		extends BillWithEntryServiceImpl<StkCheckMapper, StkCheck, StkCheckEntryMapper, StkCheckEntry>
		implements IStkCheckService {

	@Autowired
	private IStkIoService stkIoService;

	@Override
	protected void writeBack(StkCheck bill, boolean reverse) {
		//无回写
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(StkCheck bill) throws Exception {
		if (bill.getIsRubric() == 1) {
			throw new JeecgBootException("库存盘点单不能为红字单据！");
		}

		List<StkCheckEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

		//后置单据-盘盈入库单、盘亏出库单：自动生成
		List<StkCheckEntry> profitList = new ArrayList<>();
		List<StkCheckEntry> lossList = new ArrayList<>();
		for(StkCheckEntry entry:entryList) {
			if (entry.getProfitQty().compareTo(BigDecimal.ZERO) > 0) {
				profitList.add(entry);
			} else if (entry.getProfitQty().compareTo(BigDecimal.ZERO) < 0) {
				lossList.add(entry);
			}
		}

		if(profitList.size() > 0) {
			stkIoService.createInBill(bill, profitList);
		}
		if(lossList.size() > 0) {
			stkIoService.createOutBill(bill, lossList);
		}
	}

	@Override
	protected void voidBillPreprocess(StkCheck bill) throws Exception {
		//后置单据-盘盈入库单、盘亏出库单-自动作废：库存盘点单生效后自动生成的
		List<StkIo> list = stkIoService.listNotVoided(bill.getBillType(), bill.getId());
		for(StkIo stkIo: list) {
			if (stkIo.getSrcBillId().equals(bill.getId()) && stkIo.getIsAuto() == 1) {
				stkIoService.voidBill(stkIo.getId());
			}
		}

		//后置单据-盘盈入库单、盘亏出库单：非自动生成的
		String billNos = stkIoService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

}
