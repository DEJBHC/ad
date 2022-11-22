package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.mapper.FinPaymentReqEntryMapper;
import io.finer.erp.finance.mapper.FinPaymentReqMapper;
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
 * @Description: 付款申请单
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
@Service
public class FinPaymentReqServiceImpl
		extends BillWithEntryServiceImpl<FinPaymentReqMapper, FinPaymentReq, FinPaymentReqEntryMapper, FinPaymentReqEntry>
		implements IFinPaymentReqService {

	@Lazy
	@Autowired
	private IPurOrderService purOrderService;
	@Lazy
	@Autowired
	private IFinPaymentService finPaymentService;
	@Lazy
	@Autowired
	private IFinPayableCheckService finPayableCheckService;

	@Override
	protected  void beforePersistAdd(FinPaymentReq bill, List<FinPaymentReqEntry> entryList){
		if(entryList==null) {
			return;
		}
		if (bill.getPaymentType().startsWith("201") && entryList.size() != 1) { //201* 采购预付
			throw new JeecgBootException(bill.getSrcNo() + "：采购预付申请单有只能有一个采购订单！");
		}

		BigDecimal amt = new BigDecimal("0.00");
		for(FinPaymentReqEntry entry:entryList) {
			if (entry.getAmt() != null) {
				amt = amt.add(entry.getAmt());
			}
		}
		bill.setAmt(amt);
	}

	@Override
	protected void beforePersistEdit(FinPaymentReq bill, List<FinPaymentReqEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	@Override
	protected void writeBack(FinPaymentReq bill, boolean reverse) {
		//无回写
	}

	@Override
	protected boolean hasFinishedExecute(FinPaymentReq bill) {
		int i = bill.getPaidAmt().compareTo(bill.getAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;
		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	protected void voidBillPreprocess(FinPaymentReq bill) {
		//后置单据-付款单
		String billNos = finPaymentService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void paymentWriteBack(FinPayment writter, List<FinPaymentEntry> writterEntryList, boolean reverse) throws Exception {
		Map<String, FinPaymentReq> billMap = new HashMap<>();
		//按FinPaymentReq的明细展开：
		//  采购预付款申请明细的源单为采购订单（同一个）;
		//  采购付款申请明细的源单为采购入库单（可多个）。
		List<FinPaymentEntry> writterEntryList1 =  new ArrayList<>();

		for(FinPaymentEntry writterEntry: writterEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinPaymentReq")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}
			if (writter.getIsRubric() == 1) {
				throw new JeecgBootException("红字付款单不能回写付款申请单！");
			}

			//前置条件、预处理
			FinPaymentReq bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (!reverse) {
				bill.setPaidAmt(bill.getPaidAmt().add(writterEntryAmt));
				int i = bill.getPaidAmt().compareTo(bill.getAmt());
				if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
					throw new JeecgBootException(writterEntry.getSrcNo() + "：付款金额不能超出申请单的未付金额！");
				}
			}
			else {
				bill.setPaidAmt(bill.getPaidAmt().subtract(writterEntryAmt));
				//不用判断作废金额是否超额：reverse用于生效单据作废，而作废时单据金额不能改变。
			}

			//向前转置分录：按付款申请单明细生成
			List<FinPaymentReqEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
			Map<String, FinPaymentEntry> entryMap = new HashMap<>();
			for(FinPaymentReqEntry entry: entryList) {
				//向前转置
				FinPaymentEntry writterEntry1 = writtenBackForward(entry, writterEntry, writterEntryList1, FinPaymentEntry.class);
				entryMap.put(entry.getId(), writterEntry1);
				writterEntry1.setAmt(BigDecimal.ZERO);
			}

			//付款金额拆分：按申请金额比例拆分到付款申请单明细（向前转置分录）
			BigDecimal totalAmt = new BigDecimal("0.00");
			for(FinPaymentReqEntry entry: entryList) {
				BigDecimal paidAmt = writterEntryAmt.multiply(entry.getAmt()).divide(bill.getAmt(), 2, BigDecimal.ROUND_HALF_UP);
				//min() 不能用于红字单据
				paidAmt = paidAmt.min(writterEntryAmt);
				if (!reverse) {
					paidAmt = paidAmt.min(entry.getAmt().subtract(entry.getPaidAmt())); //不能超过未付金额
					entry.setPaidAmt(entry.getPaidAmt().add(paidAmt));
				}
				else {
					paidAmt = paidAmt.min(entry.getPaidAmt()); //不能超出已付金额
					entry.setPaidAmt(entry.getPaidAmt().subtract(paidAmt));
				}
				//设置转置分录
				FinPaymentEntry writterEntry1 = entryMap.get(entry.getId());
				writterEntry1.setAmt(paidAmt);

				totalAmt = totalAmt.add(paidAmt);
				if (totalAmt.compareTo(writterEntryAmt) >= 0) {
					break;
				}
			}

			//拆分误差处理：不能用于红字单据
			totalAmt = writterEntryAmt.subtract(totalAmt);
			if (totalAmt.compareTo(BigDecimal.ZERO) > 0) {// 少拆分了
				for(FinPaymentReqEntry entry: entryList) {
					BigDecimal paidAmt;
					if (!reverse) {
						paidAmt = entry.getAmt().subtract(entry.getPaidAmt()); //不能超过未付金额
						if (paidAmt.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
						paidAmt = paidAmt.min(totalAmt);
						entry.setPaidAmt(entry.getPaidAmt().add(paidAmt));//少加了，加回去
					}
					else {
						paidAmt = entry.getPaidAmt().min(totalAmt); //不能超过已付金额
						entry.setPaidAmt(entry.getPaidAmt().subtract(paidAmt));//少减了，减回去
					}

					//转置分录
					FinPaymentEntry writterEntry1 = entryMap.get(entry.getId());
					writterEntry1.setAmt(writterEntry1.getAmt().add(paidAmt)); //加上少拆分金额

					totalAmt = totalAmt.subtract(paidAmt);
					if (totalAmt.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}
			}
			else if (totalAmt.compareTo(BigDecimal.ZERO) < 0) {// 多拆分了
				totalAmt = totalAmt.negate();
				for(FinPaymentReqEntry entry: entryList) {
					BigDecimal paidAmt;
					if (!reverse) {
						paidAmt = entry.getPaidAmt();
						if (paidAmt.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
						paidAmt = paidAmt.min(totalAmt); //不能超过已付金额
						entry.setPaidAmt(entry.getPaidAmt().subtract(paidAmt));//多加了，减回去
					}
					else {
						paidAmt = entry.getPaidAmt().subtract(entry.getPaidAmt()); //不能超过未付金额
						paidAmt = paidAmt.min(totalAmt);
						entry.setPaidAmt(entry.getPaidAmt().add(paidAmt));//多减了，加回去
					}

					//转置分录
					FinPaymentEntry writterEntry1 = entryMap.get(entry.getId());
					writterEntry1.setAmt(writterEntry1.getAmt().subtract(paidAmt)); //减去多拆分金额

					totalAmt = totalAmt.subtract(paidAmt);
					if (totalAmt.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}
			}

			for(FinPaymentReqEntry entry: entryList) {
				this.entryMapper.updateById(entry);
			}
		}

		for(FinPaymentReq bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}

		if (writter.getPaymentType().startsWith("201")) { //201* 采购预付
			//向前回写：订单预付余额
			for(FinPaymentEntry entry: writterEntryList1) {
				//如果源单不为PurOrder，不用获取purOrderService
				if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
					purOrderService.paymentWriteBackPrepaymentBal(writterEntryList1, reverse);
					break;
				}
			}
		}
		else {
			//自动生成：应付核销单
			//  reverse==true时（单据作废）， 自动生成的单据，在付款单据作废前自动作废
			finPayableCheckService.createBill(writter, writterEntryList1);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payableCheckWriteBackPrepaymentBal(List<FinPayableCheckEntry> writterEntryList, boolean reverse) {
	 	//简化方案：采购预付款申请只包括一个采购订单，不进行金额分拆
		Map<String, FinPaymentReq> billMap = new HashMap<>();
		List<FinPayableCheckEntry> writterEntryList1 =  new ArrayList<>();
		for(FinPayableCheckEntry writterEntry: writterEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinPaymentReq")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}
			if (writterEntryAmt.compareTo(BigDecimal.ZERO) < 0) {
				throw new JeecgBootException("预付款单的金额不能出现红字（负数）！");
			}

			//前置条件、预处理
			FinPaymentReq bill = writtenBackPreprocess(writterEntry, billMap);

			//向前转置
			List<FinPaymentReqEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
			if (bill.getPaymentType().startsWith("201") && entryList.size() != 1) { //201* 采购预付
				throw new JeecgBootException(bill.getSrcNo() + "：采购预付申请有且只能有一个采购订单！");
			}
			writtenBackForward(entryList.get(0), writterEntry, writterEntryList1, FinPayableCheckEntry.class);
		}

		//向前回调
		for(FinPayableCheckEntry entry: writterEntryList1) {
			//如果源单不为 PurOrder，不用获取 purOrderService
			if (!StringUtils.isEmpty(entry.getSrcBillType()) && entry.getSrcBillType().startsWith("PurOrder")) {
				purOrderService.payableCheckWriteBackPrepaymentBal(writterEntryList1, reverse);
				break;
			}
		}
	}

//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public void payableCheckWriteBackPrepaymentBal(List<FinPayableCheckEntry> writterEntryList, boolean reverse) {
//	    // 复杂方案：采购预付款申请可包括多个采购订单，需进行金额分拆
//		Map<String, FinPaymentReq> billMap = new HashMap<>();
//
//		//按FinPaymentReq的明细展开：
//		//  采购预付款申请明细的源单为采购订单
//		List<FinPayableCheckEntry> writterEntryList1 =  new ArrayList<>();
//
//		Map<String, PurOrder> orderMap = new HashMap<>();
//
//		for(FinPayableCheckEntry writterEntry: writterEntryList) {
//			String srcBillType = writterEntry.getSrcBillType();
//			BigDecimal writterEntryAmt = writterEntry.getAmt();
//			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinPaymentReq")
//					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
//				continue;
//			}
//			if (writterEntryAmt.compareTo(BigDecimal.ZERO) < 0) {
//				throw new JeecgBootException("预付款单的金额不能出现红字（负数）！");
//			}
//
//			//前置条件、预处理
//			FinPaymentReq bill = writtenBackPreprocess(writterEntry, billMap);
//
//			//向前转置分录：按付款申请单明细生成
//			List<FinPaymentReqEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
//			Map<String, FinPayableCheckEntry> entryMap = new HashMap<>();
//			for(FinPaymentReqEntry entry: entryList) {
//				FinPayableCheckEntry writterEntry1 = writtenBackForward(entry, writterEntry, writterEntryList1, FinPayableCheckEntry.class);
//				entryMap.put(entry.getId(), writterEntry1);
//				writterEntry1.setAmt(BigDecimal.ZERO);
//			}
//
//			//核销金额拆分：按已付金额比例拆分到付款申请单明细（向前转置分录）
//			BigDecimal totalAmt = new BigDecimal("0.00");
//			for(FinPaymentReqEntry entry: entryList) {
//				//由于误差处理，可能会导致多减0.01元，所以要检查订单预付余额为0时不能再减。
//				PurOrder order = orderMap.get(entry.getSrcBillId());
//				if (order == null) {
//					order = purOrderService.getById(entry.getSrcBillId());
//					if (order == null) {
//						continue;
//					}
//					orderMap.put(entry.getSrcBillId(), order);
//				}
//
//				BigDecimal checkedAmt = writterEntryAmt.multiply(entry.getPaidAmt()).divide(bill.getPaidAmt(), 2, BigDecimal.ROUND_HALF_UP);
//				checkedAmt = checkedAmt.min(writterEntryAmt).min(entry.getPaidAmt());
//				if (!reverse) {
//					checkedAmt = checkedAmt.min(order.getPrepaymentBal());//不能超出该订单的预付余额
//					order.setPrepaymentBal(order.getPrepaymentBal().subtract(checkedAmt));
//				}
//				//设置转置分录
//				FinPayableCheckEntry writterEntry1 = entryMap.get(entry.getId());
//				writterEntry1.setAmt(checkedAmt);
//
//				totalAmt = totalAmt.add(checkedAmt);
//				if (totalAmt.compareTo(writterEntryAmt) <= 0) {
//					break;
//				}
//			}
//
//			//拆分误差处理：不能用于红字单据
//			totalAmt = writterEntryAmt.subtract(totalAmt);
//			if (totalAmt.compareTo(BigDecimal.ZERO) > 0) {//少拆分了
//				for(FinPaymentReqEntry entry: entryList) {
//					PurOrder order = orderMap.get(entry.getSrcBillId());
//					FinPayableCheckEntry writterEntry1 = entryMap.get(entry.getId());
//					BigDecimal checkedAmt;
//					if (!reverse) {
//						checkedAmt = entry.getPaidAmt().subtract(writterEntry1.getAmt()); //不能超过申请单分录（已付金额-本次核销）
//						checkedAmt = checkedAmt.min(order.getPrepaymentBal()); //不能超过申请单分录对应订单的预付余额
//						if (checkedAmt.compareTo(BigDecimal.ZERO) <= 0) {
//							continue;
//						}
//						checkedAmt = checkedAmt.min(totalAmt);
//						order.setPrepaymentBal(order.getPrepaymentBal().subtract(checkedAmt));//少减了，减回去
//					}
//					else {
//						checkedAmt = entry.getPaidAmt().min(totalAmt);
//						order.setPrepaymentBal(order.getPrepaymentBal().add(checkedAmt));//少加了，加回去
//					}
//					writterEntry1.setAmt(writterEntry1.getAmt().add(checkedAmt));//加上少拆分金额
//					totalAmt = totalAmt.subtract(checkedAmt);
//					if (totalAmt.compareTo(BigDecimal.ZERO) <= 0) {
//						break;
//					}
//				}
//			}
//			else if (totalAmt.compareTo(BigDecimal.ZERO) < 0) {// 多拆分了
//				totalAmt = totalAmt.negate();
//				for(FinPaymentReqEntry entry: entryList) {
//					PurOrder order = orderMap.get(entry.getSrcBillId());
//					FinPayableCheckEntry writterEntry1 = entryMap.get(entry.getId());
//					BigDecimal checkedAmt;
//					if (!reverse) {
//
//						checkedAmt = entry.getPaidAmt().subtract(writterEntry1.getAmt());
//						checkedAmt = checkedAmt.min(order.getPrepaymentBal());
//						if (checkedAmt.compareTo(BigDecimal.ZERO) <= 0) {
//							continue;
//						}
//						checkedAmt = checkedAmt.min(totalAmt);
//						order.setPrepaymentBal(order.getPrepaymentBal().add(checkedAmt));//多减了，加回去
//					}
//					else {
//						checkedAmt = entry.getPaidAmt().min(totalAmt);
//						order.setPrepaymentBal(order.getPrepaymentBal().subtract(checkedAmt));//多加了，减回去
//					}
//					writterEntry1.setAmt(writterEntry1.getAmt().subtract(checkedAmt));//减去多拆分金额
//					totalAmt = totalAmt.subtract(checkedAmt);
//					if (totalAmt.compareTo(BigDecimal.ZERO) <= 0) {
//						break;
//					}
//				}
//			}
//		}
//
//		//向前回调
//		purOrderService.payableCheckWriteBackPrepaymentBal(writterEntryList1, reverse);
//	}

}
