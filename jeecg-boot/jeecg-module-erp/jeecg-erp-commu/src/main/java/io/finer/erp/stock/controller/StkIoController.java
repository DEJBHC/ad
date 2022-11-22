package io.finer.erp.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.service.IStkIoEntryService;
import io.finer.erp.stock.service.IStkIoService;
import io.finer.erp.stock.vo.StkIoPage;
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
import org.jetbrains.annotations.NotNull;
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
 * @Description: 出入库单
 * @Author: jeecg-boot
 * @Date:   2020-03-31
 * @Version: V1.0
 */
@Api(tags="出入库单")
@RestController
@RequestMapping("/stock/stkIo")
@Slf4j
public class StkIoController {
	@Autowired
	private IStkIoService stkIoService;
	@Autowired
	private IStkIoEntryService stkIoEntryService;

	/**
	 * 分页列表查询
	 *
	 * @param stkIo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "出入库单-分页列表查询")
	@ApiOperation(value="出入库单-分页列表查询", notes="出入库单-分页列表查询")
	@GetMapping(value = {"/list", "/list/{stockIoType}", "/list/{stockIoType}/{isRubric}"})  //stockIoType、isRubric会传至stkIo.stockIoType
	public Result<?> queryPageList(StkIo stkIo,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		QueryWrapper<StkIo> queryWrapper = QueryGenerator.initQueryWrapper(stkIo, req.getParameterMap());
		Page<StkIo> page = new Page<StkIo>(pageNo, pageSize);
		IPage<StkIo> pageList = stkIoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "出入库单-通过id查询")
	@ApiOperation(value="出入库单-通过id查询", notes="出入库单-通过id查询")
	@GetMapping(value = {"/queryById", "/queryById/dictText"})
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
	 	StkIo stkIo = stkIoService.getById(id);
	 	if(stkIo ==null) {
			return Result.error("未找到对应数据");
		 }
		 return Result.OK(stkIo);
	}

	 /**
	  * 通过ids列表查询
	  *
	  */
	 //@AutoLog(value = "出入库单-通过ids列表查询")
	 @ApiOperation(value="出入库单-通过ids列表查询", notes="出入库单-通过ids列表查询")
	 @GetMapping(value = "/listByIds")
	 public Result<List<StkIo>> queryListByIds(@RequestParam(name="ids",required=true) String ids) {
		 List<StkIo> list = stkIoService.listByIds(Arrays.asList(ids.split(",")));
		 return Result.OK(list);
	 }

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "明细集合-通过id查询")
	@ApiOperation(value="明细集合-通过id查询", notes="明细-通过id查询")
	@GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		List<StkIoEntry> stkIoEntryList = stkIoEntryService.selectByMainId(id);
		return Result.OK(stkIoEntryList);
	}

	//@AutoLog(value = "明细集合-通过ids查询")
	@ApiOperation(value="明细集合-通过ids查询", notes="明细-通过ids查询")
	@GetMapping(value = "/queryEntryByMainIds")
	public Result<?> queryEntryListByMainIds(@NotNull @RequestParam(name="ids",required=true) String ids) {
	 	List<StkIoEntry> stkIoEntryList = null;
		for(String id: ids.split(",")) {
			List<StkIoEntry> list = stkIoEntryService.selectByMainId(id);
			if (stkIoEntryList == null) {
				stkIoEntryList = list;
			} else {
				stkIoEntryList.addAll(list);
			}
		}
		return Result.OK(stkIoEntryList);
	}

	//@AutoLog(value = "明细集合-通过id查询")
	@ApiOperation(value="明细集合-通过id查询", notes="明细-通过id查询")
	@GetMapping(value = "/queryEditingEntryByMainId")
	public Result<?> queryEditingEntryListByMainId(@RequestParam(name="id",required=true) String id) {
	 List<StkIoEntry> stkIoEntryList = stkIoEntryService.selectEditingByMainId(id);
	 return Result.OK(stkIoEntryList);
	}

	/**
	*   新增
	*
	* @param stkIoPage
	* @return
	*/
	@AutoLog(value = "出入库单-新增")
	@ApiOperation(value="出入库单-新增", notes="出入库单-新增")
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody StkIoPage stkIoPage, @PathVariable String action) {
		StkIo bill = new StkIo();
		BeanUtils.copyProperties(stkIoPage, bill);
		try {
			if (action.equals("submit")) {
				stkIoService.submitAdd(bill, stkIoPage.getStkIoEntryList());
				return Result.OK("新增提交成功！");
			} else {
				stkIoService.saveAdd(bill, stkIoPage.getStkIoEntryList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param stkIoPage
	 * @return
	 */
	@AutoLog(value = "出入库单-编辑")
	@ApiOperation(value="出入库单-编辑", notes="出入库单-编辑")
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody StkIoPage stkIoPage, @PathVariable String action) {
		StkIo bill = new StkIo();
		BeanUtils.copyProperties(stkIoPage, bill);
		try {
			if (action.equals("submit")) {
				stkIoService.submitEdit(bill, stkIoPage.getStkIoEntryList());
				return Result.OK("编辑提交成功!");
			} else {
				stkIoService.saveEdit(bill, stkIoPage.getStkIoEntryList());
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
	@AutoLog(value = "出入库单-通过id删除")
	@ApiOperation(value="出入库单-通过id删除", notes="出入库单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			stkIoService.delete(id);
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
	@AutoLog(value = "出入库单-批量删除")
	@ApiOperation(value="出入库单-批量删除", notes="出入库单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.stkIoService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

    @AutoLog(value = "出入库单-审核")
	@ApiOperation(value="出入库单-审核", notes="出入库单-审核")
	@PutMapping(value = "/check")
	public Result<?> check(@RequestBody JSONObject json) {
    	try {
			stkIoService.check(json.getString("id"),
					json.getString("approvalResultType"),
					json.getString("approvalRemark"));
			return Result.OK( "审核提交成功!");
		} catch (Exception e) {
    		return Result.error(e.getMessage());
		}
    }

	 @AutoLog(value = "出入库单-发起审批")
	 @ApiOperation(value="出入库单-发起审批", notes="出入库单-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 stkIoService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-结束审批")
	 @ApiOperation(value="出入库单-结束审批", notes="出入库单-结束审批")
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 stkIoService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-执行")
	 @ApiOperation(value="出入库单-执行", notes="出入库单-执行")
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 stkIoService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-关闭")
	 @ApiOperation(value="出入库单-关闭", notes="出入库单-关闭")
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 stkIoService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单--反关闭")
	 @ApiOperation(value="出入库单--反关闭", notes="出入库单--反关闭")
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 stkIoService.unclose(json.getString("id"));
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
	 @AutoLog(value = "出入库单-批量关闭")
	 @ApiOperation(value="出入库单-批量关闭", notes="出入库单-批量关闭")
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 stkIoService.close(Arrays.asList(json.getString("ids").split(",")));
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
	 @AutoLog(value = "出入库单-批量反关闭")
	 @ApiOperation(value="出入库单-批量反关闭", notes="出入库单-批量反关闭")
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 stkIoService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "出入库单-作废")
	 @ApiOperation(value="出入库单-作废", notes="出入库单-作废")
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 stkIoService.voidBill(json.getString("id"));
			 return Result.OK("作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  * 导出excel
	  *
	  * @param request
	  * @param stkIo
	  */
	 @AutoLog(value = "导出为excel")
	 @RequestMapping(value = {"/exportXls", "/exportXls/{stockIoType}", "/exportXls/{stockIoType}/{isRubric}"})  //stockIoType会传至stkIo.stockIoType
	 public ModelAndView exportXls(HttpServletRequest request, StkIo stkIo) {
		 // Step.1 组装查询条件查询数据
		 QueryWrapper<StkIo> queryWrapper = QueryGenerator.initQueryWrapper(stkIo, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 //Step.2 获取导出数据
		 List<StkIo> queryList = stkIoService.list(queryWrapper);
		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 List<StkIo> stkIoList = new ArrayList<StkIo>();
		 if(oConvertUtils.isEmpty(selections)) {
			 stkIoList = queryList;
		 }else {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 stkIoList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 }

		 // Step.3 组装pageList
		 List<StkIoPage> pageList = new ArrayList<StkIoPage>();
		 for (StkIo main : stkIoList) {
			 StkIoPage vo = new StkIoPage();
			 BeanUtils.copyProperties(main, vo);
			 List<StkIoEntry> stkIoEntryList = stkIoEntryService.selectByMainId(main.getId());
			 vo.setStkIoEntryList(stkIoEntryList);
			 pageList.add(vo);
		 }

		 // Step.4 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 mv.addObject(NormalExcelConstants.FILE_NAME, "出入库单列表");
		 mv.addObject(NormalExcelConstants.CLASS, StkIoPage.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出入库单数据", "导出人:"+sysUser.getRealname(), "出入库单"));
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
	 @RequestMapping(value = {"/importExcel", "/importExcel/{stockIoType}", "/importExcel/{stockIoType}/{isRubric}"}, method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request,
								  @PathVariable(required = false) String stockIoType,
								  @PathVariable(required = false) Integer isRubric) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<StkIoPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StkIoPage.class, params);

				 Map<StkIo, List<StkIoEntry>> billMap = new HashMap<>();
				 for (StkIoPage page : list) {
					 StkIo bill = new StkIo();
					 BeanUtils.copyProperties(page, bill);
					 if (stockIoType != null && !bill.getStockIoType().equals(stockIoType) ||
							 isRubric != null && !bill.getIsRubric().equals(isRubric)) {
						 throw new JeecgBootException(bill.getBillNo() + "：“出入库类型”或“是否红字”不符！");
					 }
					 billMap.put(bill, page.getStkIoEntryList());
				 }
				 stkIoService.saveAddBatch(billMap);//20211204 cfm：事务化

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
