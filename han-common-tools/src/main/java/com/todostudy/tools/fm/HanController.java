package com.todostudy.tools.fm;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todostudy.tools.fm.enums.OrderByEnum;
import com.todostudy.tools.fm.param.ByUpdateKey;
import com.todostudy.tools.fm.param.UpdateDto;
import com.todostudy.tools.utils.HanTool;
import com.todostudy.tools.fm.enums.QueryEnum;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
public abstract class HanController<T extends HBaseEntity> {

    public abstract ServiceImpl getService();

    @ApiOperation("查询所有数据接口,传入dto")
    @PostMapping("/findall")
    public <E extends HanQueryDTO> ResponseEntity findAll(@RequestBody E dto) {
        //准备查询条件
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        List<QueryDto> queryDtoList = dto.getQueryDtoList();
        queryDtoList.forEach(item -> {
            if (item.getCondition().equals(QueryEnum.eq.name())) {
                queryWrapper.eq(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.like.name())) {
                queryWrapper.like(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.ge.name())) {
                queryWrapper.ge(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.le.name())) {
                queryWrapper.le(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.in.name())) {
                queryWrapper.in(HanTool.xX2x_x(item.getKey()), (Collection) item.getValue());// "value": [1,2,3]
            }
        });

        if (dto.getOrder() != null) {
            if (dto.getOrder().getBy().equals(OrderByEnum.ASC.name())) {
                queryWrapper.orderByAsc(HanTool.xX2x_x(dto.getOrder().getOrder()));
            } else if (dto.getOrder().getBy().equals(OrderByEnum.DESC.name())) {
                queryWrapper.orderByDesc(HanTool.xX2x_x(dto.getOrder().getOrder()));
            }
        }
        return ResponseEntity.ok(getService().list(queryWrapper));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @PostMapping("/listpage")
    public <E extends HanQueryDTO> ResponseEntity listPage(@RequestParam(PC.PAGE_SIZE) long pageSize,
                                                           @RequestParam(PC.PAGE) long pg, @RequestBody E dto) {
        //设置分页条件
        Page page = new Page(pg, pageSize);
        //准备查询条件
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        List<QueryDto> queryDtoList = dto.getQueryDtoList();
        queryDtoList.forEach(item -> {
            if (item.getCondition().equals(QueryEnum.eq.name())) {
                queryWrapper.eq(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.like.name())) {
                queryWrapper.like(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.ge.name())) {
                queryWrapper.ge(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.le.name())) {
                queryWrapper.le(HanTool.xX2x_x(item.getKey()), item.getValue());
            } else if (item.getCondition().equals(QueryEnum.in.name())) {
                queryWrapper.in(HanTool.xX2x_x(item.getKey()), (Collection) item.getValue());// "value": [1,2,3]
            }

        });
        if (dto.getOrder() != null) {
            if (dto.getOrder().getBy().equals(OrderByEnum.ASC.name())) {
                queryWrapper.orderByAsc(HanTool.xX2x_x(dto.getOrder().getOrder()));
            } else if (dto.getOrder().getBy().equals(OrderByEnum.DESC.name())) {
                queryWrapper.orderByDesc(HanTool.xX2x_x(dto.getOrder().getOrder()));
            }
        }
        IPage iPage = getService().page(page, queryWrapper);
        return ResponseEntity.ok(iPage);
    }

    @ApiOperation("id查询接口")
    @GetMapping("/view/{id}")
    public ResponseEntity findbyId(@PathVariable("id") String id) {
        return ResponseEntity.ok(getService().getById(id));
    }

    /*    @ApiOperation("保存或者修改接口")
        @PostMapping("/saveOrUpate")*/
    //重写这个方法
    protected ResponseEntity saveOrUpate(T en) {
        if (!ObjectUtils.isEmpty(en.getId())) {
            getService().save(en);
            return ResponseEntity.ok(en);
        } else {
            return ResponseEntity.ok(getService().saveOrUpdate(en));
        }
    }

    @ApiOperation("-更新字段")
    @PostMapping("/updateByKeys")
    public <E extends ByUpdateKey> ResponseEntity updateByKeys(@RequestBody E param) {
        Assert.notNull(param.getSetDtos(), "更新字段不能为空");
        UpdateWrapper<ByUpdateKey> updateWrapper = new UpdateWrapper<ByUpdateKey>();
        updateWrapper.eq("id", param.getId());
        for (UpdateDto eqItem : param.getEqDtos()) {
            updateWrapper.eq(HanTool.xX2x_x(eqItem.getKey()), eqItem.getValue());
        }

        for (UpdateDto setItem : param.getSetDtos()) {
            updateWrapper.set(HanTool.xX2x_x(setItem.getKey()), setItem.getValue());
        }
        //更新
        return ResponseEntity.ok(getService().update(updateWrapper));
    }

}
