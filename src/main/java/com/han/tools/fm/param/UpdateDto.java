package com.han.tools.fm.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author handson
 * key是col , value 是字段值
 */
@Data
@ApiModel(description = "UpdateDto")
public class UpdateDto {

    /**
     * mybatis映射数据库字段
     */
    @ApiModelProperty("查询字段的属性名称：比如 userName")
    private String key;

    @ApiModelProperty("查询字段的属性值")
    private Object value;

}

