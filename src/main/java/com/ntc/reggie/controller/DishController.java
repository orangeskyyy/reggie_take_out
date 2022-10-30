package com.ntc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ntc.reggie.common.R;
import com.ntc.reggie.dto.DishDto;
import com.ntc.reggie.entity.Category;
import com.ntc.reggie.entity.Dish;
import com.ntc.reggie.entity.DishFlavor;
import com.ntc.reggie.entity.Employee;
import com.ntc.reggie.service.CategoryService;
import com.ntc.reggie.service.DishFlavorService;
import com.ntc.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("dishDto{}",dishDto);
        dishService.saveWithDishFlavor(dishDto);
        return R.success("成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        // 构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        // 构造查询条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        // 设置查询条件 name字段不为空
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        // 设置排序条件
        lqw.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,lqw);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id) {

        Dish dish = dishService.getById(id);

        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavorList);


        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavors(dishDto);
        return R.success("修改成功");
    }


}
