package io.finer.erp.base.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.base.service.IBasCustomerService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 客户
 * @Author: jeecg-boot
 * @Date:   2022-09-05
 * @Version: V1.0
 */
@Api(tags="客户")
@RestController
@RequestMapping("/base/basCustomer")
@Slf4j
public class BasCustomerController extends JeecgController<BasCustomer, IBasCustomerService> {
	@Autowired
	private IBasCustomerService basCustomerService;

	/**
	 * 分页列表查询
	 *
	 * @param basCustomer
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "客户-分页列表查询")
	@ApiOperation(value="客户-分页列表查询", notes="客户-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BasCustomer>> queryPageList(BasCustomer basCustomer,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BasCustomer> queryWrapper = QueryGenerator.initQueryWrapper(basCustomer, req.getParameterMap());
		Page<BasCustomer> page = new Page<BasCustomer>(pageNo, pageSize);
		IPage<BasCustomer> pageList = basCustomerService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "客户-通过id查询")
	 @ApiOperation(value="客户-通过id查询", notes="客户-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<BasCustomer> queryById(@RequestParam(name="id",required=true) String id) {
		 BasCustomer basCustomer = basCustomerService.getById(id);
		 if(basCustomer==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(basCustomer);
	 }

	 /**
	 *   添加
	 *
	 * @param basCustomer
	 * @return
	 */
	@AutoLog(value = "客户-添加")
	@ApiOperation(value="客户-添加", notes="客户-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BasCustomer basCustomer) {
		basCustomerService.save(basCustomer);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param basCustomer
	 * @return
	 */
	@AutoLog(value = "客户-编辑")
	@ApiOperation(value="客户-编辑", notes="客户-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasCustomer basCustomer) {
		basCustomerService.updateById(basCustomer);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "客户-通过id删除")
	@ApiOperation(value="客户-通过id删除", notes="客户-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		basCustomerService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "客户-批量删除")
	@ApiOperation(value="客户-批量删除", notes="客户-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basCustomerService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param basCustomer
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BasCustomer basCustomer) {
        return super.exportXls(request, basCustomer, BasCustomer.class, "客户");
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
        return super.importExcel(request, response, BasCustomer.class);
    }

}
