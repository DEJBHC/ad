package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.stock.entity.StkIoSum;
import io.finer.erp.stock.mapper.StkIoSumMapper;
import io.finer.erp.stock.service.IStkIoSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 出入库汇总
 * @Author: jeecg-boot
 * @Date:   2022-09-28
 * @Version: V1.0
 */
@Service
public class StkIoSumServiceImpl extends ServiceImpl<StkIoSumMapper, StkIoSum> implements IStkIoSumService {
    @Autowired
    StkIoSumMapper stkIoSumMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sum(Integer year, Integer month) {
        stkIoSumMapper.sum(year, month);
    }

}
