package io.finer.erp.stock.service;

import io.finer.erp.stock.entity.StkIoSum;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 出入库汇总
 * @Author: jeecg-boot
 * @Date:   2022-09-28
 * @Version: V1.0
 */
public interface IStkIoSumService extends IService<StkIoSum> {

    void sum(Integer year, Integer month);
}
