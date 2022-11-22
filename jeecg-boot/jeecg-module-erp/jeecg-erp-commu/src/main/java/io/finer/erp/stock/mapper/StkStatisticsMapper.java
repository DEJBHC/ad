package io.finer.erp.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import io.finer.erp.common.mapper.EntryMapper;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.vo.StkIoDaySumWmbVo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 统计查询
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface StkStatisticsMapper extends EntryMapper<StkIoEntry> {

    IPage<StkIoDaySumWmbVo> selectIoDaySumWmb(IPage<StkIoDaySumWmbVo> page,
                                              @Param(Constants.WRAPPER) Wrapper<StkIoDaySumWmbVo> query,
                                              @Param("params") Map params);
    List<StkIoDaySumWmbVo> selectIoDaySumWmb(@Param(Constants.WRAPPER) Wrapper<StkIoDaySumWmbVo> query,
                                             @Param("params") Map params);
}
