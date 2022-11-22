package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinPurInvoice;
import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.finance.service.IFinPurInvoiceEntryService;
import io.finer.erp.finance.service.IFinPurInvoiceService;
import io.finer.erp.finance.vo.FinPurInvoicePage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.exception.JeecgBootException;
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
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 采购发票登记
 * @Author:
 * @Date:   2020-05-21
 * @Version: V1.0
 */
@Api(tags="采购发票登记")
@RestController
@RequestMapping("/finance/finPurInvoice")
@Slf4j
public class FinPurInvoiceController {
	@Autowired
	private IFinPurInvoiceService finPurInvoiceService;
	@Autowired
	private IFinPurInvoiceEntryService finPurInvoiceEntryService;

	/**
	 * 分页列表查询
	 *
	 * @param finPurInvoice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "采购发票登记-分页列表查询")
	@ApiOperation(value="采购发票登记-分页列表查询", notes="采购发票登记-分页列表查询")
	@GetMapping(value = {"/list", "/list/{isRubric}/{isReturned}"})
	public Result<?> queryPageList(FinPurInvoice finPurInvoice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FinPurInvoice> queryWrapper = QueryGenerator.initQueryWrapper(finPurInvoice, req.getParameterMap());
		Page<FinPurInvoice> page = new Page<FinPurInvoice>(pageNo, pageSize);
		IPage<FinPurInvoice> pageList = finPurInvoiceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "采购发票登记-通过id查询")
	 @ApiOperation(value="采购发票登记-通过id查询", notes="采购发票登记-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 FinPurInvoice finPurInvoice = finPurInvoiceService.getById(id);
		 if(finPurInvoice==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(finPurInvoice);
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
		 List<FinPurInvoiceEntry> finPurInvoiceEntryList = finPurInvoiceEntryService.selectByMainId(id);
		 return Result.OK(finPurInvoiceEntryList);
	 }

	 /**
	 *   新增
	 *
	 * @param finPurInvoicePage
	 * @return
	 */
	@AutoLog(value = "采购发票登记-新增")
	@ApiOperation(value="采购发票登记-新增", notes="采购发票登记-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody FinPurInvoicePage finPurInvoicePage, @PathVariable String action) {
		FinPurInvoice bill = new FinPurInvoice();
		BeanUtils.copyProperties(finPurInvoicePage, bill);
		try {
			if (action.equals("submit")) {
				finPurInvoiceService.submitAdd(bill, finPurInvoicePage.getFinPurInvoiceEntryList());
				return Result.OK("新增提交成功！");
			} else {
				finPurInvoiceService.saveAdd(bill, finPurInvoicePage.getFinPurInvoiceEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param finPurInvoicePage
	 * @return
	 */
	@AutoLog(value = "采购发票登记-编辑")
	@ApiOperation(value="采购发票登记-编辑", notes="采购发票登记-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody FinPurInvoicePage finPurInvoicePage, @PathVariable String action) {
		FinPurInvoice bill = new FinPurInvoice();
		BeanUtils.copyProperties(finPurInvoicePage, bill);
		try {
			if (action.equals("submit")) {
				finPurInvoiceService.submitEdit(bill, finPurInvoicePage.getFinPurInvoiceEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				finPurInvoiceService.saveEdit(bill, finPurInvoicePage.getFinPurInvoiceEntryList());
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
	@AutoLog(value = "采购发票登记-通过id删除")
	@ApiOperation(value="采购发票登记-通过id删除", notes="采购发票登记-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			finPurInvoiceService.delete(id);
			return Result.OK("删除成功!");
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
	@AutoLog(value = "采购发票登记-批量删除")
	@ApiOperation(value="采购发票登记-批量删除", notes="采购发票登记-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.finPurInvoiceService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "采购发票登记-审核")
	 @ApiOperation(value="采购发票登记-审核", notes="采购发票登记-审核")
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK( "审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记-发起审批")
	 @ApiOperation(value="采购发票登记-发起审批", notes="采购发票登记-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记-结束审批")
	 @ApiOperation(value="采购发票登记-结束审批", notes="采购发票登记-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记-执行")
	 @ApiOperation(value="采购发票登记-执行", notes="采购发票登记-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记-关闭")
	 @ApiOperation(value="采购发票登记-关闭", notes="采购发票登记-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记--反关闭")
	 @ApiOperation(value="采购发票登记--反关闭", notes="采购发票登记--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.unclose(json.getString("id"));
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
	 @AutoLog(value = "采购发票登记-批量关闭")
	 @ApiOperation(value="采购发票登记-批量关闭", notes="采购发票登记-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "采购发票登记-批量反关闭")
	 @ApiOperation(value="采购发票登记-批量反关闭", notes="采购发票登记-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "采购发票登记-作废")
	 @ApiOperation(value="采购发票登记-作废", notes="采购发票登记-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 finPurInvoiceService.voidBill(json.getString("id"));
			 return Result.OK("单据作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param finPurInvoice
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value = {"/exportXls", "/exportXls/{isRubric}/{isReturned}"})
    public ModelAndView exportXls(HttpServletRequest request, FinPurInvoice finPurInvoice) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<FinPurInvoice> queryWrapper = QueryGenerator.initQueryWrapper(finPurInvoice, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<FinPurInvoice> queryList = finPurInvoiceService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<FinPurInvoice> finPurInvoiceList = new ArrayList<FinPurInvoice>();
      if(oConvertUtils.isEmpty(selections)) {
          finPurInvoiceList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          finPurInvoiceList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<FinPurInvoicePage> pageList = new ArrayList<FinPurInvoicePage>();
      for (FinPurInvoice main : finPurInvoiceList) {
          FinPurInvoicePage vo = new FinPurInvoicePage();
          BeanUtils.copyProperties(main, vo);
          List<FinPurInvoiceEntry> finPurInvoiceEntryList = finPurInvoiceEntryService.selectByMainId(main.getId());
          vo.setFinPurInvoiceEntryList(finPurInvoiceEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "采购发票登记列表");
      mv.addObject(NormalExcelConstants.CLASS, FinPurInvoicePage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("采购发票登记数据", "导出人:"+sysUser.getRealname(), "采购发票登记"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @return
	  */
	 @AutoLog(value = "通过excel导入数据")
	 @RequestMapping(value = {"/importExcel", "/importExcel/{isRubric}/{isReturned}"}, method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request,
								  @PathVariable(required = false) Integer isRubric,
								  @PathVariable(required = false) Integer isReturned) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<FinPurInvoicePage> list = ExcelImportUtil.importExcel(file.getInputStream(), FinPurInvoicePage.class, params);

				 Map<FinPurInvoice, List<FinPurInvoiceEntry>> billMap = new HashMap<>();
				 for (FinPurInvoicePage page : list) {
					 FinPurInvoice bill = new FinPurInvoice();
					 BeanUtils.copyProperties(page, bill);
					 if (isRubric != null && !bill.getIsRubric().equals(isRubric)) {
						 throw new JeecgBootException(bill.getBillNo() + "：“是否红字”不符！");
					 }
					 if (isReturned != null && !bill.getIsReturned().equals(isReturned)) {
						 throw new JeecgBootException(bill.getBillNo() + "：“是否退货”不符！");
					 }
					 billMap.put(bill, page.getFinPurInvoiceEntryList());
				 }
				 finPurInvoiceService.saveAddBatch(billMap); //20211205 cfm：事务化

				 return Result.OK("文件导入成功！数据行数:" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(),e);
				 return Result.error("文件导入失败:"+e.getMessage());
			 } finally {
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
