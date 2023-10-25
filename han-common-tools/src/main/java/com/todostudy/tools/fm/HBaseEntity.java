package com.todostudy.tools.fm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Getter
@Setter
public class HBaseEntity implements Serializable {
    private static final long serialVersionUID = 8948437944054606982L;

    //默认主键生成策略雪花算法
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @JsonIgnore
    public <T> void copyToDTO(T dto, String... ignoreProperties) {
        BeanUtils.copyProperties(this, dto, ignoreProperties);
    }

}
