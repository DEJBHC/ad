package io.finer.erp.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.finance.entity.FinReceiptReqEntry;
import io.finer.erp.finance.mapper.FinReceiptReqEntryMapper;
import io.finer.erp.finance.service.IFinReceiptReqEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: (红字)收款申请单明细
 * @Author: jeecg-boot
 * @Date:   2022-09-07
 * @Version: V1.0
 */
@Service
public class FinReceiptReqEntryServiceImpl extends ServiceImpl<FinReceiptReqEntryMapper, FinReceiptReqEntry> implements IFinReceiptReqEntryService {

	@Autowired
	private FinReceiptReqEntryMapper finReceiptReqEntryMapper;

	@Override
	public List<FinReceiptReqEntry> selectByMainId(String mainId) {
		return finReceiptReqEntryMapper.selectByMainId(mainId);
	}
}
