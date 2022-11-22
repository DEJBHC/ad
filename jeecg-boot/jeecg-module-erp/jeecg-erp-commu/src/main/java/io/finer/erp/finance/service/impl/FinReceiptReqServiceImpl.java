package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.FinReceipt;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.finer.erp.finance.entity.FinReceiptReq;
import io.finer.erp.finance.entity.FinReceiptReqEntry;
import io.finer.erp.finance.mapper.FinReceiptReqEntryMapper;
import io.finer.erp.finance.mapper.FinReceiptReqMapper;
import io.finer.erp.finance.service.IFinReceiptReqService;
import io.finer.erp.finance.service.IFinReceiptService;
import io.finer.erp.finance.service.IFinReceivableCheckService;
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
 * @Description: (红字)收款申请单
 * @Author: jeecg-boot
 * @Date:   2022-09-07
 * @Version: V1.0
 */
@Service
public class FinReceiptReqServiceImpl
		extends BillWithEntryServiceImpl<FinReceiptReqMapper, FinReceiptReq, FinReceiptReqEntryMapper, FinReceiptReqEntry>
		implements IFinReceiptReqService {
	@Lazy
	@Autowired
	private IFinReceiptService finReceiptService;
	@Lazy
	@Autowired
	private IFinReceivableCheckService finReceivableCheckService;

	@Override
	protected  void beforePersistAdd(FinReceiptReq bill, List<FinReceiptReqEntry> entryList){
		if (entryList==null) {
			return;
		}
		if (bill.getIsRubric() == 1 && entryList.size() != 1) {
			throw new JeecgBootException(bill.getSrcNo() + "：销售退货退款申请单有只能有一个销售退货入库单！");
		}

		BigDecimal amt = new BigDecimal("0.00");
		for(FinReceiptReqEntry entry:entryList) {
			if (entry.getAmt() != null) {
				amt = amt.add(entry.getAmt());
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinReceiptReq bill, List<FinReceiptReqEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected void writeBack(FinReceiptReq bill, boolean reverse) {
		//无回写
	}

	@Override
	protected boolean hasFinishedExecute(FinReceiptReq bill) {
		int i = bill.getReceivedAmt().compareTo(bill.getAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;
		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	protected void voidBillPreprocess(FinReceiptReq bill) {
		//后置单据-收款单
		String billNos = finReceiptService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void receiptWriteBack(FinReceipt writter, List<FinReceiptEntry> writterEntryList, boolean reverse) throws Exception {
		Map<String, FinReceiptReq> billMap = new HashMap<>();
		//按FinReceiptReq的明细展开：
		//  (红字)销售收款申请单明细的源单为(红字)销售出库单。
		List<FinReceiptEntry> writterEntryList1 =  new ArrayList<>();

		for(FinReceiptEntry writterEntry: writterEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinReceiptReq")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}
			if (writter.getIsRubric() == 0) {
				throw new JeecgBootException(writter.getBillNo() + ": 蓝字收款单不能回写红字收款申请单！");
			}

			//前置条件、预处理
			FinReceiptReq bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			bill.setReceivedAmt(bill.getReceivedAmt().add(writterEntryAmt));
			int i = bill.getReceivedAmt().compareTo(bill.getAmt());
			if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
				throw new JeecgBootException(writterEntry.getSrcNo() + "：收款金额不能超出申请单的未收金额！");
			}

			List<FinReceiptReqEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
			if (entryList.size() != 1) {
				throw new JeecgBootException(bill.getSrcNo() + "：销售退货退款申请有只能有一个销售退货入库单！");
			}
			FinReceiptReqEntry entry = entryList.get(0);
			entry.setReceivedAmt(entry.getReceivedAmt().add(writterEntryAmt));
			this.entryMapper.updateById(entry);

			//向前转置
			writtenBackForward(entry, writterEntry, writterEntryList1, FinReceiptEntry.class);
		}

		for(FinReceiptReq bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		if (writterEntryList1.size() > 0) {
			finReceivableCheckService.createBill(writter, writterEntryList1);
		}
	}

}
