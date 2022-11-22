package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinReceipt;
import io.finer.erp.finance.entity.FinReceiptEntry;
import io.finer.erp.finance.service.IFinReceiptEntryService;
import io.finer.erp.finance.service.IFinReceiptService;
import io.finer.erp.finance.vo.FinReceiptPage;
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
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2020-04-30
 * @Version: V1.0
 */
@Api(tags="收款单")
@RestController
@RequestMapping("/finance/finReceipt")
@Slf4j
public class FinReceiptController {
	@Autowired
	private IFinReceiptService finReceiptService;
	@Autowired
	private IFinReceiptEntryService finReceiptEntryService;

	/**
	 * 分页列表查询
	 *
	 * @param finReceipt
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "收款单-分页列表查询")
	@ApiOperation(value="收款单-分页列表查询", notes="收款单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{receiptType}"})  //receiptType会传至finReceiptType.receiptTypeType
	public Result<?> queryPageList(FinReceipt finReceipt,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FinReceipt> queryWrapper = QueryGenerator.initQueryWrapper(finReceipt, req.getParameterMap());
		Page<FinReceipt> page = new Page<FinReceipt>(pageNo, pageSize);
		IPage<FinReceipt> pageList = finReceiptService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 @GetMapping(value = "/checkableList")
	 public Result<?> queryCheckablePageList(FinReceipt finReceipt,
											 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											 HttpServletRequest req) {
		 QueryWrapper<FinReceipt> queryWrapper = QueryGenerator.initQueryWrapper(finReceipt, req.getParameterMap());
		 queryWrapper.eq("is_effective", 1);
		 queryWrapper.eq("is_voided", 0);
		 queryWrapper.apply("amt - checked_amt > 0");
		 Page<FinReceipt> page = new Page<FinReceipt>(pageNo, pageSize);
		 IPage<FinReceipt> pageList = finReceiptService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "收款单-通过id查询")
	 @ApiOperation(value="收款单-通过id查询", notes="收款单-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 FinReceipt finReceipt = finReceiptService.getById(id);
		 if(finReceipt==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.ok(finReceipt);

	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "收款明细集合-通过id查询")
	 @ApiOperation(value="收款明细集合-通过id查询", notes="收款明细-通过id查询")
	 @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	 public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<FinReceiptEntry> finReceiptEntryList = finReceiptEntryService.selectByMainId(id);
		 return Result.ok(finReceiptEntryList);
	 }

	 /**
	 *   新增
	 *
	 * @param finReceiptPage
	 * @return
	 */
	@AutoLog(value = "收款单-新增")
	@ApiOperation(value="收款单-新增", notes="收款单-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody FinReceiptPage finReceiptPage, @PathVariable String action) {
		FinReceipt bill = new FinReceipt();
		BeanUtils.copyProperties(finReceiptPage, bill);
		try {
			if (action.equals("submit")) {
				finReceiptService.submitAdd(bill, finReceiptPage.getFinReceiptEntryList());
				return Result.OK("新增提交成功！");
			} else {
				finReceiptService.saveAdd(bill, finReceiptPage.getFinReceiptEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param finReceiptPage
	 * @return
	 */
	@AutoLog(value = "收款单-编辑")
	@ApiOperation(value="收款单-编辑", notes="收款单-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody FinReceiptPage finReceiptPage, @PathVariable String action) {
		FinReceipt bill = new FinReceipt();
		BeanUtils.copyProperties(finReceiptPage, bill);
		try {
			if (action.equals("submit")) {
				finReceiptService.submitEdit(bill, finReceiptPage.getFinReceiptEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				finReceiptService.saveEdit(bill, finReceiptPage.getFinReceiptEntryList());
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
	@AutoLog(value = "收款单-通过id删除")
	@ApiOperation(value="收款单-通过id删除", notes="收款单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			finReceiptService.delete(id);
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
	@AutoLog(value = "收款单-批量删除")
	@ApiOperation(value="收款单-批量删除", notes="收款单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.finReceiptService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "收款单-审核")
	 @ApiOperation(value="收款单-审核", notes="收款单-审核")
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK( "审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单-发起审批")
	 @ApiOperation(value="收款单-发起审批", notes="收款单-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单-结束审批")
	 @ApiOperation(value="收款单-结束审批", notes="收款单-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单-执行")
	 @ApiOperation(value="收款单-执行", notes="收款单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单-关闭")
	 @ApiOperation(value="收款单-关闭", notes="收款单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单--反关闭")
	 @ApiOperation(value="收款单--反关闭", notes="收款单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.unclose(json.getString("id"));
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
	 @AutoLog(value = "收款单-批量关闭")
	 @ApiOperation(value="收款单-批量关闭", notes="收款单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "收款单-批量反关闭")
	 @ApiOperation(value="收款单-批量反关闭", notes="收款单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "收款单-作废")
	 @ApiOperation(value="收款单-作废", notes="收款单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 finReceiptService.voidBill(json.getString("id"));
			 return Result.OK("单据作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
    * 导出excel
    *
    * @param request
    * @param finReceipt
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value = {"/exportXls", "/exportXls/{receiptType}"})
    public ModelAndView exportXls(HttpServletRequest request, FinReceipt finReceipt) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<FinReceipt> queryWrapper = QueryGenerator.initQueryWrapper(finReceipt, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<FinReceipt> queryList = finReceiptService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<FinReceipt> finReceiptList = new ArrayList<FinReceipt>();
      if(oConvertUtils.isEmpty(selections)) {
          finReceiptList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          finReceiptList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<FinReceiptPage> pageList = new ArrayList<FinReceiptPage>();
      for (FinReceipt main : finReceiptList) {
          FinReceiptPage vo = new FinReceiptPage();
          BeanUtils.copyProperties(main, vo);
          List<FinReceiptEntry> finReceiptEntryList = finReceiptEntryService.selectByMainId(main.getId());
          vo.setFinReceiptEntryList(finReceiptEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "收款单列表");
      mv.addObject(NormalExcelConstants.CLASS, FinReceiptPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("收款单数据", "导出人:"+sysUser.getRealname(), "收款单"));
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
    @RequestMapping(value = {"/importExcel", "/importExcel/{receiptType}"}, method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, @PathVariable(required = false) String receiptType) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<FinReceiptPage> list = ExcelImportUtil.importExcel(file.getInputStream(), FinReceiptPage.class, params);

			  Map<FinReceipt, List<FinReceiptEntry>> billMap = new HashMap<>();
			  for (FinReceiptPage page : list) {
				  FinReceipt bill = new FinReceipt();
				  BeanUtils.copyProperties(page, bill);
				  if (receiptType != null && !bill.getReceiptType().equals(receiptType) ) {
					  throw new JeecgBootException(bill.getBillNo() + "：收款类型不符！");
				  }
				  billMap.put(bill, page.getFinReceiptEntryList());
			  }
              finReceiptService.saveAddBatch(billMap); //20211205 cfm: 事务化

			  return Result.ok("文件导入成功！数据行数:" + list.size());
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
      return Result.ok("文件导入失败！");
    }

}
