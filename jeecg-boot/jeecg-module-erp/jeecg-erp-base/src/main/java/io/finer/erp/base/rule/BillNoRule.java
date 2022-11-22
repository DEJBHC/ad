package io.finer.erp.base.rule;

import com.alibaba.fastjson.JSONObject;
import io.finer.erp.base.service.IBasSequenceService;
import org.jeecg.common.handler.IFillRuleHandler;
import org.jeecg.common.util.SpringContextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BillNoRule implements IFillRuleHandler {
    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        String prefix = "", period = "";
        Integer seqLen = 0;
        if (params != null) {
            Object obj = params.get("prefix");
            if (obj != null) prefix = obj.toString();

            obj = params.get("period");
            if (obj != null) period = obj.toString();

            obj = params.get("seqLen");
            if (obj != null) seqLen = Integer.parseInt(obj.toString());
        }

        SimpleDateFormat format = new SimpleDateFormat(period);
        String k = prefix + format.format(new Date());
        if (k == null || k.length() == 0) {
            return null;
        }

        IBasSequenceService svc = (IBasSequenceService) SpringContextUtils.getBean("basSequenceServiceImpl");
        return k + String.format("%0" + seqLen + "d", svc.nextSequence(k));
    }
}
