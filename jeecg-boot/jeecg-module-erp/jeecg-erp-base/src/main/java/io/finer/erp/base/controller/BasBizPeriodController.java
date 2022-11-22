package io.finer.erp.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.base.entity.BasBizPeriod;
import io.finer.erp.base.service.IBasBizPeriodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 /**
 * @Description: bas_biz_period
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
@Api(tags="bas_biz_period")
@RestController
@RequestMapping("/base/basBizPeriod")
@Slf4j
public class BasBizPeriodController extends JeecgController<BasBizPeriod, IBasBizPeriodService> {
	@Autowired
	private IBasBizPeriodService basBizPeriodService;

	 /**
	  * 查询
	  *
	  * @param
	  * @return
	  */
	 //@AutoLog(value = "bas_biz_period-查询")
	 @ApiOperation(value="bas_biz_period-查询", notes="bas_biz_period-查询")
	 @GetMapping(value = "/query")
	 public Result<?> query() {
		 BasBizPeriod basBizPeriod = basBizPeriodService.getOne(Wrappers.emptyWrapper());
		 if(basBizPeriod ==null) {
			 return Result.error("未找到数据");
		 }
		 return Result.ok(basBizPeriod);
	 }

	/**
	 *  编辑
	 *
	 * @param basBizPeriod
	 * @return
	 */
	@AutoLog(value = "bas_biz_period-编辑")
	@ApiOperation(value="bas_biz_period-编辑", notes="bas_biz_period-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BasBizPeriod basBizPeriod) {
		basBizPeriodService.updateById(basBizPeriod);
		return Result.ok("编辑成功!");
	}

	 //@AutoLog(value = "当前月度")
	 @ApiOperation(value="当前月度", notes="当前月度")
	 @GetMapping(value = "/currentPeriod")
	 public Result<?> currentPeriod() {
		 BasBizPeriod basBizPeriod = basBizPeriodService.currentPeriod();
		 return Result.ok(basBizPeriod);
	 }

	 @AutoLog(value = "当前月度回退")
	 @ApiOperation(value="当前月度回退", notes="当前月度回退")
	 @PutMapping(value = "/backPeriod")
	 public Result<?> backPeriod(@RequestBody JSONObject json) {
		 BasBizPeriod basBizPeriod =  basBizPeriodService.backPeriod(json.getInteger("year"), json.getInteger("month"));
		 return Result.ok(basBizPeriod);
	 }
 }
