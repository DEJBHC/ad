package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.FinPurInvoice;
import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.finance.mapper.FinPurInvoiceEntryMapper;
import io.finer.erp.finance.mapper.FinPurInvoiceMapper;
import io.finer.erp.finance.service.IFinPurInvoiceService;
import io.finer.erp.stock.service.IStkIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 采购发票登记
 * @Author:
 * @Date:
 * @Version:
 */
@Service
public class FinPurInvoiceServiceImpl
		extends BillWithEntryServiceImpl<FinPurInvoiceMapper, FinPurInvoice, FinPurInvoiceEntryMapper, FinPurInvoiceEntry>
		implements IFinPurInvoiceService {

	@Autowired
	private IStkIoService stkIoService;

	@Override
	protected  void beforePersistAdd(FinPurInvoice bill, List<FinPurInvoiceEntry> entryList){
		BigDecimal amt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			for(FinPurInvoiceEntry entry:entryList) {
				if (entry.getAmt() != null) {
					amt = amt.add(entry.getAmt());
				}
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinPurInvoice bill, List<FinPurInvoiceEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected void writeBack(FinPurInvoice bill, boolean reverse) {
		List<FinPurInvoiceEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		List<FinPurInvoiceEntry> entryList1 = entryList;

		if (bill.getIsRubric() == 1 && bill.getIsReturned() == 0) {
			//红字单据-红冲：以蓝单的源单开始回写
			entryList1 = new ArrayList<>();
			for(FinPurInvoiceEntry entry: entryList) {
				writtenBackForward(entry, entryList1, FinPurInvoiceEntry.class);
			}
		}

		stkIoService.purInvoiceWriteBack(entryList1, reverse);
	}

	@Override
	protected void voidBillPreprocess(FinPurInvoice bill) throws Exception {
		//无后置单据
	}
}
