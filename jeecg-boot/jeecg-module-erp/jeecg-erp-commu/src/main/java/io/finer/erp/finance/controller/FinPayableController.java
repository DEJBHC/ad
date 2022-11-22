package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinPayable;
import io.finer.erp.finance.service.IFinPayableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 应付单
 * @Author: jeecg-boot
 * @Date:   2020-04-13
 * @Version: V1.0
 */
@Api(tags="应付单")
@RestController
@RequestMapping("/finance/finPayable")
@Slf4j
public class FinPayableController extends JeecgController<FinPayable, IFinPayableService> {
	@Autowired
	private IFinPayableService finPayableService;

	/**
	 * 分页列表查询
	 *
	 * @param finPayable
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "应付单-分页列表查询")
	@ApiOperation(value="应付单-分页列表查询", notes="应付单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{payableType}"})  //payableType会传至finPayable.paymentType
	public Result<?> queryPageList(FinPayable finPayable,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FinPayable> queryWrapper = QueryGenerator.initQueryWrapper(finPayable, req.getParameterMap());
		Page<FinPayable> page = new Page<FinPayable>(pageNo, pageSize);
		IPage<FinPayable> pageList = finPayableService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @GetMapping(value = "/checkableList")
	 public Result<?> queryCheckablePageList(FinPayable finPayable,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<FinPayable> queryWrapper = QueryGenerator.initQueryWrapper(finPayable, req.getParameterMap());
		 queryWrapper.eq("is_effective", 1);
		 queryWrapper.eq("is_voided", 0);
		 queryWrapper.apply("amt - checked_amt > 0");
		 Page<FinPayable> page = new Page<FinPayable>(pageNo, pageSize);
		 IPage<FinPayable> pageList = finPayableService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "应付单-通过id查询")
	 @ApiOperation(value="应付单-通过id查询", notes="应付单-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 FinPayable finPayable = finPayableService.getById(id);
		 if(finPayable==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(finPayable);
	 }

	 /**
	 *   新增
	 *
	 * @param finPayable
	 * @return
	 */
	@AutoLog(value = "应付单-新增")
	@ApiOperation(value="应付单-新增", notes="应付单-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody FinPayable finPayable, @PathVariable String action) throws Exception {
		try {
			if (action.equals("submit")) {
				finPayableService.submitAdd(finPayable);
				return Result.OK("新增提交成功！");
			} else {
				finPayableService.saveAdd(finPayable);
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param finPayable
	 * @return
	 */
	@AutoLog(value = "应付单-编辑")
	@ApiOperation(value="应付单-编辑", notes="应付单-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody FinPayable finPayable, @PathVariable String action) throws Exception {
		try {
			if (action.equals("submit")) {
				finPayableService.submitEdit(finPayable);
				return Result.OK("编辑提交成功!");
			} else {
				finPayableService.saveEdit(finPayable);
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
	@AutoLog(value = "应付单-通过id删除")
	@ApiOperation(value="应付单-通过id删除", notes="应付单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			finPayableService.delete(id);
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
	@AutoLog(value = "应付单-批量删除")
	@ApiOperation(value="应付单-批量删除", notes="应付单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.finPayableService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@AutoLog(value = "应付单-审核")
	@ApiOperation(value="应付单-审核", notes="应付单-审核")
	@PutMapping(value = "/check")
	public Result<?> check(@RequestBody JSONObject json) {
		try {
			finPayableService.check(json.getString("id"),
					json.getString("approvalResultType"),
					json.getString("approvalRemark"));
			return Result.OK( "审核提交成功!");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "应付单-发起审批")
	 @ApiOperation(value="应付单-发起审批", notes="应付单-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 finPayableService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "应付单-结束审批")
	 @ApiOperation(value="应付单-结束审批", notes="应付单-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 finPayableService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "应付单-执行")
	 @ApiOperation(value="应付单-执行", notes="应付单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 finPayableService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "应付单-关闭")
	 @ApiOperation(value="应付单-关闭", notes="应付单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 finPayableService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "应付单--反关闭")
	 @ApiOperation(value="应付单--反关闭", notes="应付单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 finPayableService.unclose(json.getString("id"));
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
	 @AutoLog(value = "应付单-批量关闭")
	 @ApiOperation(value="应付单-批量关闭", notes="应付单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 finPayableService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "应付单-批量反关闭")
	 @ApiOperation(value="应付单-批量反关闭", notes="应付单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 finPayableService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "应付单-作废")
	 @ApiOperation(value="应付单-作废", notes="应付单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 finPayableService.voidBill(json.getString("id"));
			 return Result.OK("单据作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  * 导出为excel
	  *
	  * @param request
	  * @param finPayable
	  */
	 @AutoLog(value = "导出为excel")
	 @RequestMapping(value = {"/exportXls", "/exportXls/{payableType}"})  //payableType会传至finPayable.payableType
	 public ModelAndView exportXls(HttpServletRequest request, FinPayable finPayable) {
		 return super.exportXls(request, finPayable, FinPayable.class, "应付单");
	 }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @return
	  */
	 @AutoLog(value = "通过excel导入数据")
	 @RequestMapping(value = {"/importExcel", "/importExcel/{payableType}"}, method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, @PathVariable(required = false) String payableType) {
		 //20211204 cfm modi: 不用jeecg-boot生成代码，以进行数据合法性检查和事务化
		 //return super.importExcel(request, response, FinPayable.class);

		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<FinPayable> list = ExcelImportUtil.importExcel(file.getInputStream(), FinPayable.class, params);

				 for (FinPayable bill : list) {
					 if (payableType != null && !bill.getPayableType().equals(payableType)) {
						 throw new JeecgBootException(bill.getBillNo() + "：应付类型不符！");
					 }
				 }
				 finPayableService.saveAddBatch(list);//20211204 cfm：事务化

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
