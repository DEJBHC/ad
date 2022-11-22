package io.finer.erp.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.finance.entity.FinPayableCheck;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.service.IFinPayableCheckEntryService;
import io.finer.erp.finance.service.IFinPayableCheckService;
import io.finer.erp.finance.vo.FinPayableCheckPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
* @Description: 应付核销单
* @Author:
* @Date:
* @Version: V1.0
*/
@Api(tags="应付核销单")
@RestController
@RequestMapping("/finance/finPayableCheck")
@Slf4j
public class FinPayableCheckController {
   @Autowired
   private IFinPayableCheckService finPayableCheckService;
    @Autowired
    private IFinPayableCheckEntryService finPayableCheckEntryService;

   /**
    * 分页列表查询
    *
    * @param finPayableCheck
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "应付核销单-分页列表查询")
   @ApiOperation(value="应付核销单-分页列表查询", notes="应付核销单-分页列表查询")
   @GetMapping(value = {"/list", "/list/{payableCheckType}"})  //payableCheckType会传至finPayableCheck.payableCheckType
   public Result<?> queryPageList(FinPayableCheck finPayableCheck,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<FinPayableCheck> queryWrapper = QueryGenerator.initQueryWrapper(finPayableCheck, req.getParameterMap());
       Page<FinPayableCheck> page = new Page<FinPayableCheck>(pageNo, pageSize);
       IPage<FinPayableCheck> pageList = finPayableCheckService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "应付核销单-通过id查询")
    @ApiOperation(value="应付核销单-通过id查询", notes="应付核销单-通过id查询")
    @GetMapping(value = {"/queryById", "/queryById/dictText"})
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        FinPayableCheck finPayableCheck = finPayableCheckService.getById(id);
        if(finPayableCheck==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(finPayableCheck);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "往来核销明细集合-通过id查询")
    @ApiOperation(value="往来核销明细集合-通过id查询", notes="往来核销明细-通过id查询")
    @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
    public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id,
                                            @RequestParam(name="checkSide",required = false) String checkSide) {
        List<FinPayableCheckEntry> finPayableCheckEntryList = finPayableCheckEntryService.selectByMainId(id);
        if (StringUtils.isEmpty(checkSide)) {
            return Result.OK(finPayableCheckEntryList);
        }

        List<FinPayableCheckEntry> list = new ArrayList<>();
        for(FinPayableCheckEntry entry: finPayableCheckEntryList) {
            if (entry.getCheckSide().equals(checkSide)) {
                list.add(entry);
            }
        }
        return Result.ok(list);
    }

   /**
    *   新增
    *
    * @param finPayableCheckPage
    * @return
    */
   @AutoLog(value = "应付核销单-新增")
   @ApiOperation(value="应付核销单-新增", notes="应付核销单-新增")
   @PostMapping(value = "/add/{action}")
   public Result<?> add(@RequestBody FinPayableCheckPage finPayableCheckPage, @PathVariable String action) {
       FinPayableCheck bill = new FinPayableCheck();
       BeanUtils.copyProperties(finPayableCheckPage, bill);
       try {
           if (action.equals("submit")) {
               finPayableCheckService.submitAdd(bill, finPayableCheckPage.getFinPayableCheckEntryList());
               return Result.OK("新增提交成功！");
           } else {
               finPayableCheckService.saveAdd(bill, finPayableCheckPage.getFinPayableCheckEntryList());
               return Result.OK("新增保存成功！");
           }
       } catch (Exception e) {
           return Result.error(e.getMessage());
       }
   }

