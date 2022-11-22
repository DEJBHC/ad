package io.finer.erp.sale.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.finer.erp.sale.entity.SalOrder;
import io.finer.erp.sale.entity.SalOrderEntry;
import io.finer.erp.stock.entity.StkIoEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 销售订单
 * @Author: jeecg-boot
 * @Date:
 * @Version: V1.0
 */
public interface ISalOrderService extends IBillWithEntryService<SalOrder, SalOrderEntry> {

    /**
     * 后置回写更新——结算数量、结算金额
     * @param stkIoEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void stkIoWriteBack(List<StkIoEntry> stkIoEntryList, boolean reverse);

    /**
     * 后置回写更新——预收余额
     * @param receiptEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void receiptWriteBackPrereceiptBal(List<FinReceiptEntry> receiptEntryList, boolean reverse);

    /**
     * 后置回写更新——预收余额：FinReceiptServiceImpl.receivableCheckWriteBack()中预收款时调用
     * @param checkEntryList： 应收核销中的预收款
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void receivableCheckWriteBackPrereceiptBal(List<FinReceivableCheckEntry> checkEntryList, boolean reverse);

    /**
     * 后置回写更新——已结算金额：StkIoServiceImpl.receivableCheckWriteBack()中调用
     * @param checkEntryList：
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void receivableCheckWriteBackSettledAmt(List<FinReceivableCheckEntry> checkEntryList, boolean reverse);

    /**
     * 后置回写更新——已开票数量、已开票金额：不作为执行进度的控制项
     * @param invoiceEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void salInvoiceWriteBack(List<FinSalInvoiceEntry> invoiceEntryList, boolean reverse);
}
