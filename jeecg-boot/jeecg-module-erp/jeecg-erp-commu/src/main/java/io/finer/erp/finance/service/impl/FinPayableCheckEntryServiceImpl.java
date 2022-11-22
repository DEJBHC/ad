package io.finer.erp.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.mapper.FinPayableCheckEntryMapper;
import io.finer.erp.finance.service.IFinPayableCheckEntryService;
import io.finer.erp.finance.service.IFinPayableService;
import io.finer.erp.finance.service.IFinPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: fin_rp_check_entry
 * @Author: jeecg-boot
 * @Date:   2020-04-17
 * @Version: V1.0
 */
@Service
public class FinPayableCheckEntryServiceImpl extends ServiceImpl<FinPayableCheckEntryMapper, FinPayableCheckEntry> implements IFinPayableCheckEntryService {

    @Autowired
    private FinPayableCheckEntryMapper finPayableCheckEntryMapper;
    @Autowired
    private IFinPayableService finPayableService;
    @Autowired
    private IFinPaymentService finPaymentService;


    @Override
    public List<FinPayableCheckEntry> selectByMainId(String mainId) {
        List<FinPayableCheckEntry> finPayableCheckEntryList = finPayableCheckEntryMapper.selectByMainId(mainId);
        for(FinPayableCheckEntry entry:finPayableCheckEntryList) {
            entry.setUncheckedAmt(entry.getSrcBillType().startsWith("FinPayable") ?
                    finPayableService.getUncheckedAmt(entry.getSrcBillId()) :
                    finPaymentService.getUncheckedAmt(entry.getSrcBillId()));
        }
        return finPayableCheckEntryList;
    }
}
