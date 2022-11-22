package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: (红字)收款申请单
 * @Author: jeecg-boot
 * @Date:   2022-09-07
 * @Version: V1.0
 */
public interface IFinReceiptReqService extends IBillWithEntryService<FinReceiptReq, FinReceiptReqEntry> {

	@Transactional(rollbackFor = Exception.class)
	void receiptWriteBack(FinReceipt receipt, List<FinReceiptEntry> receiptEntryList, boolean reverse) throws Exception;
}
