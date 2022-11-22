package io.finer.erp.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.mapper.StkStatisticsMapper;
import io.finer.erp.stock.vo.StkIoDaySumWmbVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* @Description: 出入库统计
* @Author:
* @Date:
* @Version: V1.0
*/
@Api(tags="出入库统计")
@RestController
@RequestMapping("/stock/stkStatistics")
@Slf4j
public class StkStatisticsController {
   @Autowired
   private StkStatisticsMapper statisticsMapper;

   @GetMapping(value = "/day/sum/wmb/list")
   public Result<?> queryDaySumWmbPageList(HttpServletRequest req, StkIoDaySumWmbVo vo,
                                           @RequestParam(name="beginDate", defaultValue="") String beginDate,
                                           @RequestParam(name="endDate", defaultValue="") String endDate,
                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) throws ParseException {
       if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
           return Result.error("请输入起始日期和截止日期！");
       }
       if (beginDate.compareTo(endDate) > 0) {
           return Result.error("起始日期不能大于截止日期！");
       }
       SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
       Date date1 = sdf.parse(beginDate);
       Date date2 = sdf.parse(endDate);
       long day = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
       if (day > 31) {
           return Result.error("起止日期不能超过31天！");
       }

       QueryWrapper<StkIoDaySumWmbVo> queryWrapper = QueryGenerator.initQueryWrapper(vo, req.getParameterMap());
       Page<StkIoDaySumWmbVo> page = new Page<StkIoDaySumWmbVo>(pageNo, pageSize);
       Map<String, Object> params = new HashMap<>();
       params.put("beginDate", beginDate);
       params.put("endDate", endDate);
       IPage<StkIoDaySumWmbVo> pageList = statisticsMapper.selectIoDaySumWmb(page, queryWrapper, params);
       return Result.OK(pageList);
   }

    @RequestMapping(value = "/day/sum/wmb/exportXls")
    public ModelAndView exportDaySumWmbXls(HttpServletRequest req, StkIoDaySumWmbVo vo,
                                           @RequestParam(name="beginDate", defaultValue="") String beginDate,
                                           @RequestParam(name="endDate", defaultValue="") String endDate) {
        // Step.1 组装查询条件查询数据
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<StkIoDaySumWmbVo> queryWrapper = QueryGenerator.initQueryWrapper(vo, req.getParameterMap());
        Map<String, Object> params = new HashMap<>();
        params.put("beginDate", beginDate);
        params.put("endDate", endDate);

        // Step.2 获取导出数据
        List<StkIoDaySumWmbVo> list = statisticsMapper.selectIoDaySumWmb(queryWrapper, params);
        String selections = req.getParameter("selections");
        if(!oConvertUtils.isEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            list = list.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "出入库日汇总");
        mv.addObject(NormalExcelConstants.CLASS, StkIoDaySumWmbVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("出入库日汇总", "导出人:"+sysUser.getRealname(), "出入库日汇总"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
   }

}
