package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinPaymentReq;
import io.finer.erp.finance.entity.FinPaymentReqEntry;
import io.finer.erp.finance.service.IFinPaymentReqEntryService;
import io.finer.erp.finance.service.IFinPaymentReqService;
import io.finer.erp.finance.vo.FinPaymentReqPage;
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
 * @Description: 付款申请单
 * @Author: jeecg-boot
 * @Date:   2022-06-13
 * @Version: V1.0
 */
@Api(tags="付款申请单")
@RestController
@RequestMapping("/finance/finPaymentReq")
@Slf4j
public class FinPaymentReqController {
	@Autowired
	private IFinPaymentReqService finPaymentReqService;
	@Autowired
	private IFinPaymentReqEntryService finPaymentReqEntryService;

	/**
	 * 分页列表查询
	 *
	 * @param finPaymentReq
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "付款申请单-分页列表查询")
	@ApiOperation(value="付款申请单-分页列表查询", notes="付款申请单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{paymentType}"})
	public Result<IPage<FinPaymentReq>> queryPageList(FinPaymentReq finPaymentReq,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FinPaymentReq> queryWrapper = QueryGenerator.initQueryWrapper(finPaymentReq, req.getParameterMap());
		Page<FinPaymentReq> page = new Page<FinPaymentReq>(pageNo, pageSize);
		IPage<FinPaymentReq> pageList = finPaymentReqService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "付款申请单-通过id查询")
	 @ApiOperation(value="付款申请单-通过id查询", notes="付款申请单-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<FinPaymentReq> queryById(@RequestParam(name="id",required=true) String id) {
		 FinPaymentReq finPaymentReq = finPaymentReqService.getById(id);
		 if(finPaymentReq==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(finPaymentReq);

	 }

	 /**
	  * 通过ids列表查询
	  *
	  */
	 //@AutoLog(value = "付款申请单-通过ids列表查询")
	 @ApiOperation(value="付款申请单-通过ids列表查询", notes="采购订单-通过ids列表查询")
	 @GetMapping(value = "/listByIds")
	 public Result<List<FinPaymentReq>> queryListByIds(@RequestParam(name="ids",required=true) String ids) {
		 List<FinPaymentReq> list = finPaymentReqService.listByIds(Arrays.asList(ids.split(",")));
		 return Result.OK(list);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "付款申请单明细通过主表ID查询")
	 @ApiOperation(value="付款申请单明细主表ID查询", notes="付款申请单明细-通主表ID查询")
	 @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	 public Result<List<FinPaymentReqEntry>> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<FinPaymentReqEntry> finPaymentReqEntryList = finPaymentReqEntryService.selectByMainId(id);
		 return Result.OK(finPaymentReqEntryList);
	 }

	 /**
	  *   新增
	  *
	  * @param finPaymentReqPage
	  * @param action
	  * @return
	  */
	 @AutoLog(value = "付款申请单-新增")
	 @ApiOperation(value="付款申请单-新增", notes="付款申请单-新增")
	 @PostMapping(value = "/add/{action}")
	 public Result<?> add(@RequestBody FinPaymentReqPage finPaymentReqPage, @PathVariable String action) {
		 FinPaymentReq bill = new FinPaymentReq();
		 BeanUtils.copyProperties(finPaymentReqPage, bill);
		 try {
			 if (action.equals("submit")) {
				 finPaymentReqService.submitAdd(bill, finPaymentReqPage.getFinPaymentReqEntryList());
				 return Result.OK("新增提交成功！");
			 } else {
				 finPaymentReqService.saveAdd(bill, finPaymentReqPage.getFinPaymentReqEntryList());
				 return Result.OK("新增保存成功！");
			 }
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *   编辑
	  *
	  * @param finPaymentReqPage
	  * @param action
	  * @return
	  */
	 @AutoLog(value = "付款申请单-编辑")
	 @ApiOperation(value="付款申请单-编辑", notes="付款申请单-编辑")
	 @PutMapping(value = "/edit/{action}")
	 public Result<?> edit(@RequestBody FinPaymentReqPage finPaymentReqPage, @PathVariable String action) {
		 FinPaymentReq bill = new FinPaymentReq();
		 BeanUtils.copyProperties(finPaymentReqPage, bill);
		 try {
			 if (action.equals("submit")) {
				 finPaymentReqService.submitEdit(bill, finPaymentReqPage.getFinPaymentReqEntryList());
				 return Result.OK("编辑提交成功！");
			 } else {
				 finPaymentReqService.saveEdit(bill, finPaymentReqPage.getFinPaymentReqEntryList());
				 return Result.OK("编辑保存成功！");
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
	 @AutoLog(value = "付款申请单-通过id删除")
	 @ApiOperation(value="付款申请单-通过id删除", notes="付款申请单-通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		 try {
			 finPaymentReqService.delete(id);
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
	 @AutoLog(value = "付款申请单-批量删除")
	 @ApiOperation(value="付款申请单-批量删除", notes="付款申请单-批量删除")
	 @DeleteMapping(value = "/deleteBatch")
	 public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		 try {
			 finPaymentReqService.delete(Arrays.asList(ids.split(",")));
			 return Result.OK("批量删除成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-审核")
	 @ApiOperation(value = "付款申请单-审核", notes = "付款申请单-审核")
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-发起审批")
	 @ApiOperation(value="付款申请单-发起审批", notes="付款申请单-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-结束审批")
	 @ApiOperation(value="付款申请单-结束审批", notes="付款申请单-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-执行")
	 @ApiOperation(value="付款申请单-执行", notes="付款申请单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-关闭")
	 @ApiOperation(value="付款申请单-关闭", notes="付款申请单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单--反关闭")
	 @ApiOperation(value="付款申请单--反关闭", notes="付款申请单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.unclose(json.getString("id"));
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
	 @AutoLog(value = "付款申请单-批量关闭")
	 @ApiOperation(value="付款申请单-批量关闭", notes="付款申请单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "付款申请单-批量反关闭")
	 @ApiOperation(value="付款申请单-批量反关闭", notes="付款申请单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "付款申请单-作废")
	 @ApiOperation(value="付款申请单-作废", notes="付款申请单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 finPaymentReqService.voidBill(json.getString("id"));
			 return Result.OK("作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param finPaymentReq
    */
    @RequestMapping(value = {"/exportXls", "/exportXls/{paymentType}"})
    public ModelAndView exportXls(HttpServletRequest request, FinPaymentReq finPaymentReq) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<FinPaymentReq> queryWrapper = QueryGenerator.initQueryWrapper(finPaymentReq, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<FinPaymentReq> queryList = finPaymentReqService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<FinPaymentReq> finPaymentReqList = new ArrayList<FinPaymentReq>();
      if(oConvertUtils.isEmpty(selections)) {
          finPaymentReqList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          finPaymentReqList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<FinPaymentReqPage> pageList = new ArrayList<FinPaymentReqPage>();
      for (FinPaymentReq main : finPaymentReqList) {
          FinPaymentReqPage vo = new FinPaymentReqPage();
          BeanUtils.copyProperties(main, vo);
          List<FinPaymentReqEntry> finPaymentReqEntryList = finPaymentReqEntryService.selectByMainId(main.getId());
          vo.setFinPaymentReqEntryList(finPaymentReqEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "付款申请单列表");
      mv.addObject(NormalExcelConstants.CLASS, FinPaymentReqPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("付款申请单数据", "导出人:"+sysUser.getRealname(), "付款申请单"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @return
    */
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
              List<FinPaymentReqPage> list = ExcelImportUtil.importExcel(file.getInputStream(), FinPaymentReqPage.class, params);
			  Map<FinPaymentReq, List<FinPaymentReqEntry>> billMap = new HashMap<>();
              for (FinPaymentReqPage page : list) {
                  FinPaymentReq bill = new FinPaymentReq();
                  BeanUtils.copyProperties(page, bill);
				  if (paymentType != null && !bill.getPaymentType().equals(paymentType)) {
					  throw new JeecgBootException(bill.getBillNo() + "：“付款类型”不符！");
				  }
				  billMap.put(bill, page.getFinPaymentReqEntryList());
              }
			  finPaymentReqService.saveAddBatch(billMap);

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
