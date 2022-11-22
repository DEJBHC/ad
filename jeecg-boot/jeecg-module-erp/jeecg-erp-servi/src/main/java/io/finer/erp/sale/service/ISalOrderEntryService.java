package io.finer.erp.sale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.sale.entity.SalOrderEntry;

import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:
 * @Version: V1.0
 */
public interface ISalOrderEntryService extends IService<SalOrderEntry> {

	public List<SalOrderEntry> selectByMainId(String mainId);
}
