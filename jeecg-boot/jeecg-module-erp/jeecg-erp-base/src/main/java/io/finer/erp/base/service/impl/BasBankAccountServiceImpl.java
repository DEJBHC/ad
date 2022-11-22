package io.finer.erp.base.service.impl;

import io.finer.erp.base.entity.BasBankAccount;
import io.finer.erp.base.mapper.BasBankAccountMapper;
import io.finer.erp.base.service.IBasBankAccountService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 银行账户
 * @Author: jeecg-boot
 * @Date:   2020-04-14
 * @Version: V1.0
 */
@Service
public class BasBankAccountServiceImpl extends ServiceImpl<BasBankAccountMapper, BasBankAccount> implements IBasBankAccountService {

}
