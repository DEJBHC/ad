package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.entity.FinPayment;
import io.finer.erp.finance.entity.FinPaymentEntry;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 付款单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IFinPaymentService extends IBillWithEntryService<FinPayment, FinPaymentEntry> {

	BigDecimal getUncheckedAmt(String id);

	@Transactional(rollbackFor = Exception.class)
	void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse);
}
