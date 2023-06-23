package com.todostudy.tools.fm;

import com.todostudy.tools.fm.enums.OrderByEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderBy {

    @ApiModelProperty("排序的字段")
    String order;

    @ApiModelProperty("只能2选一 AES/DESC")
    String by;
}
