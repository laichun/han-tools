package com.todostudy.tools.fm;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HanBaseDTO implements Serializable {
    @ApiModelProperty(value = "ID")
    private String id;

    @JsonIgnore
    public <E> void copyToEntity(E entity, String... ignoreProperties) {
        BeanUtils.copyProperties(this, entity, ignoreProperties);
    }
}
