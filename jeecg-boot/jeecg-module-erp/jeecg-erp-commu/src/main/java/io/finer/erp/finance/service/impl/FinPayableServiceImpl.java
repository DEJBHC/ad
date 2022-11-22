package io.finer.erp.finance.service.impl;

import io.finer.erp.common.service.impl.BillServiceImpl;
import io.finer.erp.finance.entity.FinPayable;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.mapper.FinPayableMapper;
import io.finer.erp.finance.service.IFinPayableCheckService;
import io.finer.erp.finance.service.IFinPayableService;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: 应付单
 * @Author:
 * @Date:
 * @Version:
 */
@Service
public class FinPayableServiceImpl
        extends BillServiceImpl<FinPayableMapper, FinPayable> implements IFinPayableService {

    @Autowired
    private IStkIoService stkIoService;
    @Lazy
    @Autowired
    private IFinPayableCheckService finPayableCheckService;

    @Override
    protected void writeBack(FinPayable bill, boolean reverse) {
        //无回写
    }

    @Override
    protected boolean hasFinishedExecute(FinPayable bill) {
        int i = bill.getCheckedAmt().compareTo(bill.getAmt());
        boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;
        return super.hasFinishedExecute(bill) && b;
    }

    @Override
    protected void voidBillPreprocess(FinPayable bill) {
        //后置单据-应付核销单
        String billNos = finPayableCheckService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
        if (!StringUtils.isEmpty(billNos)) {
            throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBill(StkIo stkIo) throws Exception {
        if (stkIo.getHasRp() == 0) {
            return;
        }

        FinPayable bill = new FinPayable();
        bill.setPayableType("201");
        bill.setIsRubric(stkIo.getIsRubric());
        bill.setIsAuto(1);
        bill.setSrcBillType(stkIo.getBillType());
        bill.setSrcBillId(stkIo.getId());
        bill.setSrcNo(stkIo.getBillNo());
        String ruleCode = "fin_cgap_bill_no";
        bill.setBillNo((String) FillRuleUtil.executeRule(ruleCode, null));
        bill.setBillDate(stkIo.getBillDate()); //注意：如果设为系统日期，有可能小于当前业务期间
        bill.setAmt(stkIo.getSettleAmt());
        bill.setCheckedAmt(new BigDecimal("0.00"));
        bill.setSupplierId(stkIo.getSupplierId());
        bill.setOpDept(stkIo.getOpDept());
        bill.setOperator(stkIo.getOperator());
        bill.setIsVoided(0);
        this.submitAdd(bill);
    }

    @Override
    public BigDecimal getUncheckedAmt(String id) {
        FinPayable bill = this.baseMapper.selectById(id);
        return bill != null ? bill.getUncheckedAmt() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse) {
        Map<String, FinPayable> billMap = new HashMap<>();
        List<FinPayableCheckEntry> checkEntryList1 = new ArrayList<>();
        for (FinPayableCheckEntry writterEntry : checkEntryList) {
            String srcBillType = writterEntry.getSrcBillType();
            BigDecimal writterEntryAmt = writterEntry.getAmt();
            if (StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("FinPayable")
                    || writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
                continue;
            }

            //前置条件、预处理
            FinPayable bill = writtenBackPreprocess(writterEntry, billMap);

            //数据处理
            if (reverse) {
                writterEntryAmt = writterEntryAmt.negate();
            }
            BigDecimal checkedAmt = bill.getCheckedAmt().add(writterEntryAmt);
            int i = checkedAmt.compareTo(bill.getAmt());
            if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
                throw new JeecgBootException(bill.getBillNo() + "：核销金额不能超出未核金额！");
            }
            bill.setCheckedAmt(checkedAmt);

            //向前转置
            writtenBackForward(bill, writterEntry, checkEntryList1, FinPayableCheckEntry.class);
        }

        //后置状态
        for (FinPayable bill : billMap.values()) {
            this.baseMapper.updateById(bill);
            this.refreshExecuteStage(bill);
        }

        //向前回调
        stkIoService.payableCheckWriteBack(checkEntryList1, reverse);
    }
}
