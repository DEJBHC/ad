package io.finer.erp.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.entity.StkMaterialQty;
import io.finer.erp.stock.service.IStkMaterialQtyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: stk_material_qty
 * @Author: jeecg-boot
 * @Date:   2022-08-16
 * @Version: V1.0
 */
@Api(tags="stk_material_qty")
@RestController
@RequestMapping("/stock/stkMaterialQty")
@Slf4j
public class StkMaterialQtyController extends JeecgController<StkMaterialQty, IStkMaterialQtyService> {
	@Autowired
	private IStkMaterialQtyService stkMaterialQtyService;

	/**
	 * 分页列表查询
	 *
	 * @param stkMaterialQty
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "stk_material_qty-分页列表查询")
	@ApiOperation(value="stk_material_qty-分页列表查询", notes="stk_material_qty-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StkMaterialQty>> queryPageList(StkMaterialQty stkMaterialQty,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StkMaterialQty> queryWrapper = QueryGenerator.initQueryWrapper(stkMaterialQty, req.getParameterMap());
		Page<StkMaterialQty> page = new Page<StkMaterialQty>(pageNo, pageSize);
		IPage<StkMaterialQty> pageList = stkMaterialQtyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "stk_material_qty-通过id查询")
	@ApiOperation(value="stk_material_qty-通过id查询", notes="stk_material_qty-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StkMaterialQty> queryById(@RequestParam(name="id",required=true) String id) {
		StkMaterialQty stkMaterialQty = stkMaterialQtyService.getById(id);
		if(stkMaterialQty==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stkMaterialQty);
	}

	 /**
	  * 通过ids列表查询
	  *
	  */
	 //@AutoLog(value = "stk_material_qty-通过ids列表查询")
	 @ApiOperation(value="stk_material_qty-通过ids列表查询", notes="stk_material_qty-通过ids列表查询")
	 @GetMapping(value = "/listByIds")
	 public Result<List<StkMaterialQty>> queryListByIds(@RequestParam(name="ids",required=true) String ids) {
		 List<StkMaterialQty> list = stkMaterialQtyService.listByIds(Arrays.asList(ids.split(",")));
		 return Result.OK(list);
	 }

 }
