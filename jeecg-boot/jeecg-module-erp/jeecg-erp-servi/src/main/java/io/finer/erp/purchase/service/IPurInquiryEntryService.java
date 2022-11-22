package io.finer.erp.purchase.service;

import io.finer.erp.purchase.entity.PurInquiryEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 明细
 * @Author: jeecg-boot
 * @Date:   2022-05-31
 * @Version: V1.0
 */
public interface IPurInquiryEntryService extends IService<PurInquiryEntry> {

	public List<PurInquiryEntry> selectByMainId(String mainId);
}
