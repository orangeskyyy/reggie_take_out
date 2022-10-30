package com.ntc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ntc.reggie.common.R;
import com.ntc.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public R<String> remove(Long id);
}
