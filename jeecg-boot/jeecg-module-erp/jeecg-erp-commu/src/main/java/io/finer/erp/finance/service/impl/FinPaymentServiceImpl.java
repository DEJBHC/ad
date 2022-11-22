package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.mapper.FinPaymentEntryMapper;
import io.finer.erp.finance.mapper.FinPaymentMapper;
import io.finer.erp.finance.service.IFinPayableCheckService;
import io.finer.erp.finance.service.IFinPaymentReqService;
import io.finer.erp.finance.service.IFinPaymentService;
import io.finer.erp.purchase.service.IPurOrderService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 付款单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class FinPaymentServiceImpl
		extends BillWithEntryServiceImpl<FinPaymentMapper, FinPayment, FinPaymentEntryMapper, FinPaymentEntry>
		implements IFinPaymentService {

	@Lazy
	@Autowired
	private IPurOrderService purOrderService;
	@Autowired
	private IFinPaymentReqService finPaymentReqService;

	@Lazy
	@Autowired
	private IFinPayableCheckService finPayableCheckService;

	@Override
	protected  void beforePersistAdd(FinPayment bill, List<FinPaymentEntry> entryList){
		if(entryList==null) {
			return;
		}
		if (bill.getPaymentType().startsWith("201") && getDistinctSrcBillIdCount(entryList) != 1) {
			//201* 采购预付; 同一申请单或订单在一个采购预付单中可以分录多次。
			throw new JeecgBootException(bill.getSrcNo() + "：采购预付单有且只能有一个采购预付申请单或采购订单！");
		}

		BigDecimal amt = new BigDecimal("0.00");
		for(FinPaymentEntry entry:entryList) {
			if (entry.getAmt() != null) {
				amt = amt.add(entry.getAmt());
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinPayment bill, List<FinPaymentEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected boolean hasFinishedExecute(FinPayment bill) {
		int i = bill.getCheckedAmt().compareTo(bill.getAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;
		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void writeBack(FinPayment bill, boolean reverse) throws Exception {
		List<FinPaymentEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

		//回写{如果明细源单为付款申请}：采购预付（有申请）、采购付款(有申请）
		finPaymentReqService.paymentWriteBack(bill, entryList, reverse);

		//回写{如果明细源单为采购订单}：采购预付（无申请）
		if (bill.getPaymentType().startsWith("201")) { //201* 采购预付款
			for(FinPaymentEntry entry: entryList) {
				//如果源单不为PurOrder，不用获取purOrderService
				if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
					purOrderService.paymentWriteBackPrepaymentBal(entryList, reverse);
					break;
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(FinPayment bill) throws Exception {
		List<FinPaymentEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		//生成应付核销单{如果明细源单为入库单}：采购付款(无申请)、采购退货退款
		finPayableCheckService.createBill(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void voidBillPreprocess(FinPayment bill) throws Exception {
		//后置单据-应付核销单-自动作废：采购付款单生效后自动生成的
		List<FinPayableCheck> list = finPayableCheckService.listNotVoided(bill.getBillType(), bill.getId());
		for(FinPayableCheck payableCheck: list) {
			if (payableCheck.getSrcBillId().equals(bill.getId()) && payableCheck.getIsAuto() == 1) {
				finPayableCheckService.voidBill(payableCheck.getId());
			}
		}

		//后置单据-应付核销单：非自动生成的
		String billNos = finPayableCheckService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

	@Override
	public BigDecimal getUncheckedAmt(String id) {
		FinPayment finPayment = getById(id);
		return finPayment != null ? finPayment.getUncheckedAmt() : null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, FinPayment> billMap = new HashMap<>();
		List<FinPayableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinPayableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinPayment")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			FinPayment bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			BigDecimal checkedAmt = bill.getCheckedAmt().add(writterEntryAmt);
			int i = checkedAmt.compareTo(bill.getAmt());
			if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
				throw new JeecgBootException(bill.getBillNo() + "：核销金额不能超出未核金额！");
			}
			bill.setCheckedAmt(checkedAmt);

			//向前转置
			if (bill.getPaymentType().startsWith("201")) { //201* 采购预付
				List<FinPaymentEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
				if (getDistinctSrcBillIdCount(entryList) != 1) {//同一申请单或订单在一个采购预付单中可以分录多次。
					throw new JeecgBootException(bill.getSrcNo() + "：采购预付单有且只能有一个申请单或订单！");
				}
				//由于多条分录对应同一源单，不拆分金额，用其中一条采购预付分录进行转置即可。
				writtenBackForward(entryList.get(0), writterEntry, checkEntryList1, FinPayableCheckEntry.class);
			}
		}

		//后置状态
		for(FinPayment bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回调：预付款核销，需回写采购订单的预付余额
		if (checkEntryList1.size() > 0) {
			//{采购预付款明细的源单为采购预付申请单}
			finPaymentReqService.payableCheckWriteBackPrepaymentBal(checkEntryList1, reverse);

			//{采购预付款明细的源单为采购订单}
			for(FinPayableCheckEntry entry: checkEntryList1) {
				//如果源单不为PurOrder，不用获取purOrderService
				if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
					purOrderService.payableCheckWriteBackPrepaymentBal(checkEntryList1, reverse);
					break;
				}
			}
		}
	}
}
