package io.finer.erp.base.mapper;

import io.finer.erp.base.entity.BasMaterialCategory;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 物料分类
 * @Author: jeecg-boot
 * @Date:   2020-05-29
 * @Version: V1.0
 */
public interface BasMaterialCategoryMapper extends BaseMapper<BasMaterialCategory> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
