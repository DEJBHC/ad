package io.finer.erp.base.mapper;

import io.finer.erp.base.entity.BasUnit;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 计量单位
 * @Author: jeecg-boot
 * @Date:   2020-03-27
 * @Version: V1.0
 */
public interface BasUnitMapper extends BaseMapper<BasUnit> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);


	/**
	 * 可转换的单位列表
	 * @param id
	 */
	List<BasUnit> selectConvertibleById(@Param("id") String id);
}
