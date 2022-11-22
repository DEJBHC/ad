package io.finer.erp.purchase.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.purchase.entity.PurOrderEntry;
import io.finer.erp.purchase.entity.PurReq;
import io.finer.erp.purchase.entity.PurReqEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 采购申请
 * @Author: jeecg-boot
 * @Date:   2022-05-30
 * @Version: V1.0
 */
public interface IPurReqService extends IBillWithEntryService<PurReq, PurReqEntry> {
    /**
     * 后置回写更新——已订购数量
     * @param purOrderEntryList
     * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
     */
    @Transactional(rollbackFor = Exception.class)
    void purOrderWriteBack(List<PurOrderEntry> purOrderEntryList, boolean reverse);
}
