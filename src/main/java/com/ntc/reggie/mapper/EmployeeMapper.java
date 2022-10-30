package com.ntc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ntc.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
