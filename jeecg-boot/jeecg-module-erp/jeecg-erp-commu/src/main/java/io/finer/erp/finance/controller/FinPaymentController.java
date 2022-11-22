package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinPayment;
import io.finer.erp.finance.entity.FinPaymentEntry;
import io.finer.erp.finance.service.IFinPaymentEntryService;
import io.finer.erp.finance.service.IFinPaymentService;
import io.finer.erp.finance.vo.FinPaymentPage;
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
 * @Description: 付款单
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Api(tags="付款单")
@RestController
@RequestMapping("/finance/finPayment")
@Slf4j
public class FinPaymentController {
	@Autowired
	private IFinPaymentService finPaymentService;
	@Autowired
	private IFinPaymentEntryService finPaymentEntryService;

	/**
	 * 分页列表查询
	 *
	 * @param finPayment
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "付款单-分页列表查询")
	@ApiOperation(value="付款单-分页列表查询", notes="付款单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{paymentType}"})  //paymentType会传至finPayment.paymentType
	public Result<?> queryPageList(FinPayment finPayment,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FinPayment> queryWrapper = QueryGenerator.initQueryWrapper(finPayment, req.getParameterMap());
		Page<FinPayment> page = new Page<FinPayment>(pageNo, pageSize);
		IPage<FinPayment> pageList = finPaymentService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @GetMapping(value = "/checkableList")
	 public Result<?> queryCheckablePageList(FinPayment finPayment,
											 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											 HttpServletRequest req) {
		 QueryWrapper<FinPayment> queryWrapper = QueryGenerator.initQueryWrapper(finPayment, req.getParameterMap());
		 queryWrapper.eq("is_effective", 1);
		 queryWrapper.eq("is_voided", 0);
		 queryWrapper.apply("amt - checked_amt > 0");
		 Page<FinPayment> page = new Page<FinPayment>(pageNo, pageSize);
		 IPage<FinPayment> pageList = finPaymentService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "付款单-通过id查询")
	 @ApiOperation(value="付款单-通过id查询", notes="付款单-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 FinPayment finPayment = finPaymentService.getById(id);
		 if(finPayment==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(finPayment);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "付款明细集合-通过id查询")
	 @ApiOperation(value="付款明细集合-通过id查询", notes="付款明细-通过id查询")
	 @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	 public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<FinPaymentEntry> finPaymentEntryList = finPaymentEntryService.selectByMainId(id);
		 return Result.OK(finPaymentEntryList);
	 }

	 /**
	 *   新增
	 *
	 * @param finPaymentPage
	 * @return
	 */
	@AutoLog(value = "付款单-新增")
	@ApiOperation(value="付款单-新增", notes="付款单-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody FinPaymentPage finPaymentPage, @PathVariable String action) {
		FinPayment bill = new FinPayment();
		BeanUtils.copyProperties(finPaymentPage, bill);
		try {
			if (action.equals("submit")) {
				finPaymentService.submitAdd(bill, finPaymentPage.getFinPaymentEntryList());
				return Result.OK("新增提交成功！");
			} else {
				finPaymentService.saveAdd(bill, finPaymentPage.getFinPaymentEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param finPaymentPage
	 * @return
	 */
	@AutoLog(value = "付款单-编辑")
	@ApiOperation(value="付款单-编辑", notes="付款单-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody FinPaymentPage finPaymentPage, @PathVariable String action) {
		FinPayment bill = new FinPayment();
		BeanUtils.copyProperties(finPaymentPage, bill);
		try {
			if (action.equals("submit")) {
				finPaymentService.submitEdit(bill, finPaymentPage.getFinPaymentEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				finPaymentService.saveEdit(bill, finPaymentPage.getFinPaymentEntryList());
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
	@AutoLog(value = "付款单-通过id删除")
	@ApiOperation(value="付款单-通过id删除", notes="付款单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			finPaymentService.delete(id);
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
	@AutoLog(value = "付款单-批量删除")
	@ApiOperation(value="付款单-批量删除", notes="付款单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.finPaymentService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "付款单-审核")
	 @ApiOperation(value="付款单-审核", notes="付款单-审核")
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK( "审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单-发起审批")
	 @ApiOperation(value="付款单-发起审批", notes="付款单-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单-结束审批")
	 @ApiOperation(value="付款单-结束审批", notes="付款单-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单-执行")
	 @ApiOperation(value="付款单-执行", notes="付款单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单-关闭")
	 @ApiOperation(value="付款单-关闭", notes="付款单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单--反关闭")
	 @ApiOperation(value="付款单--反关闭", notes="付款单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.unclose(json.getString("id"));
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
	 @AutoLog(value = "付款单-批量关闭")
	 @ApiOperation(value="付款单-批量关闭", notes="付款单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "付款单-批量反关闭")
	 @ApiOperation(value="付款单-批量反关闭", notes="付款单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款单-作废")
	 @ApiOperation(value="付款单-作废", notes="付款单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 finPaymentService.voidBill(json.getString("id"));
			 return Result.OK("单据作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
    * 导出为excel
    *
    * @param request
    * @param finPayment
    */
	@AutoLog(value = "导出为excel")
    @RequestMapping(value =  {"/exportXls", "/exportXls/{paymentType}"})
    public ModelAndView exportXls(HttpServletRequest request, FinPayment finPayment) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<FinPayment> queryWrapper = QueryGenerator.initQueryWrapper(finPayment, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<FinPayment> queryList = finPaymentService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<FinPayment> finPaymentList = new ArrayList<FinPayment>();
      if(oConvertUtils.isEmpty(selections)) {
          finPaymentList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          finPaymentList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<FinPaymentPage> pageList = new ArrayList<FinPaymentPage>();
      for (FinPayment main : finPaymentList) {
          FinPaymentPage vo = new FinPaymentPage();
          BeanUtils.copyProperties(main, vo);
          List<FinPaymentEntry> finPaymentEntryList = finPaymentEntryService.selectByMainId(main.getId());
          vo.setFinPaymentEntryList(finPaymentEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "付款单列表");
      mv.addObject(NormalExcelConstants.CLASS, FinPaymentPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("付款单数据", "导出人:"+sysUser.getRealname(), "付款单"));
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
    @RequestMapping(value = {"/importExcel", "/importExcel/{paymentType}"}, method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, @PathVariable(required = false) String paymentType) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<FinPaymentPage> list = ExcelImportUtil.importExcel(file.getInputStream(), FinPaymentPage.class, params);

			  Map<FinPayment, List<FinPaymentEntry>> billMap = new HashMap<>();
			  for (FinPaymentPage page : list) {
				  FinPayment bill = new FinPayment();
				  BeanUtils.copyProperties(page, bill);
				  if (paymentType != null && !bill.getPaymentType().equals(paymentType) ) {
					  throw new JeecgBootException(bill.getBillNo() + "：付款类型不符！");
				  }
				  billMap.put(bill, page.getFinPaymentEntryList());
			  }
              finPaymentService.saveAddBatch(billMap);//20211205 cfm：事务化

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
