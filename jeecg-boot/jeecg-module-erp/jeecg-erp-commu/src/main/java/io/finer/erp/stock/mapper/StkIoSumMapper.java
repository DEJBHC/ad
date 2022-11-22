package io.finer.erp.stock.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.finer.erp.stock.entity.StkIoSum;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

/**
 * @Description: 出入库汇总
 * @Author: jeecg-boot
 * @Date:   2022-09-28
 * @Version: V1.0
 */
public interface StkIoSumMapper extends BaseMapper<StkIoSum> {

    @InterceptorIgnore(tenantLine = "true")
    @Update("{call sp_stk_io_sum(#{year,jdbcType=SMALLINT,mode=IN},#{month,jdbcType=TINYINT,mode=IN})}")
    @Options(statementType = StatementType.CALLABLE)
    void sum(Integer year, Integer month);
}
