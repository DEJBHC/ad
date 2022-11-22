package io.finer.erp.finance.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.finer.erp.finance.entity.FinReceivableSum;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

/**
 * @Description: fin_receivable_bal
 * @Author: jeecg-boot
 * @Date:   2020-05-25
 * @Version: V1.0
 */
public interface FinReceivableSumMapper extends BaseMapper<FinReceivableSum> {

    @InterceptorIgnore(tenantLine = "true")
    @Update("{call sp_fin_receivable_sum(#{year,jdbcType=SMALLINT,mode=IN},#{month,jdbcType=TINYINT,mode=IN})}")
    @Options(statementType = StatementType.CALLABLE)
    void sum(Integer year, Integer month);
}