   /**
    *  编辑
    *
    * @param finPayableCheckPage
    * @return
    */
   @AutoLog(value = "应付核销单-编辑")
   @ApiOperation(value="应付核销单-编辑", notes="应付核销单-编辑")
   @PutMapping(value = "/edit/{action}")
   public Result<?> edit(@RequestBody FinPayableCheckPage finPayableCheckPage, @PathVariable String action) {
       FinPayableCheck bill = new FinPayableCheck();
       BeanUtils.copyProperties(finPayableCheckPage, bill);
       try {
           if (action.equals("submit")) {
               finPayableCheckService.submitEdit(bill, finPayableCheckPage.getFinPayableCheckEntryList());
               return Result.OK("编辑提交成功！");
           } else {
               finPayableCheckService.saveEdit(bill, finPayableCheckPage.getFinPayableCheckEntryList());
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
   @AutoLog(value = "应付核销单-通过id删除")
   @ApiOperation(value="应付核销单-通过id删除", notes="应付核销单-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           finPayableCheckService.delete(id);
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
   @AutoLog(value = "应付核销单-批量删除")
   @ApiOperation(value="应付核销单-批量删除", notes="应付核销单-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       try {
           this.finPayableCheckService.delete(Arrays.asList(ids.split(",")));
           return Result.OK("批量删除成功！");
       } catch (Exception e) {
           return Result.error(e.getMessage());
       }
   }

    @AutoLog(value = "应付核销单-审核")
    @ApiOperation(value="应付核销单-审核", notes="应付核销单-审核")
    @PutMapping(value = "/check")
    public Result<?> check(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.check(json.getString("id"),
                    json.getString("approvalResultType"),
                    json.getString("approvalRemark"));
            return Result.OK( "审核提交成功!");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单-发起审批")
    @ApiOperation(value="应付核销单-发起审批", notes="应付核销单-发起审批")
    @PutMapping(value = "/bpm/start")
    public Result<?> startBpmInstance(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.startBpmInstance(json.getString("id"));
            return Result.OK("发起审批成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单-结束审批")
    @ApiOperation(value="应付核销单-结束审批", notes="应付核销单-结束审批")
    @PutMapping(value = "/bpm/end")
    public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.bpmInstanceEnd(json.getString("id"),
                    json.getString("approvalResultType"),
                    json.getString("approvalRemark"));
            return Result.OK("结束审批成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单-执行")
    @ApiOperation(value="应付核销单-执行", notes="应付核销单-执行")
    @PutMapping(value = "/execute")
    public Result<?> execute(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.execute(json.getString("id"));
            return Result.OK("单据执行成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单-关闭")
    @ApiOperation(value="应付核销单-关闭", notes="应付核销单-关闭")
    @PutMapping(value = "/close")
    public Result<?> close(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.close(json.getString("id"));
            return Result.OK("关闭成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单--反关闭")
    @ApiOperation(value="应付核销单--反关闭", notes="应付核销单--反关闭")
    @PutMapping(value = "/unclose")
    public Result<?> unclose(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.unclose(json.getString("id"));
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
    @AutoLog(value = "应付核销单-批量关闭")
    @ApiOperation(value="应付核销单-批量关闭", notes="应付核销单-批量关闭")
    @PutMapping(value = "/closeBatch")
    public Result<String> closeBatch(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.close(Arrays.asList(json.getString("ids").split(",")));
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
    @AutoLog(value = "应付核销单-批量反关闭")
    @ApiOperation(value="应付核销单-批量反关闭", notes="应付核销单-批量反关闭")
    @PutMapping(value = "/uncloseBatch")
    public Result<String> uncloseBatch(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.unclose(Arrays.asList(json.getString("ids").split(",")));
            return Result.OK("批量反关闭成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @AutoLog(value = "应付核销单-作废")
    @ApiOperation(value="应付核销单-作废", notes="应付核销单-作废")
    @PutMapping(value = "/void")
    public Result<?> voidBill(@RequestBody JSONObject json) {
        try {
            finPayableCheckService.voidBill(json.getString("id"));
            return Result.OK("单据作废成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
   * 导出为excel
   *
   * @param request
   * @param finPayableCheck
   */
   @AutoLog(value = "导出为excel")
   @RequestMapping(value = {"/exportXls", "/exportXls/{payableCheckType}"})
   public ModelAndView exportXls(HttpServletRequest request, FinPayableCheck finPayableCheck) {
     // Step.1 组装查询条件查询数据
     QueryWrapper<FinPayableCheck> queryWrapper = QueryGenerator.initQueryWrapper(finPayableCheck, request.getParameterMap());
     LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

     //Step.2 获取导出数据
     List<FinPayableCheck> queryList = finPayableCheckService.list(queryWrapper);
     // 过滤选中数据
     String selections = request.getParameter("selections");
     List<FinPayableCheck> finPayableCheckList = new ArrayList<FinPayableCheck>();
     if(oConvertUtils.isEmpty(selections)) {
         finPayableCheckList = queryList;
     }else {
         List<String> selectionList = Arrays.asList(selections.split(","));
         finPayableCheckList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
     }

     // Step.3 组装pageList
     List<FinPayableCheckPage> pageList = new ArrayList<FinPayableCheckPage>();
     for (FinPayableCheck main : finPayableCheckList) {
         FinPayableCheckPage vo = new FinPayableCheckPage();
         BeanUtils.copyProperties(main, vo);
         List<FinPayableCheckEntry> finPayableCheckEntryList = finPayableCheckEntryService.selectByMainId(main.getId());
         vo.setFinPayableCheckEntryList(finPayableCheckEntryList);
         pageList.add(vo);
     }

     // Step.4 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     mv.addObject(NormalExcelConstants.FILE_NAME, "应付核销单列表");
     mv.addObject(NormalExcelConstants.CLASS, FinPayableCheckPage.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应付核销单数据", "导出人:"+sysUser.getRealname(), "应付核销单"));
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
   @RequestMapping(value = {"/importExcel", "/importExcel/{payableCheckType}"}, method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request,
                                @PathVariable(required = false) String payableCheckType) {
     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
     Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
     for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
         MultipartFile file = entity.getValue();// 获取上传文件对象
         ImportParams params = new ImportParams();
         params.setTitleRows(2);
         params.setHeadRows(1);
         params.setNeedSave(true);
         try {
             List<FinPayableCheckPage> list = ExcelImportUtil.importExcel(file.getInputStream(), FinPayableCheckPage.class, params);

             Map<FinPayableCheck, List<FinPayableCheckEntry>> billMap = new HashMap<>();
             for (FinPayableCheckPage page : list) {
                 FinPayableCheck bill = new FinPayableCheck();
                 BeanUtils.copyProperties(page, bill);
                 if (payableCheckType != null && !bill.getPayableCheckType().equals(payableCheckType)) {
                     throw new JeecgBootException( bill.getBillNo() + "：应付核销类型不符！");
                 }
                 billMap.put(bill, page.getFinPayableCheckEntryList());
             }
             finPayableCheckService.saveAddBatch(billMap);//20211204 cfm：事务化

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
