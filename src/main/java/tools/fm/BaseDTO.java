package tools.fm;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO  implements Serializable {
  @ApiModelProperty(value = "ID")
  private String id;

  // add boke
  @ApiModelProperty("查询字段的集合列表")
  private List<QueryDto> queryDtoList;
}
