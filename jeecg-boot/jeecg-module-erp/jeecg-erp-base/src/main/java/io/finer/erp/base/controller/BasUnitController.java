package io.finer.erp.base.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.base.entity.BasUnit;
import io.finer.erp.base.service.IBasUnitService;

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
 * @Description: 计量单位
 * @Author: jeecg-boot
 * @Date:   2022-09-03
 * @Version: V1.0
 */
@Api(tags="计量单位")
@RestController
@RequestMapping("/base/basUnit")
@Slf4j
public class BasUnitController extends JeecgController<BasUnit, IBasUnitService>{
	@Autowired
	private IBasUnitService basUnitService;

	/**
	 * 分页列表查询
	 *
	 * @param basUnit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "计量单位-分页列表查询")
	@ApiOperation(value="计量单位-分页列表查询", notes="计量单位-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<BasUnit>> queryPageList(BasUnit basUnit,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
		if(hasQuery != null && "true".equals(hasQuery)){
			QueryWrapper<BasUnit> queryWrapper =  QueryGenerator.initQueryWrapper(basUnit, req.getParameterMap());
			List<BasUnit> list = basUnitService.queryTreeListNoPage(queryWrapper);
			IPage<BasUnit> pageList = new Page<>(1, 10, list.size());
			pageList.setRecords(list);
			return Result.OK(pageList);
		}else{
			String parentId = basUnit.getPid();
			if (oConvertUtils.isEmpty(parentId)) {
				parentId = "0";
			}
			basUnit.setPid(null);
			QueryWrapper<BasUnit> queryWrapper = QueryGenerator.initQueryWrapper(basUnit, req.getParameterMap());
			// 使用 eq 防止模糊查询
			queryWrapper.eq("pid", parentId);
			Page<BasUnit> page = new Page<BasUnit>(pageNo, pageSize);
			IPage<BasUnit> pageList = basUnitService.page(page, queryWrapper);
			return Result.OK(pageList);
		}
	}

	/**
	 * 获取子数据
	 * @param basUnit
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "计量单位-获取子数据")
	@ApiOperation(value="计量单位-获取子数据", notes="计量单位-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<BasUnit>> queryPageList(BasUnit basUnit,HttpServletRequest req) {
		QueryWrapper<BasUnit> queryWrapper = QueryGenerator.initQueryWrapper(basUnit, req.getParameterMap());
		List<BasUnit> list = basUnitService.list(queryWrapper);
		IPage<BasUnit> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "计量单位-批量获取子数据")
	@ApiOperation(value="计量单位-批量获取子数据", notes="计量单位-批量获取子数据")
	@GetMapping("/getChildListBatch")
	public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
		try {
			QueryWrapper<BasUnit> queryWrapper = new QueryWrapper<>();
			List<String> parentIdList = Arrays.asList(parentIds.split(","));
			queryWrapper.in("pid", parentIdList);
			List<BasUnit> list = basUnitService.list(queryWrapper);
			IPage<BasUnit> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "计量单位-通过id查询")
	@ApiOperation(value="计量单位-通过id查询", notes="计量单位-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BasUnit> queryById(@RequestParam(name="id",required=true) String id) {
		BasUnit basUnit = basUnitService.getById(id);
		if(basUnit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(basUnit);
	}

	/**
	 *   添加
	 *
	 * @param basUnit
	 * @return
	 */
	@AutoLog(value = "计量单位-添加")
	@ApiOperation(value="计量单位-添加", notes="计量单位-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BasUnit basUnit) {
		basUnitService.addBasUnit(basUnit);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param basUnit
	 * @return
	 */
	@AutoLog(value = "计量单位-编辑")
	@ApiOperation(value="计量单位-编辑", notes="计量单位-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasUnit basUnit) {
		basUnitService.updateBasUnit(basUnit);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "计量单位-通过id删除")
	@ApiOperation(value="计量单位-通过id删除", notes="计量单位-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		basUnitService.deleteBasUnit(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "计量单位-批量删除")
	@ApiOperation(value="计量单位-批量删除", notes="计量单位-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basUnitService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param basUnit
	 */
	@AutoLog(value = "导出为excel")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BasUnit basUnit) {
		return super.exportXls(request, basUnit, BasUnit.class, "计量单位");
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
		return super.importExcel(request, response, BasUnit.class);
	}

	@GetMapping(value = "/getRate")
	public Result<?> getRate(@RequestParam(name="toId", required=true) String toId,
							 @RequestParam(name="fromId", required=true) String fromID,
							 HttpServletRequest req) {
		return Result.ok(basUnitService.getRate(fromID, toId));
	}
}
