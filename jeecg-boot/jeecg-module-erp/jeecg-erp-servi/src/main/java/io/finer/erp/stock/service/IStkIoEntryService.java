package io.finer.erp.stock.service;

import io.finer.erp.stock.entity.StkIoEntry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IStkIoEntryService extends IService<StkIoEntry> {
	List<StkIoEntry> selectByMainId(String mainId);
	List<StkIoEntry> selectEditingByMainId(String mainId);
}
