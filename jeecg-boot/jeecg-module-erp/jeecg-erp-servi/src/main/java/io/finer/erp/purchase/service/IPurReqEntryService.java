package io.finer.erp.purchase.service;

import io.finer.erp.purchase.entity.PurReqEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2022-05-30
 * @Version: V1.0
 */
public interface IPurReqEntryService extends IService<PurReqEntry> {

	public List<PurReqEntry> selectByMainId(String mainId);
}
