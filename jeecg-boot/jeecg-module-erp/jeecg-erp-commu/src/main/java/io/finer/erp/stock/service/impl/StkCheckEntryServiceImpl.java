package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.finer.erp.stock.mapper.StkCheckEntryMapper;
import io.finer.erp.stock.service.IStkCheckEntryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 明细
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class StkCheckEntryServiceImpl
        extends ServiceImpl<StkCheckEntryMapper, StkCheckEntry>
        implements IStkCheckEntryService {

	@Override
	public List<StkCheckEntry> selectByMainId(String mainId) {
		return this.baseMapper.selectByMainId(mainId);
	}
}
