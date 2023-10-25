package com.todostudy.tools.fm;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HanQueryDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    private String id;

    //
    @ApiModelProperty("查询字段的集合列表")
    private List<QueryDto> queryDtoList;

    @ApiModelProperty("排序")
    private OrderBy order;
}
