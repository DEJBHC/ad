package io.finer.erp.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.finer.erp.base.entity.BasBillOptions;
import io.finer.erp.base.mapper.BasBillOptionsMapper;
import io.finer.erp.base.service.IBasBillOptionsService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: bas_bill_options
 * @Author: jeecg-boot
 * @Date:   2022-01-23
 * @Version: V1.0
 */
@Service
public class BasBillOptionsServiceImpl extends ServiceImpl<BasBillOptionsMapper, BasBillOptions> implements IBasBillOptionsService {

    @Override
    public BasBillOptions getByBillType(String billType) {
        LambdaQueryWrapper<BasBillOptions> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BasBillOptions::getBillType, billType);
        return super.getOne(queryWrapper);
    }
}
