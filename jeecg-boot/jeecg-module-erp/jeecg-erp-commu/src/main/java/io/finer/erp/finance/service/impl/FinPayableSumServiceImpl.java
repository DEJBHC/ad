package io.finer.erp.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.finance.entity.FinPayableSum;
import io.finer.erp.finance.mapper.FinPayableSumMapper;
import io.finer.erp.finance.service.IFinPayableSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: fin_payable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Service
public class FinPayableSumServiceImpl extends ServiceImpl<FinPayableSumMapper, FinPayableSum> implements IFinPayableSumService {
    @Autowired
    FinPayableSumMapper finPayableSumMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sum(Integer year, Integer month) {
        finPayableSumMapper.sum(year, month);
    }
}
