package com.todostudy.tools.fm.param;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * laich  Date:2022/8/15
 *
 * @Desciprtion:
 */
@Data
public class HolidayDO implements Serializable {

    private Long id;
    private Long holiday;
}
