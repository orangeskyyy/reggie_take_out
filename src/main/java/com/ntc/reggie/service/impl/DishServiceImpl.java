package com.ntc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ntc.reggie.dto.DishDto;
import com.ntc.reggie.entity.Dish;
import com.ntc.reggie.entity.DishFlavor;
import com.ntc.reggie.mapper.DishMapper;
import com.ntc.reggie.service.DishFlavorService;
import com.ntc.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void saveWithDishFlavor(DishDto dishDto) {
        this.save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().forEach(e -> e.setDishId(dishDto.getId()));
        flavors = flavors.stream().map(e -> {
            e.setDishId(dishDto.getId());
            return e;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void updateWithFlavors(DishDto dishDto) {
        // 1. 更新菜品信息
        this.updateById(dishDto);

        // 2. 更新口味信息
        // 2.1 删除原先的口味信息
        // 根据dish_id删除dishFlavor
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        // 2.2 增加新的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 给每个flavor增加dish_id字段
        flavors = flavors.stream().map(flavor -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
