package io.finer.erp.biz.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 月度结账
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface CloseAccountsMapper {
    List<Map<String, Object>> selectNotEffectiveBill(@Param("year") Integer year, @Param("month") Integer month);
}
