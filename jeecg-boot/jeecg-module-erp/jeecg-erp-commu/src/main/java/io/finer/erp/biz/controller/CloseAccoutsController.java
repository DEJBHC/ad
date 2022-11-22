package io.finer.erp.biz.controller;

import com.alibaba.fastjson.JSONObject;
import io.finer.erp.base.entity.BasBizPeriod;
import io.finer.erp.base.service.IBasBizPeriodService;
import io.finer.erp.biz.service.ICloseAccountsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 月度结账
 * @Author:
 * @Date:
 * @Version: V1.0
*/
@Api(tags="月度结账")
@RestController
@RequestMapping("/biz/closeAccounts")
@Slf4j
public class CloseAccoutsController extends JeecgController<BasBizPeriod, IBasBizPeriodService> {
   @Autowired
   private ICloseAccountsService closeAccountsService;

   @AutoLog(value = "月度结账")
   @ApiOperation(value="月度结账", notes="月度结账")
   @PutMapping(value = "/month")
   public Result<?> closeAccount(@RequestBody JSONObject json) {
       BasBizPeriod basBizPeriod = closeAccountsService.closeAccounts(json.getInteger("year"), json.getInteger("month"));
       return Result.ok(basBizPeriod);
   }
}
