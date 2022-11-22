package io.finer.erp.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.finance.entity.FinReceivableSum;

/**
 * @Description: fin_receivable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface IFinReceivableSumService extends IService<FinReceivableSum> {
    void sum(Integer year, Integer month);
}
