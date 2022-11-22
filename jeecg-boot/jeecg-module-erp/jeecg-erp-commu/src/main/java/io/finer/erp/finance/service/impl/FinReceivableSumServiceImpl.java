package io.finer.erp.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.finance.entity.FinReceivableSum;
import io.finer.erp.finance.mapper.FinReceivableSumMapper;
import io.finer.erp.finance.service.IFinReceivableSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: fin_receivable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Service
public class FinReceivableSumServiceImpl
        extends ServiceImpl<FinReceivableSumMapper, FinReceivableSum> implements IFinReceivableSumService {

    @Autowired
    FinReceivableSumMapper finReceivableSumMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sum(Integer year, Integer month) {
        finReceivableSumMapper.sum(year, month);
    }
}
