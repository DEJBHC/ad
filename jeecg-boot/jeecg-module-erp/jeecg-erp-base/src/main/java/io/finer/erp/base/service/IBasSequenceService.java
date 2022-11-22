package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasSequence;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: bas_sequence
 * @Author: jeecg-boot
 * @Date: 2020-03-20
 * @Version: V1.0
 */
public interface IBasSequenceService extends IService<BasSequence> {
    @Transactional(isolation = Isolation.SERIALIZABLE ,rollbackFor = Exception.class)
    int nextSequence(String k);
}
