package io.finer.erp.base.service.impl;

import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.base.mapper.BasCustomerMapper;
import io.finer.erp.base.service.IBasCustomerService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 客户
 * @Author: jeecg-boot
 * @Date:   2022-09-05
 * @Version: V1.0
 */
@Service
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements IBasCustomerService {

}
