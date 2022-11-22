package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysThirdDepart;

/**
 * @Description: 第三方部门映射表
 * @Author: cfm
 * @Date:   2021-12-20
 * @Version: V1.0
 */
public interface ISysThirdDepartService extends IService<SysThirdDepart> {
    SysThirdDepart getOneBySysDepartId(String sysDepartId, String thirdType);
    SysThirdDepart getOneByThirdDepartId(String thirdDepartId, String thirdType);
    Boolean removeByThirdType(String thirdType);
}
