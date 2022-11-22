package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.mapper.FinPayableCheckEntryMapper;
import io.finer.erp.finance.mapper.FinPayableCheckMapper;
import io.finer.erp.finance.service.IFinPayableCheckService;
import io.finer.erp.finance.service.IFinPayableService;
import io.finer.erp.finance.service.IFinPaymentService;
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
 * @Description: 往来核销单
 * @Author: jeecg-boot
 * @Date:   2020-04-17
 * @Version: V1.0
 */
@Service
public class FinPayableCheckServiceImpl
		extends BillWithEntryServiceImpl<FinPayableCheckMapper, FinPayableCheck, FinPayableCheckEntryMapper, FinPayableCheckEntry>
		implements IFinPayableCheckService {

	@Autowired
	private IFinPayableService finPayableService;
	@Autowired
	private IFinPaymentService finPaymentService;

	@Override
	protected  void beforePersistAdd(FinPayableCheck bill, List<FinPayableCheckEntry> entryList){
		BigDecimal amt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			for(FinPayableCheckEntry entry: entryList) {
				if (entry.getCheckSide().equals("1") && entry.getAmt() != null) {
					amt = amt.add(entry.getAmt());
				}
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinPayableCheck bill, List<FinPayableCheckEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void writeBack(FinPayableCheck bill, boolean reverse) {
		List<FinPayableCheckEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		finPayableService.payableCheckWriteBack(entryList, reverse);
		finPaymentService.payableCheckWriteBack(entryList, reverse);
	}

	@Override
	protected void voidBillPreprocess(FinPayableCheck bill) throws Exception {
		//无后置单据
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createBill(FinPayment payment, List<FinPaymentEntry> paymentEntryList) throws Exception {
		//明细
		List<FinPayableCheckEntry> entryList = new ArrayList<>();
		BigDecimal checkAmt = new BigDecimal("0.00");
		int entryNo = 100;
		// CheckSide="1"
		for(FinPaymentEntry paymentEntry: paymentEntryList) {
			String srcBillType = paymentEntry.getSrcBillType();
			BigDecimal paymentAmt = paymentEntry.getAmt();
			if (!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")
					&& paymentAmt != null && paymentAmt.compareTo(BigDecimal.ZERO) != 0) {
				FinPayableCheckEntry entry = createBillEntry(paymentEntry);
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
		FinPayableCheckEntry entry = createBillEntry(payment, checkAmt);
		entry.setEntryNo(Integer.max(++entryNo, 201));
		entryList.add(entry);

		//主表
		FinPayableCheck bill = createBillMain(payment);

		this.submitAdd(bill, entryList);
	}

	private FinPayableCheck createBillMain(FinPayment payment) {
		FinPayableCheck bill = new FinPayableCheck();
		bill.setPayableCheckType("2"); //应付核销
		bill.setIsRubric(payment.getIsRubric());
		bill.setIsAuto(1);
		bill.setSrcBillType(payment.getBillType());
		bill.setSrcBillId(payment.getId());
		bill.setSrcNo(payment.getBillNo());
		String ruleCode = "fin_yfhx_bill_no";
		bill.setBillNo((String) FillRuleUtil.executeRule(ruleCode, null));
		bill.setBillDate(payment.getBillDate()); //注意：如果设为系统日期，有可能小于当前业务期间
		bill.setSupplierId(payment.getSupplierId());
		return bill;
	}

	private FinPayableCheckEntry createBillEntry(FinPaymentEntry paymentEntry) {
		List<FinPayable> payableList =
				finPayableService.listNotVoided(paymentEntry.getSrcBillType(), paymentEntry.getSrcBillId());
		if (payableList.size() != 1) {
			throw new JeecgBootException(paymentEntry.getSrcNo() + "：该出库单对应的应付单不能"
					+ (payableList.size() == 0 ? "为0个！" : "多于1个！"));
		}
		FinPayable payable = payableList.get(0);
		//应付单可能已被全部或部分核销，包括被手动核销，或被前面提交的付款单自动核销
		BigDecimal uncheckedAmt = payable.getUncheckedAmt();
		BigDecimal paymentAmt = paymentEntry.getAmt();
		if (paymentAmt.multiply(uncheckedAmt).compareTo(BigDecimal.ZERO) <= 0) {
			//如果一个为蓝字、另一个红字，不能核销；如果其中一个为0，不核销；
			return null;
		}
		//两个都为蓝或都为红
		BigDecimal amt = paymentAmt.compareTo(BigDecimal.ZERO) > 0
				? paymentAmt.min(uncheckedAmt) : paymentAmt.max(uncheckedAmt);

		FinPayableCheckEntry entry = new FinPayableCheckEntry();
		entry.setCheckSide("1");
		entry.setSrcBillType(payable.getBillType());
		entry.setSrcBillId(payable.getId());
		entry.setSrcNo(payable.getBillNo());
		entry.setAmt(amt);
		return entry;
	}

	private FinPayableCheckEntry createBillEntry(FinPayment payment, BigDecimal checkAmt) {
		FinPayableCheckEntry entry = new FinPayableCheckEntry();
		entry.setCheckSide("2");
		entry.setSrcBillType(payment.getBillType());
		entry.setSrcBillId(payment.getId());
		entry.setSrcNo(payment.getBillNo());
		entry.setAmt(checkAmt);
		return entry;
	}

}
