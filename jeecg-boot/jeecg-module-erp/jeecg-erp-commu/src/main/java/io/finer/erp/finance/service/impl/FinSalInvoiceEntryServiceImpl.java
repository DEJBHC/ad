package io.finer.erp.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.finer.erp.finance.mapper.FinSalInvoiceEntryMapper;
import io.finer.erp.finance.service.IFinSalInvoiceEntryService;
import io.finer.erp.stock.service.IStkIoEntryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 明细
 * @Author:t
 * @Date:   2020-05-24
 * @Version: V1.0
 */
@Service
public class FinSalInvoiceEntryServiceImpl
		extends ServiceImpl<FinSalInvoiceEntryMapper, FinSalInvoiceEntry> implements IFinSalInvoiceEntryService {

	@Autowired
	private FinSalInvoiceEntryMapper finSalInvoiceEntryMapper;

	@Override
	public List<FinSalInvoiceEntry> selectByMainId(String mainId) {
		List<FinSalInvoiceEntry> list = finSalInvoiceEntryMapper.selectByMainId(mainId);
		//20221018 del
//		for(FinSalInvoiceEntry entry : list) {
//			//蓝单source为stk_io, 红单的source为fin_sal_invoice
//			String srcBillId = entry.getSrcEntryId();
//			String srcBillType = entry.getSrcBillType();
//			if (!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("FinSalInvoice")) {//红单
//				FinSalInvoiceEntry source = getById(srcBillId);
//				if (source != null) {
//					entry.setSrcBlueQty(source.getQty());
//					entry.setSrcBlueAmt(source.getAmt());
//				}
//			}
//		}
		return list;
	}

}
