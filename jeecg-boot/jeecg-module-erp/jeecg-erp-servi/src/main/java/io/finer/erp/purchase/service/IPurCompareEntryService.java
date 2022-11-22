package io.finer.erp.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.purchase.entity.PurCompareEntry;

import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2022-06-03
 * @Version: V1.0
 */
public interface IPurCompareEntryService extends IService<PurCompareEntry> {

	public List<PurCompareEntry> selectByMainId(String mainId);
}
