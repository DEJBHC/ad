package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.entity.SysThirdDepart;
import org.jeecg.modules.system.mapper.SysThirdDepartMapper;
import org.jeecg.modules.system.service.ISysThirdDepartService;
import org.springframework.stereotype.Service;

/**
 * @Description: 第三方部门映射表
 * @Author: cfm
 * @Date:   2021-12-20
 * @Version: V1.0
 */
@Service
public class SysThirdDepartServiceImpl extends ServiceImpl<SysThirdDepartMapper, SysThirdDepart> implements ISysThirdDepartService {
    @Override
    public SysThirdDepart getOneBySysDepartId(String sysDepartId, String thirdType) {
        LambdaQueryWrapper<SysThirdDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysThirdDepart::getSysDepartId, sysDepartId);
        queryWrapper.eq(SysThirdDepart::getThirdType, thirdType);
        return super.getOne(queryWrapper);
    }

    @Override
    public SysThirdDepart getOneByThirdDepartId(String thirdDepartId, String thirdType) {
        LambdaQueryWrapper<SysThirdDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysThirdDepart::getThirdDepartId, thirdDepartId);
        queryWrapper.eq(SysThirdDepart::getThirdType, thirdType);
        return super.getOne(queryWrapper);
    }

    @Override
    public Boolean removeByThirdType(String thirdType) {
        LambdaQueryWrapper<SysThirdDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysThirdDepart::getThirdType, thirdType);
        return remove(queryWrapper);
    }
}
