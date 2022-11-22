package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.mapper.FinReceivableCheckEntryMapper;
import io.finer.erp.finance.mapper.FinReceivableCheckMapper;
import io.finer.erp.finance.service.IFinReceiptService;
import io.finer.erp.finance.service.IFinReceivableCheckService;
import io.finer.erp.finance.service.IFinReceivableService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 应收核销单
 * @Author: jeecg-boot
 * @Date:   2022-08-22
 * @Version: V1.0
 */
@Service
public class FinReceivableCheckServiceImpl
		extends BillWithEntryServiceImpl<FinReceivableCheckMapper, FinReceivableCheck, FinReceivableCheckEntryMapper, FinReceivableCheckEntry>
		implements IFinReceivableCheckService {

	@Autowired
	private IFinReceivableService finReceivableService;
	@Autowired
	private IFinReceiptService finReceiptService;

	@Override
	protected  void beforePersistAdd(FinReceivableCheck bill, List<FinReceivableCheckEntry> entryList){
		BigDecimal amt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			for(FinReceivableCheckEntry entry: entryList) {
				if (entry.getCheckSide().equals("1") && entry.getAmt() != null) {
					amt = amt.add(entry.getAmt());
				}
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinReceivableCheck bill, List<FinReceivableCheckEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected void writeBack(FinReceivableCheck bill, boolean reverse) {
		List<FinReceivableCheckEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		finReceivableService.receivableCheckWriteBack(entryList, reverse);
		finReceiptService.receivableCheckWriteBack(entryList, reverse);
	}

	@Override
	protected void voidBillPreprocess(FinReceivableCheck bill) throws Exception {
		//无后置单据
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createBill(FinReceipt receipt, List<FinReceiptEntry> receiptEntryList) throws Exception {
		//明细
		List<FinReceivableCheckEntry> entryList = new ArrayList<>();
		BigDecimal checkAmt = new BigDecimal("0.00");
		int entryNo = 100;
		// CheckSide="1"
		for(FinReceiptEntry receiptEntry: receiptEntryList) {
			String srcBillType = receiptEntry.getSrcBillType();
			BigDecimal receiptAmt = receiptEntry.getAmt();
			if (!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")
					&& receiptAmt != null && receiptAmt.compareTo(BigDecimal.ZERO) != 0) {
				FinReceivableCheckEntry entry =	createBillEntry(receiptEntry);
				if (entry != null) {
					entry.setEntryNo(++entryNo);
					entryList.add(entry);
					checkAmt = checkAmt.add(entry.getAmt());
				}
			}
		}
		if (entryList.size() == 0) {
			return;
		}

		// CheckSide="2"
		FinReceivableCheckEntry entry = createBillEntry(receipt, checkAmt);
		entry.setEntryNo(Integer.max(++entryNo, 201));
		entryList.add(entry);

		//主表
		FinReceivableCheck bill = createBillMain(receipt);

		this.submitAdd(bill, entryList);
	}

	private FinReceivableCheck createBillMain(FinReceipt receipt) {
		FinReceivableCheck bill = new FinReceivableCheck();
		bill.setReceivableCheckType("1"); //应收核销
		bill.setIsRubric(receipt.getIsRubric());
		bill.setIsAuto(1);
		bill.setSrcBillType(receipt.getBillType());
		bill.setSrcBillId(receipt.getId());
		bill.setSrcNo(receipt.getBillNo());
		String ruleCode = "fin_yshx_bill_no";
		bill.setBillNo((String) FillRuleUtil.executeRule(ruleCode, null));
		bill.setBillDate(receipt.getBillDate()); //注意：如果设为系统日期，有可能小于当前业务期间
		bill.setCustomerId(receipt.getCustomerId());
		return bill;
	}

	private FinReceivableCheckEntry createBillEntry(FinReceiptEntry receiptEntry) {
		List<FinReceivable> receivableList =
				finReceivableService.listNotVoided(receiptEntry.getSrcBillType(), receiptEntry.getSrcBillId());
		if (receivableList.size() != 1) {
			throw new JeecgBootException(receiptEntry.getSrcNo() + "：该出库单对应的应付单不能"
					+ (receivableList.size() == 0 ? "为0个！" : "多于1个！"));
		}
		FinReceivable receivable = receivableList.get(0);
		//应收单可能已被全部或部分核销，包括被手动核销，或被前面提交的收款单自动核销
		BigDecimal uncheckedAmt = receivable.getUncheckedAmt();
		BigDecimal receiptAmt = receiptEntry.getAmt();
		if (receiptAmt.multiply(uncheckedAmt).compareTo(BigDecimal.ZERO) <= 0) {
			//如果一个为蓝字、另一个红字，不能核销；如果其中一个为0，不核销；
			return null;
		}
		//两个都为蓝或都为红

		BigDecimal amt = receiptAmt.compareTo(BigDecimal.ZERO) > 0
				? receiptAmt.min(uncheckedAmt) : receiptAmt.max(uncheckedAmt);

		FinReceivableCheckEntry entry = new FinReceivableCheckEntry();
		entry.setCheckSide("1");
		entry.setSrcBillType(receivable.getBillType());
		entry.setSrcBillId(receivable.getId());
		entry.setSrcNo(receivable.getBillNo());
		entry.setAmt(amt);
		return entry;
	}

	private FinReceivableCheckEntry createBillEntry(FinReceipt receipt, BigDecimal checkAmt) {
		FinReceivableCheckEntry entry = new FinReceivableCheckEntry();
		entry.setCheckSide("2");
		entry.setSrcBillType(receipt.getBillType());
		entry.setSrcBillId(receipt.getId());
		entry.setSrcNo(receipt.getBillNo());
		entry.setAmt(checkAmt);
		return entry;
	}

}
