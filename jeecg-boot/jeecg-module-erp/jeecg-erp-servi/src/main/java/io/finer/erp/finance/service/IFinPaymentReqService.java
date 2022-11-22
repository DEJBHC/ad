package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 付款申请单
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
public interface IFinPaymentReqService extends IBillWithEntryService<FinPaymentReq, FinPaymentReqEntry> {

	@Transactional(rollbackFor = Exception.class)
	void paymentWriteBack(FinPayment payment, List<FinPaymentEntry> paymentEntryList, boolean reverse) throws Exception;

	/**
	 * 后置回写更新——预付余额：FinPaymentServiceImpl.payableCheckWriteBack()中预付款时调用
	 * @param writterEntryList： 应付核销中的预付款
	 * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
	 */
	@Transactional(rollbackFor = Exception.class)
	void payableCheckWriteBackPrepaymentBal(List<FinPayableCheckEntry> writterEntryList, boolean reverse);
}
