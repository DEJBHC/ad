package io.finer.erp.purchase.service;

import io.finer.erp.purchase.entity.PurOrderEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2022-06-08
 * @Version: V1.0
 */
public interface IPurOrderEntryService extends IService<PurOrderEntry> {

	public List<PurOrderEntry> selectByMainId(String mainId);
}
