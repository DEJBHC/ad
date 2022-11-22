package io.finer.erp.stock.service;

import io.finer.erp.stock.entity.StkCheckEntry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2020-05-18
 * @Version: V1.0
 */
public interface IStkCheckEntryService extends IService<StkCheckEntry> {

	public List<StkCheckEntry> selectByMainId(String mainId);
}
