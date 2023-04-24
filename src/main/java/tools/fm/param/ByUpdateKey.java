package tools.fm.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 使用时候继承他即可以
 */
@Data
public class ByUpdateKey implements Serializable {

    @ApiModelProperty("ID id必传")
    private String id; // id必传
    private List<UpdateDto> dtos;
}
