package io.finer.erp.finance.service.impl;

import io.finer.erp.finance.entity.FinPaymentReqEntry;
import io.finer.erp.finance.mapper.FinPaymentReqEntryMapper;
import io.finer.erp.finance.service.IFinPaymentReqEntryService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 付款申请单明细
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
@Service
public class FinPaymentReqEntryServiceImpl extends ServiceImpl<FinPaymentReqEntryMapper, FinPaymentReqEntry> implements IFinPaymentReqEntryService {
	
	@Autowired
	private FinPaymentReqEntryMapper finPaymentReqEntryMapper;
	
	@Override
	public List<FinPaymentReqEntry> selectByMainId(String mainId) {
		return finPaymentReqEntryMapper.selectByMainId(mainId);
	}
}
