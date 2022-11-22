package io.finer.erp.biz.service.impl;

import io.finer.erp.base.entity.BasBizPeriod;
import io.finer.erp.base.service.IBasBizPeriodService;
import io.finer.erp.biz.mapper.CloseAccountsMapper;
import io.finer.erp.biz.service.ICloseAccountsService;
import io.finer.erp.finance.service.IFinPayableSumService;
import io.finer.erp.finance.service.IFinReceivableSumService;
import io.finer.erp.stock.service.IStkIoSumService;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 月度结账
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class CloseAccountsServiceImpl implements ICloseAccountsService {
    @Autowired
    private CloseAccountsMapper closeAccountsMapper;
    @Autowired
    private IBasBizPeriodService basBizPeriodService;
    @Autowired
    private IFinReceivableSumService finReceivableSumService;
    @Autowired
    private IFinPayableSumService finPayableSumService;
    @Autowired
    private IStkIoSumService stkIoSumService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasBizPeriod closeAccounts(Integer year, Integer month) {
        BasBizPeriod curr = basBizPeriodService.currentPeriod();
        if (year.compareTo(curr.getYear()) != 0 || month.compareTo(curr.getMonth()) != 0) {
            throw new JeecgBootException( String.format("【%d年%d月】该月度不是当前月度，不能做结账！", year, month));
        }
        if (year.compareTo(curr.getBeginYear()) < 0 ||
                (year.compareTo(curr.getBeginYear()) == 0 && month.compareTo(curr.getBeginMonth()) < 0)) {
            throw new JeecgBootException(String.format("【%d年%d月】该月度早于起用月度，不能做结账！", year, month));
        }

        //单据与结账：
        // ·应收单：应收单参与应收月结
        // ·收款单/预收单：参与应收月结
        // ·应付单：参与应付月结
        // ·付款单/预付单：参与应付月结
        // ·出入库单：参与出入库单月结
        // ·库存盘点单：生效后会生成同单据日期的出入库单，从而影响出入库单的月结
        // ·其他单据：不参与月结，但为了阶段性了结工作，要求已生效
        StringBuilder s = new StringBuilder(String.format("【%d年%d月】该月度的未作废单据有未生效的，不能做结账！\n", year, month));
        List<Map<String, Object>> list = closeAccountsMapper.selectNotEffectiveBill(year, month);
        if (list != null && list.size() > 0) {
            for(Map<String, Object> rec: list) {
                s.append(rec.get("name")).append("：").append(rec.get("bill_nos")).append("；\n");
            }
            throw new JeecgBootException(s.substring(0, s.length()-2));
        }

        finReceivableSumService.sum(year, month);
        finPayableSumService.sum(year, month);
        stkIoSumService.sum(year, month);
        return basBizPeriodService.forwardPeriod(year, month);
    }

//    /**
//     * 是否可以结账（暂留）
//     */
//    private <T> Boolean canCloseAccount(Integer year, Integer month, BaseMapper<T> mapper) {
//        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
//        queryWrapper.apply("YEAR(bill_date) = {0} AND MONTH(bill_date) = {1}" +
//                " AND is_effective = 0" +
//                " AND is_voided = 0", year, month);
//        return mapper.selectCount(queryWrapper) == 0;
//    }

 }
