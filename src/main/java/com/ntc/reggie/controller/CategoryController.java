package com.ntc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ntc.reggie.common.R;
import com.ntc.reggie.entity.Category;
import com.ntc.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
@ResponseBody
@Slf4j
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("成功");
    }

    @DeleteMapping()
    public R<String> deleteById(Long id) {
        log.info("id{}",id);
        // categoryService.removeById(id);
        return categoryService.remove(id);

    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        lqw.like(StringUtils.isNotEmpty(name), Category::getName,name);
        lqw.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        if (categoryService.updateById(category)) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }


    @GetMapping("list")
    private R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper= new LambdaQueryWrapper<>();
        Integer type = category.getType();
        categoryLambdaQueryWrapper.eq(type != null,Category::getType, type);
        // 添加排序条件
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);
    }
}
