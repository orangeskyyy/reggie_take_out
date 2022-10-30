package com.ntc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ntc.reggie.dto.DishDto;
import com.ntc.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithDishFlavor(DishDto dish);

    void updateWithFlavors(DishDto dishDto);
}
