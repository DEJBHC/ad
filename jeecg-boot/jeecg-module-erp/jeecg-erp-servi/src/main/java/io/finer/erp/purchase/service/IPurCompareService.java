package io.finer.erp.purchase.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.purchase.entity.PurCompare;
import io.finer.erp.purchase.entity.PurCompareEntry;
import io.finer.erp.purchase.entity.PurOrderEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: pur_quot
 * @Author: jeecg-boot
 * @Date:   2022-06-03
 * @Version: V1.0
 */
public interface IPurCompareService extends IBillWithEntryService<PurCompare, PurCompareEntry> {
    /**
     * 后置回写更新——已订购数量：本单据无“已订购数量”，本方法只是作为回写链的一环，向前转发回写！
     * @param purOrderEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void purOrderWriteBack(List<PurOrderEntry> purOrderEntryList, boolean reverse);
}
