package io.finer.erp.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.finer.erp.base.entity.BasUnit;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 计量单位
 * @Author: jeecg-boot
 * @Date:   2020-03-27
 * @Version: V1.0
 */
public interface IBasUnitService extends IService<BasUnit> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addBasUnit(BasUnit basUnit);

	/**修改节点*/
	void updateBasUnit(BasUnit basUnit) throws JeecgBootException;

	/**删除节点*/
	void deleteBasUnit(String id) throws JeecgBootException;

	/**查询所有数据，无分页*/
	List<BasUnit> queryTreeListNoPage(QueryWrapper<BasUnit> queryWrapper);

	List<BasUnit> selectConvertibleById(String id);
	BigDecimal getRate(String fromId, String toId);
	BigDecimal convert(BigDecimal decimal, String fromId, String toId);
}
