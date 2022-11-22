package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.FinSalInvoice;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.finer.erp.finance.mapper.FinSalInvoiceEntryMapper;
import io.finer.erp.finance.mapper.FinSalInvoiceMapper;
import io.finer.erp.finance.service.IFinSalInvoiceService;
import io.finer.erp.stock.service.IStkIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 销售发票登记
 * @Author
 * @Date:   2020-05-24
 * @Version: V1.0
 */
@Service
public class FinSalInvoiceServiceImpl
		extends BillWithEntryServiceImpl<FinSalInvoiceMapper, FinSalInvoice, FinSalInvoiceEntryMapper, FinSalInvoiceEntry>
		implements IFinSalInvoiceService {

	@Autowired
	private IStkIoService stkIoService;

	@Override
	protected  void beforePersistAdd(FinSalInvoice bill, List<FinSalInvoiceEntry> entryList){
		BigDecimal amt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			for(FinSalInvoiceEntry entry:entryList) {
				if (entry.getAmt() != null) {
					amt = amt.add(entry.getAmt());
				}
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinSalInvoice bill, List<FinSalInvoiceEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected void writeBack(FinSalInvoice bill, boolean reverse) {
		List<FinSalInvoiceEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		List<FinSalInvoiceEntry> entryList1 = entryList;

		if (bill.getIsRubric() == 1 && bill.getIsReturned() == 0) {
			//红字单据-红冲：以蓝单的源单开始回写
			entryList1 = new ArrayList<>();
			for(FinSalInvoiceEntry entry: entryList) {
				writtenBackForward(entry, entryList1, FinSalInvoiceEntry.class);
			}
		}

		stkIoService.salInvoiceWriteBack(entryList1, reverse);
	}

	@Override
	protected void voidBillPreprocess(FinSalInvoice bill) throws Exception {
		//无后置单据
	}

}
