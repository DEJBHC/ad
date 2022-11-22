package io.finer.erp.finance.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.finer.erp.finance.entity.FinPayableSum;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

/**
 * @Description: fin_payable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface FinPayableSumMapper extends BaseMapper<FinPayableSum> {

    @InterceptorIgnore(tenantLine = "true")
    @Update("{call sp_fin_payable_sum(#{year,jdbcType=SMALLINT,mode=IN},#{month,jdbcType=TINYINT,mode=IN})}")
    @Options(statementType = StatementType.CALLABLE)
    void sum(Integer year, Integer month);
}
