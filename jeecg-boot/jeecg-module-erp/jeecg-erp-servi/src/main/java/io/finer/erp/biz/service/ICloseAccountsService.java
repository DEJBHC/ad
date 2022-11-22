package io.finer.erp.biz.service;

import io.finer.erp.base.entity.BasBizPeriod;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 月度结账
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface ICloseAccountsService {
    /**
     * 月度结账
     * @param year
     * @param month
     */
    @Transactional(rollbackFor = Exception.class)
    BasBizPeriod closeAccounts(Integer year, Integer month);
}
