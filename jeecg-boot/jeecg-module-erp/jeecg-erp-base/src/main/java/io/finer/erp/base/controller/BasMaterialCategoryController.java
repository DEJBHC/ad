package io.finer.erp.base.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.base.entity.BasMaterialCategory;
import io.finer.erp.base.service.IBasMaterialCategoryService;

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
 * @Description: 物料分类
 * @Author: jeecg-boot
 * @Date:   2022-09-03
 * @Version: V1.0
 */
@Api(tags="物料分类")
@RestController
@RequestMapping("/base/basMaterialCategory")
@Slf4j
public class BasMaterialCategoryController extends JeecgController<BasMaterialCategory, IBasMaterialCategoryService>{
	@Autowired
	private IBasMaterialCategoryService basMaterialCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param basMaterialCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物料分类-分页列表查询")
	@ApiOperation(value="物料分类-分页列表查询", notes="物料分类-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<BasMaterialCategory>> queryPageList(BasMaterialCategory basMaterialCategory,
															@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
		if(hasQuery != null && "true".equals(hasQuery)){
			QueryWrapper<BasMaterialCategory> queryWrapper =  QueryGenerator.initQueryWrapper(basMaterialCategory, req.getParameterMap());
			List<BasMaterialCategory> list = basMaterialCategoryService.queryTreeListNoPage(queryWrapper);
			IPage<BasMaterialCategory> pageList = new Page<>(1, 10, list.size());
			pageList.setRecords(list);
			return Result.OK(pageList);
		}else{
			String parentId = basMaterialCategory.getPid();
			if (oConvertUtils.isEmpty(parentId)) {
				parentId = "0";
			}
			basMaterialCategory.setPid(null);
			QueryWrapper<BasMaterialCategory> queryWrapper = QueryGenerator.initQueryWrapper(basMaterialCategory, req.getParameterMap());
			// 使用 eq 防止模糊查询
			queryWrapper.eq("pid", parentId);
			Page<BasMaterialCategory> page = new Page<BasMaterialCategory>(pageNo, pageSize);
			IPage<BasMaterialCategory> pageList = basMaterialCategoryService.page(page, queryWrapper);
			return Result.OK(pageList);
		}
	}

	/**
	 * 获取子数据
	 * @param basMaterialCategory
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "物料分类-获取子数据")
	@ApiOperation(value="物料分类-获取子数据", notes="物料分类-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<BasMaterialCategory>> queryPageList(BasMaterialCategory basMaterialCategory,HttpServletRequest req) {
		QueryWrapper<BasMaterialCategory> queryWrapper = QueryGenerator.initQueryWrapper(basMaterialCategory, req.getParameterMap());
		List<BasMaterialCategory> list = basMaterialCategoryService.list(queryWrapper);
		IPage<BasMaterialCategory> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "物料分类-批量获取子数据")
	@ApiOperation(value="物料分类-批量获取子数据", notes="物料分类-批量获取子数据")
	@GetMapping("/getChildListBatch")
	public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
		try {
			QueryWrapper<BasMaterialCategory> queryWrapper = new QueryWrapper<>();
			List<String> parentIdList = Arrays.asList(parentIds.split(","));
			queryWrapper.in("pid", parentIdList);
			List<BasMaterialCategory> list = basMaterialCategoryService.list(queryWrapper);
			IPage<BasMaterialCategory> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "物料分类-通过id查询")
	@ApiOperation(value="物料分类-通过id查询", notes="物料分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BasMaterialCategory> queryById(@RequestParam(name="id",required=true) String id) {
		BasMaterialCategory basMaterialCategory = basMaterialCategoryService.getById(id);
		if(basMaterialCategory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(basMaterialCategory);
	}

	/**
	 *   添加
	 *
	 * @param basMaterialCategory
	 * @return
	 */
	@AutoLog(value = "物料分类-添加")
	@ApiOperation(value="物料分类-添加", notes="物料分类-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BasMaterialCategory basMaterialCategory) {
		basMaterialCategoryService.addBasMaterialCategory(basMaterialCategory);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param basMaterialCategory
	 * @return
	 */
	@AutoLog(value = "物料分类-编辑")
	@ApiOperation(value="物料分类-编辑", notes="物料分类-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasMaterialCategory basMaterialCategory) {
		basMaterialCategoryService.updateBasMaterialCategory(basMaterialCategory);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物料分类-通过id删除")
	@ApiOperation(value="物料分类-通过id删除", notes="物料分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		basMaterialCategoryService.deleteBasMaterialCategory(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物料分类-批量删除")
	@ApiOperation(value="物料分类-批量删除", notes="物料分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basMaterialCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param basMaterialCategory
	 */
	@AutoLog(value = "导出为excel")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BasMaterialCategory basMaterialCategory) {
		return super.exportXls(request, basMaterialCategory, BasMaterialCategory.class, "物料分类");
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
		return super.importExcel(request, response, BasMaterialCategory.class);
	}

}
