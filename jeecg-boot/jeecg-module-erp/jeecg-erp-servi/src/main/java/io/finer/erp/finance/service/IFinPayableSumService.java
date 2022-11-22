package io.finer.erp.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.finance.entity.FinPayableSum;

/**
 * @Description: fin_payable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface IFinPayableSumService extends IService<FinPayableSum> {
    void sum(Integer year, Integer month);
}
