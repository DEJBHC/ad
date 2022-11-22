package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasBizPeriod;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: bas_biz_period
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface IBasBizPeriodService extends IService<BasBizPeriod> {
    BasBizPeriod currentPeriod();

    @Transactional(rollbackFor = Exception.class)
    BasBizPeriod backPeriod(Integer year, Integer month);

    @Transactional(rollbackFor = Exception.class)
    BasBizPeriod forwardPeriod(Integer year, Integer month);

    /**
     * 日期所在月是否已结账
     * @param date
     * @return
     */
    boolean closedAccounts(Date date);
}
