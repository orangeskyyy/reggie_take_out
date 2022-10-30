package com.ntc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ntc.reggie.common.R;
import com.ntc.reggie.entity.Employee;
import com.ntc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        if (emp == null) {
            return R.error("登录失败");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误，登录失败");
        }
        // 0-禁用 1-可用
        if (emp.getStatus() == 0) {
            return R.error("状态已被禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功。。。");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("新增员工信息：{}",employee);
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功。。。");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        // 构造查询条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        // 设置查询条件 name字段不为空
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 设置排序条件
        lqw.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息，并且修改更新时间和更新者
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee) {
        // long id = Thread.currentThread().getId();
        // log.info("controller的线程id:{}",id);
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
