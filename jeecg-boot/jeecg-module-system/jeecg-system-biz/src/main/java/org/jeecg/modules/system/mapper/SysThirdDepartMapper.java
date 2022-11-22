package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysThirdDepart;

/**
 * @Description: 第三方部门映射表
 * @Author: cfm
 * @Date: 2021-12-20
 * @Version: V1.0
 */
public interface SysThirdDepartMapper extends BaseMapper<SysThirdDepart> {
//    @Select("select third_depart_id from sys_third_depart where sys_depart_id = #{sysDepartId} and third_type = #{thirdType}")
//    String selectThirdId(@Param("sysDepartId") String sysDepartId, @Param("thirdType") String thirdType);
//
//    @Select("select sys_depart_id from sys_third_depart where third_depart_id = #{thirdDepartId} and third_type = #{thirdType}")
//    String selectSysId(@Param("thirdDepartId") String thirdDepartId, @Param("thirdType") String thirdType);
}
