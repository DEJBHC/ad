package io.finer.erp.purchase.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.entity.FinPaymentEntry;
import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.purchase.entity.PurOrder;
import io.finer.erp.purchase.entity.PurOrderEntry;
import io.finer.erp.stock.entity.StkIoEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 采购订单
 * @Author: jeecg-boot
 * @Date:   2022-06-08
 * @Version: V1.0
 */
public interface IPurOrderService extends IBillWithEntryService<PurOrder, PurOrderEntry> {

    /**
     * 后置回写更新——结算数量、结算金额
     * @param stkIoEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void stkIoWriteBack(List<StkIoEntry> stkIoEntryList, boolean reverse);

    /**
     * 后置回写更新——预付余额
     * @param paymentEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void paymentWriteBackPrepaymentBal(List<FinPaymentEntry> paymentEntryList, boolean reverse);

    /**
     * 后置回写更新——预付余额：FinPaymentServiceImpl.payableCheckWriteBack()中预付款时调用
     * @param checkEntryList： 应付核销中的预付款
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void payableCheckWriteBackPrepaymentBal(List<FinPayableCheckEntry> checkEntryList, boolean reverse);

    /**
     * 后置回写更新——已结算金额：StkIoServiceImpl.payableCheckWriteBack()中调用
     * @param checkEntryList：
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void payableCheckWriteBackSettledAmt(List<FinPayableCheckEntry> checkEntryList, boolean reverse);

    /**
     * 后置回写更新——已开票数量、已开票金额：不作为执行进度的控制项
     * @param invoiceEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void purInvoiceWriteBack(List<FinPurInvoiceEntry> invoiceEntryList, boolean reverse);
}
