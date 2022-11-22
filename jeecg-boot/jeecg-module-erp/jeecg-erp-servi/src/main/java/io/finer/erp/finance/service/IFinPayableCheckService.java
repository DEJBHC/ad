package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 往来核销单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IFinPayableCheckService extends IBillWithEntryService<FinPayableCheck, FinPayableCheckEntry> {
    @Transactional(rollbackFor = Exception.class)
    void createBill(FinPayment payment, List<FinPaymentEntry> paymentEntryList) throws Exception;
}
