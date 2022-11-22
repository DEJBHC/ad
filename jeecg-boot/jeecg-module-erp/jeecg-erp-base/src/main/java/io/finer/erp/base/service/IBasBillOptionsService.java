package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasBillOptions;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: bas_bill_options
 * @Author: jeecg-boot
 * @Date:   2022-01-23
 * @Version: V1.0
 */
public interface IBasBillOptionsService extends IService<BasBillOptions> {
    BasBillOptions getByBillType(String billType);
}
