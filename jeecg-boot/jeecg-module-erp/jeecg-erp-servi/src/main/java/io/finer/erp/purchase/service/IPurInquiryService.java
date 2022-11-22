package io.finer.erp.purchase.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.purchase.entity.PurInquiry;
import io.finer.erp.purchase.entity.PurInquiryEntry;
import io.finer.erp.purchase.entity.PurOrderEntry;
import io.finer.erp.purchase.entity.PurQuot;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 采购询价单
 * @Author: jeecg-boot
 * @Date:   2022-05-31
 * @Version: V1.0
 */
public interface IPurInquiryService extends IBillWithEntryService<PurInquiry, PurInquiryEntry> {

    /**
     * 后置回写更新——已生效报价单数
     * @param purQuot
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void purQuotWriteBack(PurQuot purQuot, boolean reverse);

    /**
     * 后置回写更新——已订购数量：本单据无“已订购数量”，本方法只是作为回写链的一环，向前转发回写！
     * @param purOrderEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void purOrderWriteBack(List<PurOrderEntry> purOrderEntryList, boolean reverse);
}
