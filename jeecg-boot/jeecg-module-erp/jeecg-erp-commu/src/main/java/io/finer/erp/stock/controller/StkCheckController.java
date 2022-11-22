package io.finer.erp.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.entity.StkCheck;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.service.IStkCheckEntryService;
import io.finer.erp.stock.service.IStkCheckService;
import io.finer.erp.stock.service.IStkInventoryService;
import io.finer.erp.stock.vo.StkCheckPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 盘点卡
 * @Author: jeecg-boot
 * @Date:   2020-05-18
 * @Version: V1.0
 */
@Api(tags="盘点卡")
@RestController
@RequestMapping("/stock/stkCheck")
@Slf4j
public class StkCheckController {
	@Autowired
	private IStkCheckService stkCheckService;
	@Autowired
	private IStkCheckEntryService stkCheckEntryService;
	@Autowired
	private IStkInventoryService stkInventoryService;

	/**
	 * 分页列表查询
	 *
	 * @param stkCheck
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "盘点卡-分页列表查询")
	@ApiOperation(value="盘点卡-分页列表查询", notes="盘点卡-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(StkCheck stkCheck,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StkCheck> queryWrapper = QueryGenerator.initQueryWrapper(stkCheck, req.getParameterMap());
		Page<StkCheck> page = new Page<StkCheck>(pageNo, pageSize);
		IPage<StkCheck> pageList = stkCheckService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "盘点卡-通过id查询")
	 @ApiOperation(value="盘点卡-通过id查询", notes="盘点卡-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 StkCheck stkCheck = stkCheckService.getById(id);
		 if(stkCheck ==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(stkCheck);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "明细通过主表ID查询")
	 @ApiOperation(value="明细主表ID查询", notes="明细-通主表ID查询")
	 @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	 public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<StkCheckEntry> stkCheckEntryList = stkCheckEntryService.selectByMainId(id);
		 return Result.OK(stkCheckEntryList);
	 }

	 //@AutoLog(value = "明细通过盘点范围查询")
	 @ApiOperation(value="明细通过盘点范围查询", notes="明细-通过盘点范围查询")
	 @GetMapping(value = "/queryEntryByRange")
	 public Result<?> queryEntryListByRange(StkCheck stkCheck, HttpServletRequest req) {
		 QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("is_closed", 0);

		 String sql =   "SELECT id FROM bas_warehouse %s";
		 String where = " WHERE `code` LIKE (SELECT CONCAT(`code`, '%%') FROM bas_warehouse WHERE id = '%s')";
		 String id = stkCheck.getWarehouseId();
		 where = (id == null || id.isEmpty()) ? "" : String.format(where, id);
		 queryWrapper.inSql("warehouse_id", String.format(sql, where));

		 sql =   "SELECT m.id " +
				 "  FROM bas_material_category c JOIN bas_material m ON c.id = m.category_id ";
		 where = " WHERE c.code LIKE (SELECT CONCAT(`code`, '%%') FROM bas_material_category WHERE id = '%s')";
		 id = stkCheck.getMaterialCategoryId();
		 where = (id == null || id.isEmpty()) ? "" : String.format(where, id);
		 queryWrapper.inSql("material_id", String.format(sql, where));

		 List<StkInventory> invList = stkInventoryService.list(queryWrapper);
		 List<StkCheckEntry> checkList = new ArrayList<>();
		 int i = 1;
		 for(StkInventory inv:invList) {
			 StkCheckEntry check = new StkCheckEntry();
			 BeanUtils.copyProperties(inv, check,
					 "id", "qty", "remark", "custom1", "custom2", "version");
			 check.setEntryNo(i++);
			 check.setIsNewBatch(0);
			 check.setBookQty(inv.getQty());
			 check.setQty(inv.getQty()); //初始：实存数量=账存数量，有盈亏的才需录入
			 check.setProfitQty(BigDecimal.ZERO);
			 checkList.add(check);
		 }
		 return Result.OK(checkList);
	 }

	 /**
	 *   新增
	 *
	 * @param stkCheckPage
	 * @return
	 */
	@AutoLog(value = "盘点卡-新增")
	@ApiOperation(value="盘点卡-新增", notes="盘点卡-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody StkCheckPage stkCheckPage, @PathVariable String action) {
		StkCheck bill = new StkCheck();
		BeanUtils.copyProperties(stkCheckPage, bill);
		try {
			if (action.equals("submit")) {
				stkCheckService.submitAdd(bill, stkCheckPage.getStkCheckEntryList());
				return Result.OK("新增提交成功！");
			} else {
				stkCheckService.saveAdd(bill, stkCheckPage.getStkCheckEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param stkCheckPage
	 * @return
	 */
	@AutoLog(value = "盘点卡-编辑")
	@ApiOperation(value="盘点卡-编辑", notes="盘点卡-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody StkCheckPage stkCheckPage, @PathVariable String action) {
		StkCheck bill = new StkCheck();
		BeanUtils.copyProperties(stkCheckPage, bill);
		try {
			if (action.equals("submit")) {
				stkCheckService.submitEdit(bill, stkCheckPage.getStkCheckEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				stkCheckService.saveEdit(bill, stkCheckPage.getStkCheckEntryList());
				return Result.OK("编辑保存成功!");
			}

		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "盘点卡-通过id删除")
	@ApiOperation(value="盘点卡-通过id删除", notes="盘点卡-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			stkCheckService.delete(id);
			return Result.OK("单据删除成功!");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "盘点卡-批量删除")
	@ApiOperation(value="盘点卡-批量删除", notes="盘点卡-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.stkCheckService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "盘点卡-审核")
	 @ApiOperation(value="盘点卡-审核", notes="盘点卡-审核")
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK( "审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-发起审批")
	 @ApiOperation(value="盘点卡-发起审批", notes="盘点卡-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-结束审批")
	 @ApiOperation(value="盘点卡-结束审批", notes="盘点卡-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-执行")
	 @ApiOperation(value="盘点卡-执行", notes="盘点卡-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-关闭")
	 @ApiOperation(value="盘点卡-关闭", notes="盘点卡-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡--反关闭")
	 @ApiOperation(value="盘点卡--反关闭", notes="盘点卡--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.unclose(json.getString("id"));
			 return Result.OK("反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "盘点卡-批量关闭")
	 @ApiOperation(value="盘点卡-批量关闭", notes="盘点卡-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.close(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量反关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "盘点卡-批量反关闭")
	 @ApiOperation(value="盘点卡-批量反关闭", notes="盘点卡-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-作废")
	 @ApiOperation(value="盘点卡-作废", notes="盘点卡-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.voidBill(json.getString("id"));
			 return Result.OK("作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

    /**
    * 导出为excel
    *
    * @param request
    * @param stkCheck
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StkCheck stkCheck) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<StkCheck> queryWrapper = QueryGenerator.initQueryWrapper(stkCheck, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<StkCheck> queryList = stkCheckService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<StkCheck> stkCheckList = new ArrayList<StkCheck>();
      if(oConvertUtils.isEmpty(selections)) {
          stkCheckList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          stkCheckList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<StkCheckPage> pageList = new ArrayList<StkCheckPage>();
      for (StkCheck main : stkCheckList) {
          StkCheckPage vo = new StkCheckPage();
          BeanUtils.copyProperties(main, vo);
          List<StkCheckEntry> stkCheckEntryList = stkCheckEntryService.selectByMainId(main.getId());
          vo.setStkCheckEntryList(stkCheckEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "盘点卡列表");
      mv.addObject(NormalExcelConstants.CLASS, StkCheckPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("盘点卡数据", "导出人:"+sysUser.getRealname(), "盘点卡"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
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
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<StkCheckPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StkCheckPage.class, params);

				Map<StkCheck, List<StkCheckEntry>> billMap = new HashMap<>();
				for (StkCheckPage page : list) {
					StkCheck bill = new StkCheck();
					BeanUtils.copyProperties(page, bill);
					billMap.put(bill, page.getStkCheckEntryList());
				}
				stkCheckService.saveAddBatch(billMap); //20211204 cfm：事务化

				return Result.OK("文件导入成功！数据行数:" + list.size());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("文件导入失败:"+e.getMessage());
			}
			finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.OK("文件导入失败！");
    }

 }
