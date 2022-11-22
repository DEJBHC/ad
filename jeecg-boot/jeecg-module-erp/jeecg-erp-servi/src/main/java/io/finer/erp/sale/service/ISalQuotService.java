package io.finer.erp.sale.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.sale.entity.SalQuot;
import io.finer.erp.sale.entity.SalQuotEntry;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: sal_quot
 * @Author: jeecg-boot
 * @Date:
 * @Version: V1.0
 */
public interface ISalQuotService extends IBillWithEntryService<SalQuot, SalQuotEntry> {
    /**
     * 设定客户
     */
    @Transactional(rollbackFor = Exception.class)
    void setCustomer(String id, String customerId);

}
