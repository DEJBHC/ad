package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 第三方部门映射表
 * @Author: cfm
 * @Date:   2021-12-20
 * @Version: V1.0
 */
@Data
@TableName("sys_third_depart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_third_depart对象", description="第三方部门映射表")
public class SysThirdDepart {

	/**编号*/
	@TableId(type = IdType.ASSIGN_ID)
  @ApiModelProperty(value = "编号")
	private String id;
	/**系统部门id*/
	@ApiModelProperty(value = "系统部门id")
	private String sysDepartId;
	/**登录来源*/
	@ApiModelProperty(value = "第三方类型")
	private String thirdType;
	@ApiModelProperty(value = "第三方部门id")
	private String thirdDepartId;
}
