package io.finer.erp.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.finer.erp.base.entity.BasWarehouse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;

import java.util.List;

/**
 * @Description: 仓库
 * @Author: jeecg-boot
 * @Date:   2020-04-01
 * @Version: V1.0
 */
public interface IBasWarehouseService extends IService<BasWarehouse> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addBasWarehouse(BasWarehouse basWarehouse);

	/**修改节点*/
	void updateBasWarehouse(BasWarehouse basWarehouse) throws JeecgBootException;

	/**删除节点*/
	void deleteBasWarehouse(String id) throws JeecgBootException;

	/**查询所有数据，无分页*/
	List<BasWarehouse> queryTreeListNoPage(QueryWrapper<BasWarehouse> queryWrapper);
}
