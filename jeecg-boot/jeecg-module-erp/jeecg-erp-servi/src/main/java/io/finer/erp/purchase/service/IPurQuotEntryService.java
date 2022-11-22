package io.finer.erp.purchase.service;

import io.finer.erp.purchase.entity.PurQuotEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2022-06-03
 * @Version: V1.0
 */
public interface IPurQuotEntryService extends IService<PurQuotEntry> {

	public List<PurQuotEntry> selectByMainId(String mainId);
}
