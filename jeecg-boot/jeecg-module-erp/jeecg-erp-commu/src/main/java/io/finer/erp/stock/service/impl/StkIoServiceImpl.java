package io.finer.erp.stock.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.common.util.BillUtils;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.service.*;
import io.finer.erp.purchase.service.IPurOrderService;
import io.finer.erp.sale.service.ISalOrderService;
import io.finer.erp.stock.entity.StkCheck;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.mapper.StkIoEntryMapper;
import io.finer.erp.stock.mapper.StkIoMapper;
import io.finer.erp.stock.service.IStkInventoryService;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version:
 */
@Service
public class StkIoServiceImpl
		extends BillWithEntryServiceImpl<StkIoMapper, StkIo, StkIoEntryMapper, StkIoEntry>
		implements IStkIoService {

	@Autowired
	private IStkInventoryService stkInventoryService;

	@Lazy
	@Autowired
	private IPurOrderService purOrderService;
	@Lazy
	@Autowired
	private IFinPayableService finPayableService;
	@Lazy
	@Autowired
	private IFinPurInvoiceService finPurInvoiceService;
	@Lazy
	@Autowired
	private IFinPaymentReqService finPaymentReqService;
	@Lazy
	@Autowired
	private IFinPaymentService finPaymentService;

	@Lazy
	@Autowired
	private ISalOrderService salOrderService;
	@Lazy
	@Autowired
	private IFinReceivableService finReceivableService;
	@Lazy
	@Autowired
	private IFinSalInvoiceService finSalInvoiceService;
	@Lazy
	@Autowired
	private IFinReceiptService finReceiptService;

	@Override
	protected  void beforePersistAdd(StkIo bill, List<StkIoEntry> entryList){
		BigDecimal cost = new BigDecimal("0.00");
		BigDecimal settleAmt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			String supplierId = bill.getSupplierId();
			supplierId = supplierId != null ? supplierId : "";
			for(StkIoEntry entry:entryList) {
				entry.setSupplierId(supplierId);
				if (entry.getCost() != null) {
					cost = cost.add(entry.getCost());
				}
				if (entry.getSettleAmt() != null) {
					settleAmt = settleAmt.add(entry.getSettleAmt());
				}
			}
		}
		bill.setCost(cost);
		bill.setSettleAmt(settleAmt);
	}

	@Override
	protected void beforePersistEdit(StkIo bill, List<StkIoEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(StkIo bill) throws Exception {
		List<StkIoEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

		//后置单据-应收应付：自动生成
		if (bill.getHasRp() == 1) {
			if (bill.getStockIoType().startsWith("1")){
				//finPayableService.createPayable(bill, entryList);
				finPayableService.createBill(bill);
			} else if (bill.getStockIoType().startsWith("2")) {
				finReceivableService.createBill(bill);
			}
		}

		//后置单据-实时库存：更新
		stkInventoryService.updateInventory(bill, entryList, false);
	}

	@Override
	protected boolean hasFinishedExecute(StkIo bill) {
		//后置回写——已结算金额
		int i = bill.getSettledAmt().compareTo(bill.getSettleAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;

		//	后置回写——已开票金额
		String invoiceType = bill.getInvoiceType();
		if (invoiceType != null && invoiceType.startsWith("1")) {
			i = bill.getInvoicedAmt().compareTo(bill.getSettleAmt());
			b = b && (bill.getIsRubric() == 0 ? i >= 0 : i <= 0);
		}

		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	protected void writeBack(StkIo bill, boolean reverse) {
		//如果是退货，不向前回写订单（订单可能已关闭、结算毛利润等）
		String srcBillType = bill.getSrcBillType();
		if (bill.getIsReturned() == 0 && !StringUtils.isEmpty(srcBillType)) {
			List<StkIoEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

			//如果源单不为PurOrder、SalOrder，不用获取purOrderService、salOrderService
			if (srcBillType.startsWith("PurOrder")) {
				purOrderService.stkIoWriteBack(entryList, reverse);
			}
			else if (srcBillType.startsWith("SalOrder")) {
				salOrderService.stkIoWriteBack(entryList, reverse);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void voidBillPreprocess(StkIo bill) throws Exception {
		//后置单据-采购发票、采购付款申请、采购付款
		String billNos = null;
		if (!bill.getStockIoType().startsWith("1")) {
			billNos = finPurInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			if (StringUtils.isEmpty(billNos)) {
				billNos = finPaymentReqService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
			if (StringUtils.isEmpty(billNos)) {
				billNos = finPaymentService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
		}

		//后置单据-销售发票、销售收款
		if (StringUtils.isEmpty(billNos) && !bill.getStockIoType().startsWith("2")) {
			billNos = finSalInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			if (StringUtils.isEmpty(billNos)) {
				billNos = finReceiptService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
		}
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}

		//后置单据-应付单、应收单-自动作废：出入库据生效后自动生成的
		if (bill.getStockIoType().startsWith("1")) {
			List<FinPayable> list = finPayableService.listNotVoided(bill.getBillType(), bill.getId());
			if (list != null) {
				for(FinPayable f: list) {
					finPayableService.voidBill(f.getId());
				}
			}
		} else if (bill.getStockIoType().startsWith("2")) {
			List<FinReceivable> list = finReceivableService.listNotVoided(bill.getBillType(), bill.getId());
			if (list != null) {
				for(FinReceivable f: list) {
					finReceivableService.voidBill(f.getId());
				}
			}
		}

		//后置单据-应付单、应收单-不自动作废：非出入库据生效后自动生成的；先做自动作废，否则应该自动作废的也会视为“未作废”
		if (!bill.getStockIoType().startsWith("1")) {
			billNos = finPayableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		}
		if (StringUtils.isEmpty(billNos) && !bill.getStockIoType().startsWith("2")) {
			billNos = finReceivableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		}
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}

		//后置单据-实时库存：更新
		List<StkIoEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		stkInventoryService.updateInventory(bill, entryList, true);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createInBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList) throws Exception {
		createBill("102", stkCheck, stkCheckEntryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createOutBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList) throws Exception {
		createBill("202", stkCheck, stkCheckEntryList);
	}

	private void createBill(String stockIoType, StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList) throws Exception {
		StkIo bill = new StkIo();
		bill.setStockIoType(stockIoType);
		bill.setIsAuto(1);
		bill.setIsRubric(0);
		bill.setSrcBillType(stkCheck.getBillType());
		bill.setSrcBillId(stkCheck.getId());
		bill.setSrcNo(stkCheck.getBillNo());
		String ruleCode = stockIoType.equals("102") ? "stk_pyrk_bill_no":"stk_pkck_bill_no";
		bill.setBillNo((String) FillRuleUtil.executeRule(ruleCode, null));
		bill.setBillDate(stkCheck.getBillDate()); //注意：如果设为系统日期，有可能小于当前业务期间
		bill.setHandler(stkCheck.getChecker());
		bill.setHasRp(0);
		bill.setHasSwell(0);

		List<StkIoEntry> entryList = new ArrayList<>();
		int entryNo = 0;
		for(StkCheckEntry checkEntity: stkCheckEntryList) {
			BigDecimal qty = checkEntity.getProfitQty(); //profitQty: 盘盈为正，盘亏为负
			if (stockIoType.equals("202")) {
				qty = qty.negate();
			}

			StkIoEntry entry = new StkIoEntry();
			entry.setEntryNo(++entryNo);
			entry.setSrcBillType(stkCheck.getBillType());
			entry.setSrcBillId(stkCheck.getId());
			entry.setSrcEntryId(checkEntity.getId());
			entry.setSrcNo(String.format("%s:%d", checkEntity.getBillNo(), checkEntity.getEntryNo()));
			entry.setWarehouseId(checkEntity.getWarehouseId());
			entry.setMaterialId(checkEntity.getMaterialId());
			entry.setBatchNo(checkEntity.getBatchNo());
			entry.setUnitId(checkEntity.getUnitId());
			entry.setStockIoDirection(stockIoType.substring(0,1));
			entry.setSwellQty(BigDecimal.ZERO);
			entry.setQty(qty);
			entry.setExpense(BigDecimal.ZERO);
			entry.setCost(BigDecimal.ZERO);
			entry.setSupplierId(checkEntity.getSupplierId());
			entryList.add(entry);
		}

		this.submitAdd(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		List<FinPayableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinPayableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("StkIo")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			StkIo bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			bill.setSettledAmt(bill.getSettledAmt().add(writterEntryAmt));

			//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
			if (bill.getIsReturned() == 0) {
				writtenBackForward(bill, writterEntry, checkEntryList1, FinPayableCheckEntry.class);
			}
		}

		//后置状态
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回写
		for(FinPayableCheckEntry entry: checkEntryList1) {
			//如果源单不为 PurOrder，不用获取 purOrderService
			if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
				purOrderService.payableCheckWriteBackSettledAmt(checkEntryList1, reverse);
				break;
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		List<FinReceivableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinReceivableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("StkIo")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			StkIo bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			bill.setSettledAmt(bill.getSettledAmt().add(writterEntryAmt));

			//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
			if (bill.getIsReturned() == 0) {
				writtenBackForward(bill, writterEntry, checkEntryList1, FinReceivableCheckEntry.class);
			}
		}

		//后置状态
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回写
		for(FinReceivableCheckEntry entry: checkEntryList1) {
			//如果源单不为SalOrder，不用获取salOrderService
			if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("SalOrder")) {
				salOrderService.receivableCheckWriteBackSettledAmt(checkEntryList1, reverse);
				break;
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void purInvoiceWriteBack(List<FinPurInvoiceEntry> invoiceEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		Map<String, StkIoEntry> entryMap = new HashMap<>();
		List<FinPurInvoiceEntry> invoiceEntryList1 = new ArrayList<>();
		for(FinPurInvoiceEntry writter: invoiceEntryList) {
			String srcBillType = writter.getSrcBillType();
			if(!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")) {
				Pair<StkIo, StkIoEntry> pair =
						this.invoiceWriteBack(writter.getSrcBillId(), writter.getSrcEntryId(), writter.getSrcNo(),
						writter.getUnitId(), writter.getQty(), writter.getAmt(), reverse, billMap, entryMap);

				//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
				if (pair.getFirst().getIsReturned() == 0) {
					writtenBackForward(pair.getSecond(), writter, invoiceEntryList1, FinPurInvoiceEntry.class);
				}
			}
		}

		for(StkIoEntry entry: entryMap.values()) {
			this.entryMapper.updateById(entry);
		}
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回调
		for(FinPurInvoiceEntry entry: invoiceEntryList1) {
			//如果源单不为PurOrder，不用获取purOrderService
			if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
				purOrderService.purInvoiceWriteBack(invoiceEntryList1, reverse);
				break;
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void salInvoiceWriteBack(List<FinSalInvoiceEntry> invoiceEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		Map<String, StkIoEntry> entryMap = new HashMap<>();
		List<FinSalInvoiceEntry> invoiceEntryList1 = new ArrayList<>();
		for(FinSalInvoiceEntry writter: invoiceEntryList) {
			String srcBillType = writter.getSrcBillType();
			if(!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")) {
				Pair<StkIo, StkIoEntry> pair =
						this.invoiceWriteBack(writter.getSrcBillId(), writter.getSrcEntryId(), writter.getSrcNo(),
						writter.getUnitId(), writter.getQty(), writter.getAmt(), reverse, billMap, entryMap);

				//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
				if (pair.getFirst().getIsReturned() == 0) {
					writtenBackForward(pair.getSecond(), writter, invoiceEntryList1, FinSalInvoiceEntry.class);
				}
			}
		}

		for(StkIoEntry entry: entryMap.values()) {
			this.entryMapper.updateById(entry);
		}
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		//向前回调
		for(FinSalInvoiceEntry entry: invoiceEntryList1) {
			//如果源单不为SalOrder，不用获取salOrderService
			if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("SalOrder")) {
				salOrderService.salInvoiceWriteBack(invoiceEntryList1, reverse);
				break;
			}
		}
	}

	private Pair<StkIo, StkIoEntry> invoiceWriteBack(String srcBillId, String srcEntryId, String srcNo,
													 String unitId, BigDecimal qty, BigDecimal amt, boolean reverse,
													 Map<String, StkIo> billMap, Map<String, StkIoEntry> entryMap) {
		//前置状态
		StkIo bill = billMap.get(srcBillId);
		if (bill == null) {
			bill = this.baseMapper.selectById(srcBillId);
			if (bill == null) {
				throw new JeecgBootException(srcNo.split(":")[0] + "：单据未找到，可能被其他用户删除了！");
			}

			if (bill.getIsEffective() == 0 || bill.getIsVoided() == 1) {
				throw new JeecgBootException(bill.getBillNo() + "：单据未生效或被作废，不能被回写！");
			}
			billMap.put(srcBillId, bill);
		}

		StkIoEntry entry = entryMap.get(srcEntryId);
		if (entry == null) {
			entry = this.entryMapper.selectById(srcEntryId);
			if (entry == null){
				throw new JeecgBootException(srcNo + "：出入库分录不存在！");
			}
			entryMap.put(srcEntryId, entry);
		}

		//数据处理
		if (!unitId.equals(entry.getUnitId())) {
			qty = BillUtils.convertUnit(qty, unitId, entry.getUnitId());
		}
		if (reverse){
			qty = qty.negate();
			amt = amt.negate();
		}

		entry.setInvoicedQty(entry.getInvoicedQty().add(qty));
		int i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
		if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
			throw new JeecgBootException(srcNo + "：开票数量不能超出未开票数量！");
		}

		entry.setInvoicedAmt(entry.getInvoicedAmt().add(amt));
		i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
		if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
			throw new JeecgBootException(srcNo + "：开票金额不能超出未开票金额！");
		}
		bill.setInvoicedAmt(bill.getInvoicedAmt().add(amt));

		return Pair.of(bill, entry);
	}

}
