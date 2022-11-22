package io.finer.erp.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.base.entity.BasSequence;
import io.finer.erp.base.mapper.BasSequenceMapper;
import io.finer.erp.base.service.IBasSequenceService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: bas_sequence
 * @Author: jeecg-boot
 * @Date: 2020-03-20
 * @Version: V1.0
 */
@Service
@Component
public class BasSequenceServiceImpl extends ServiceImpl<BasSequenceMapper, BasSequence> implements IBasSequenceService {

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE ,rollbackFor = Exception.class)
    public int nextSequence(String k) {
        int result = 1;
        BasSequence bs = this.getById(k);
        if (bs == null) {
            bs = new BasSequence();
            bs.setK(k);
            bs.setV(1);
            this.save(bs);
        } else {
            result = bs.getV() + 1;
            bs.setV(result);
            this.updateById(bs);
        }
        return result;
    }
}
