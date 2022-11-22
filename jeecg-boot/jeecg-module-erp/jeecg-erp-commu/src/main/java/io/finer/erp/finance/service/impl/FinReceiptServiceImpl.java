package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.mapper.FinReceiptEntryMapper;
import io.finer.erp.finance.mapper.FinReceiptMapper;
import io.finer.erp.finance.service.IFinReceiptReqService;
import io.finer.erp.finance.service.IFinReceiptService;
import io.finer.erp.finance.service.IFinReceivableCheckService;
import io.finer.erp.sale.service.ISalOrderService;
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
 * @Description: 收款单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class FinReceiptServiceImpl
		extends BillWithEntryServiceImpl<FinReceiptMapper, FinReceipt, FinReceiptEntryMapper, FinReceiptEntry>
		implements IFinReceiptService {

	@Lazy
	@Autowired
	private ISalOrderService salOrderService;

	@Autowired
	private IFinReceiptReqService finReceiptReqService;

	@Lazy
	@Autowired
	private IFinReceivableCheckService finReceivableCheckService;

	@Override
	protected  void beforePersistAdd(FinReceipt bill, List<FinReceiptEntry> entryList){
		if(entryList==null) {
			return;
		}
		if (bill.getReceiptType().startsWith("101") && getDistinctSrcBillIdCount(entryList) > 1) {
			//101* 销售预收; 同一订单在一个销售预收单中可以分录多次。
			throw new JeecgBootException(bill.getSrcNo() + "：销售预收单有只能有一个销售订单！");
		}

		BigDecimal amt = new BigDecimal("0.00");
		for(FinReceiptEntry entry:entryList) {
			if (entry.getAmt() != null) {
				amt = amt.add(entry.getAmt());
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinReceipt bill, List<FinReceiptEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected boolean hasFinishedExecute(FinReceipt bill) {
		int i = bill.getCheckedAmt().compareTo(bill.getAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;
		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void voidBillPreprocess(FinReceipt bill) throws Exception {
		//后置单据-应收核销单-自动作废：销售收款单生效后自动生成的
		List<FinReceivableCheck> list = finReceivableCheckService.listNotVoided(bill.getBillType(), bill.getId());
		for(FinReceivableCheck check: list) {
			if (check.getSrcBillId().equals(bill.getId()) && check.getIsAuto() == 1) {
				finReceivableCheckService.voidBill(check.getId());
			}
		}

		//后置单据-应收核销单：非自动生成的
		String billNos = finReceivableCheckService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

	@Override
	public BigDecimal getUncheckedAmt(String id) {
		FinReceipt finReceipt = getById(id);
		return finReceipt != null ? finReceipt.getUncheckedAmt() : null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void writeBack(FinReceipt bill, boolean reverse) throws Exception {
		List<FinReceiptEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

		//回写{如果明细源单为销售退货退款申请}
		finReceiptReqService.receiptWriteBack(bill, entryList, reverse);

		if (bill.getReceiptType().startsWith("101")) { //101 销售预收单
			//回写{如果明细源单为销售订单}
			for(FinReceiptEntry entry: entryList) {
				//如果源单不为SalOrder，不用获取salOrderService
				if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("SalOrder")) {
					salOrderService.receiptWriteBackPrereceiptBal(entryList, reverse);
					break;
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(FinReceipt bill) throws Exception {
		//生成应收核销单{如果明细源单为出库单}
		List<FinReceiptEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		finReceivableCheckService.createBill(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, FinReceipt> billMap = new HashMap<>();
		List<FinReceivableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinReceivableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinReceipt")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			FinReceipt bill = writtenBackPreprocess(writterEntry, billMap);

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
			if (bill.getReceiptType().startsWith("101")) { //101* 销售预收
				List<FinReceiptEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
				if (getDistinctSrcBillIdCount(entryList) != 1) {//同一订单在一个销售预收单中可以分录多次。
					throw new JeecgBootException(bill.getSrcNo() + "：销售预收应有且只能有一条明细！");
				}
				//由于多条分录对应同一源单，不拆分金额，用其中一条销售预收分录进行转置即可。
				writtenBackForward(entryList.get(0), writterEntry, checkEntryList1, FinReceivableCheckEntry.class);
			}
		}

		//后置状态
		for(FinReceipt bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回调：预收款核销，需回写销售订单的预收余额
		if (checkEntryList1.size() > 0) {
			//{销售预收单明细的源单为销售订单}
			for(FinReceivableCheckEntry entry: checkEntryList1) {
				//如果源单不为SalOrder，不用获取salOrderService
				if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("SalOrder")) {
					salOrderService.receivableCheckWriteBackPrereceiptBal(checkEntryList1, reverse);
					break;
				}
			}
		}
	}

}
