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
public interface IFinReceivableCheckService extends IBillWithEntryService<FinReceivableCheck, FinReceivableCheckEntry> {
    @Transactional(rollbackFor = Exception.class)
    void createBill(FinReceipt receipt, List<FinReceiptEntry> receiptEntryList) throws Exception;

}
