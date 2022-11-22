package io.finer.erp.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.base.entity.BasBizPeriod;
import io.finer.erp.base.mapper.BasBizPeriodMapper;
import io.finer.erp.base.service.IBasBizPeriodService;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * @Description: bas_biz_period
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Service
public class BasBizPeriodServiceImpl extends ServiceImpl<BasBizPeriodMapper, BasBizPeriod> implements IBasBizPeriodService {
    @Override
    public BasBizPeriod currentPeriod() {
        BasBizPeriod bizPeriod = baseMapper.selectOne(null);
        if (bizPeriod == null) {
            throw new JeecgBootException("业务期间记录不存在！");
        }
        return bizPeriod;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasBizPeriod backPeriod(Integer year, Integer month) {
        BasBizPeriod curr = checkCurrentPeriod(year, month); //检查参数是否等于当前月份，以避免多人回退！
        if (year.compareTo(curr.getBeginYear()) < 0 ||
                (year.compareTo(curr.getBeginYear()) == 0 && month.compareTo(curr.getBeginMonth()) <= 0)) {
            throw new JeecgBootException(String.format("【%d年%d月】小于等于起用月度，不能回退！", year, month));
        }

        year = month == 1 ? year - 1 : year;
        month = month == 1 ? 12 : month - 1;
        curr.setYear(year);
        curr.setMonth(month);
        baseMapper.updateById(curr);
        return curr;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasBizPeriod forwardPeriod(Integer year, Integer month) {
        BasBizPeriod curr = checkCurrentPeriod(year, month); //检查参数是否等于当前月份，以避免多人前进！
        year = month == 12 ? year + 1 : year;
        month = month == 12 ? 1 : month + 1;

        curr.setYear(year);
        curr.setMonth(month);
        baseMapper.updateById(curr);
        return curr;
    }

    @Override
    public boolean closedAccounts(Date date) {
        return date.compareTo(currentPeriod().getFirstDay()) < 0;
    }

    private BasBizPeriod checkCurrentPeriod(Integer year, Integer month) {
        BasBizPeriod curr = currentPeriod();
        String s = String.format("【%d年%d月】", year, month);
        if (year.compareTo(curr.getYear()) != 0 || month.compareTo(curr.getMonth()) != 0) {
            throw new JeecgBootException( s + "不是当前月度，不能执行该操作！");
        }
        return curr;
    }

}
