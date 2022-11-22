package io.finer.erp.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.base.entity.BasBillOptions;
import io.finer.erp.base.service.IBasBillOptionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: bas_bill_options
 * @Author: jeecg-boot
 * @Date:   2022-01-23
 * @Version: V1.0
 */
@Api(tags="bas_bill_options")
@RestController
@RequestMapping("/base/basBillOptions")
@Slf4j
public class BasBillOptionsController extends JeecgController<BasBillOptions, IBasBillOptionsService> {
	@Autowired
	private IBasBillOptionsService basBillOptionsService;

	/**
	 * 分页列表查询
	 *
	 * @param basBillOptions
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "bas_bill_options-分页列表查询")
	@ApiOperation(value="bas_bill_options-分页列表查询", notes="bas_bill_options-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BasBillOptions basBillOptions,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BasBillOptions> queryWrapper = QueryGenerator.initQueryWrapper(basBillOptions, req.getParameterMap());
		Page<BasBillOptions> page = new Page<BasBillOptions>(pageNo, pageSize);
		IPage<BasBillOptions> pageList = basBillOptionsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "bas_bill_options-通过id查询")
	 @ApiOperation(value="bas_bill_options-通过id查询", notes="bas_bill_options-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 BasBillOptions basBillOptions = basBillOptionsService.getById(id);
		 if(basBillOptions==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(basBillOptions);
	 }

	 /**
	 *   新增
	 *
	 * @param basBillOptions
	 * @return
	 */
	@AutoLog(value = "bas_bill_options-新增")
	@ApiOperation(value="bas_bill_options-新增", notes="bas_bill_options-新增")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BasBillOptions basBillOptions) {
		basBillOptionsService.save(basBillOptions);
		return Result.OK("新增成功！");
	}

	/**
	 *  编辑
	 *
	 * @param basBillOptions
	 * @return
	 */
	@AutoLog(value = "bas_bill_options-编辑")
	@ApiOperation(value="bas_bill_options-编辑", notes="bas_bill_options-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BasBillOptions basBillOptions) {
		basBillOptionsService.updateById(basBillOptions);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bas_bill_options-通过id删除")
	@ApiOperation(value="bas_bill_options-通过id删除", notes="bas_bill_options-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		basBillOptionsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bas_bill_options-批量删除")
	@ApiOperation(value="bas_bill_options-批量删除", notes="bas_bill_options-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basBillOptionsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param basBillOptions
    */
	@AutoLog(value = "导出为excel")
	@RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BasBillOptions basBillOptions) {
        return super.exportXls(request, basBillOptions, BasBillOptions.class, "bas_bill_options");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@AutoLog(value = "通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BasBillOptions.class);
    }

}
