package io.finer.erp.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.finance.entity.FinReceiptReqEntry;

import java.util.List;

/**
 * @Description: (红字)收款申请单明细
 * @Author: jeecg-boot
 * @Date:   2022-09-07
 * @Version: V1.0
 */
public interface IFinReceiptReqEntryService extends IService<FinReceiptReqEntry> {

	public List<FinReceiptReqEntry> selectByMainId(String mainId);
}
