package io.finer.erp.base.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.base.entity.BasWarehouse;
import io.finer.erp.base.service.IBasWarehouseService;

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
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date:   2022-09-02
 * @Version: V1.0
 */
@Api(tags="仓库")
@RestController
@RequestMapping("/base/basWarehouse")
@Slf4j
public class BasWarehouseController extends JeecgController<BasWarehouse, IBasWarehouseService>{
	@Autowired
	private IBasWarehouseService basWarehouseService;

	/**
	 * 分页列表查询
	 *
	 * @param basWarehouse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "仓库-分页列表查询")
	@ApiOperation(value="仓库-分页列表查询", notes="仓库-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<BasWarehouse>> queryPageList(BasWarehouse basWarehouse,
													 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													 HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
		if(hasQuery != null && "true".equals(hasQuery)){
			QueryWrapper<BasWarehouse> queryWrapper =  QueryGenerator.initQueryWrapper(basWarehouse, req.getParameterMap());
			List<BasWarehouse> list = basWarehouseService.queryTreeListNoPage(queryWrapper);
			IPage<BasWarehouse> pageList = new Page<>(1, 10, list.size());
			pageList.setRecords(list);
			return Result.OK(pageList);
		}else{
			String parentId = basWarehouse.getPid();
			if (oConvertUtils.isEmpty(parentId)) {
				parentId = "0";
			}
			basWarehouse.setPid(null);
			QueryWrapper<BasWarehouse> queryWrapper = QueryGenerator.initQueryWrapper(basWarehouse, req.getParameterMap());
			// 使用 eq 防止模糊查询
			queryWrapper.eq("pid", parentId);
			Page<BasWarehouse> page = new Page<BasWarehouse>(pageNo, pageSize);
			IPage<BasWarehouse> pageList = basWarehouseService.page(page, queryWrapper);
			return Result.OK(pageList);
		}
	}

	/**
	 * 获取子数据
	 * @param basWarehouse
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "仓库-获取子数据")
	@ApiOperation(value="仓库-获取子数据", notes="仓库-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<BasWarehouse>> queryPageList(BasWarehouse basWarehouse,HttpServletRequest req) {
		QueryWrapper<BasWarehouse> queryWrapper = QueryGenerator.initQueryWrapper(basWarehouse, req.getParameterMap());
		List<BasWarehouse> list = basWarehouseService.list(queryWrapper);
		IPage<BasWarehouse> pageList = new Page<>(1, 10, list.size());
		pageList.setRecords(list);
		return Result.OK(pageList);
	}

	/**
	 * 批量查询子节点
	 * @param parentIds 父ID（多个采用半角逗号分割）
	 * @return 返回 IPage
	 * @param parentIds
	 * @return
	 */
	//@AutoLog(value = "仓库-批量获取子数据")
	@ApiOperation(value="仓库-批量获取子数据", notes="仓库-批量获取子数据")
	@GetMapping("/getChildListBatch")
	public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
		try {
			QueryWrapper<BasWarehouse> queryWrapper = new QueryWrapper<>();
			List<String> parentIdList = Arrays.asList(parentIds.split(","));
			queryWrapper.in("pid", parentIdList);
			List<BasWarehouse> list = basWarehouseService.list(queryWrapper);
			IPage<BasWarehouse> pageList = new Page<>(1, 10, list.size());
			pageList.setRecords(list);
			return Result.OK(pageList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("批量查询子节点失败：" + e.getMessage());
		}
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "仓库-通过id查询")
	@ApiOperation(value="仓库-通过id查询", notes="仓库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BasWarehouse> queryById(@RequestParam(name="id",required=true) String id) {
		BasWarehouse basWarehouse = basWarehouseService.getById(id);
		if(basWarehouse==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(basWarehouse);
	}

	/**
	 *   添加
	 *
	 * @param basWarehouse
	 * @return
	 */
	@AutoLog(value = "仓库-添加")
	@ApiOperation(value="仓库-添加", notes="仓库-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BasWarehouse basWarehouse) {
		basWarehouseService.addBasWarehouse(basWarehouse);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param basWarehouse
	 * @return
	 */
	@AutoLog(value = "仓库-编辑")
	@ApiOperation(value="仓库-编辑", notes="仓库-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasWarehouse basWarehouse) {
		basWarehouseService.updateBasWarehouse(basWarehouse);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "仓库-通过id删除")
	@ApiOperation(value="仓库-通过id删除", notes="仓库-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		basWarehouseService.deleteBasWarehouse(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "仓库-批量删除")
	@ApiOperation(value="仓库-批量删除", notes="仓库-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basWarehouseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param basWarehouse
	 */
	@AutoLog(value = "导出为excel")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BasWarehouse basWarehouse) {
		return super.exportXls(request, basWarehouse, BasWarehouse.class, "仓库");
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
		return super.importExcel(request, response, BasWarehouse.class);
	}

}
