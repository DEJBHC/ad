package io.finer.erp.finance.service;

import io.finer.erp.common.service.IBillService;
import io.finer.erp.finance.entity.FinReceivable;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import io.finer.erp.stock.entity.StkIo;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 应收单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IFinReceivableService extends IBillService<FinReceivable> {

    @Transactional(rollbackFor = Exception.class)
    void createBill(StkIo stkIo) throws Exception;

    BigDecimal getUncheckedAmt(String id);

    @Transactional(rollbackFor = Exception.class)
    void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse);
}
