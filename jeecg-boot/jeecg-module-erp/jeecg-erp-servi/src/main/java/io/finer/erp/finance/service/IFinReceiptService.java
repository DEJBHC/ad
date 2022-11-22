package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinReceipt;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
public interface IFinReceiptService extends IBillWithEntryService<FinReceipt, FinReceiptEntry> {

	BigDecimal getUncheckedAmt(String id);

	@Transactional(rollbackFor = Exception.class)
	void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse);
}
