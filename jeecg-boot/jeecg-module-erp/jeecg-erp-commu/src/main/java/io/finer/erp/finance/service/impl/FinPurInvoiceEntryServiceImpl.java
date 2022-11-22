package io.finer.erp.finance.service.impl;

import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.finance.mapper.FinPurInvoiceEntryMapper;
import io.finer.erp.finance.service.IFinPurInvoiceEntryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 明细
 * @Author:
 * @Date:   2020-05-21
 * @Version: V1.0
 */
@Service
public class FinPurInvoiceEntryServiceImpl
		extends ServiceImpl<FinPurInvoiceEntryMapper, FinPurInvoiceEntry> implements IFinPurInvoiceEntryService {

	@Autowired
	private FinPurInvoiceEntryMapper finPurInvoiceEntryMapper;

	@Override
	public List<FinPurInvoiceEntry> selectByMainId(String mainId) {
		List<FinPurInvoiceEntry> list = finPurInvoiceEntryMapper.selectByMainId(mainId);
		//20221018 del
//		for(FinPurInvoiceEntry entry : list) {
//			//蓝单source为stk_io, 红单的source为fin_pur_invoice
//			String srcBillId = entry.getSrcEntryId();
//			String srcBillType = entry.getSrcBillType();
//			if (!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("FinPurInvoice")) {//红单
//				FinPurInvoiceEntry source = getById(srcBillId);
//				if (source != null) {
//					entry.setSrcBlueQty(source.getQty());
//					entry.setSrcBlueAmt(source.getAmt());
//				}
//			}
//		}
		return list;
	}

}
