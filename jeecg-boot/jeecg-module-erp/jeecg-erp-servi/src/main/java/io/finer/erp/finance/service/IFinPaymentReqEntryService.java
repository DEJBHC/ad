package io.finer.erp.finance.service;

import io.finer.erp.finance.entity.FinPaymentReqEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 付款申请单明细
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
public interface IFinPaymentReqEntryService extends IService<FinPaymentReqEntry> {

	public List<FinPaymentReqEntry> selectByMainId(String mainId);
}
