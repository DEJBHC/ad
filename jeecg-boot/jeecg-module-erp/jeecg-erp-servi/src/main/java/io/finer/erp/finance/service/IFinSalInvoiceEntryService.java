package io.finer.erp.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;

import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2020-05-21
 * @Version: V1.0
 */
public interface IFinSalInvoiceEntryService extends IService<FinSalInvoiceEntry> {

	public List<FinSalInvoiceEntry> selectByMainId(String mainId);
}
