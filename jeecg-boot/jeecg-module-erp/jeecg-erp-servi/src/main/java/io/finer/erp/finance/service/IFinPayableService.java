package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillService;
import io.finer.erp.finance.entity.FinPayable;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.stock.entity.StkIo;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 应付单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IFinPayableService extends IBillService<FinPayable> {
    @Transactional(rollbackFor = Exception.class)
    void createBill(StkIo stkIo) throws Exception;

    BigDecimal getUncheckedAmt(String id);

    @Transactional(rollbackFor = Exception.class)
    void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse);
}
