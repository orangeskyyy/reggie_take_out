package com.ntc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ntc.reggie.common.CustomException;
import com.ntc.reggie.common.R;
import com.ntc.reggie.entity.Category;
import com.ntc.reggie.entity.Dish;
import com.ntc.reggie.entity.Setmeal;
import com.ntc.reggie.mapper.CategoryMapper;
import com.ntc.reggie.service.CategoryService;
import com.ntc.reggie.service.DishService;
import com.ntc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    @Override
    public R<String> remove(Long categoryId) {
        // 根据分类id查询dish中是否有关联的categoryId
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException("该分类存在于相关的菜品dish中，无法删除");
        }
        // 根据分类id查询setmeal中是否有关联的categoryId
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0) {
            throw new CustomException("该分类存在于相关的套餐Setmeal中，无法删除");
        }
        super.removeById(categoryId);
        return R.success("该分类删除成功！");
    }
}
