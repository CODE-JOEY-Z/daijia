package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.JoeyLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {
    @Autowired
    private CustomerService customerInfoService;

    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> wxLogin(@PathVariable String code) {
        return Result.ok(customerInfoService.login(code));
    }

//    @Operation(summary = "获取用户信息")
//    @GetMapping("/getCustomerLoginInfo")
//    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token){
//        CustomerLoginVo customerLoginVo = customerInfoService.getCustomerLoginInfo(token);
//        return Result.ok(customerLoginVo);
//    }

    @Operation(summary = "获取用户信息增强版")
    @GetMapping("/getCustomerLoginInfo")
    @JoeyLogin
    public Result<CustomerLoginVo> getCustomerLoginInfo() {
        // 从ThreadLocal中获取用户id
        Long customerId = AuthContextHolder.getUserId();
        CustomerLoginVo customerLoginInfo = customerInfoService.getCustomerLoginInfoPlus(customerId);
        return Result.ok(customerLoginInfo);
    }
}

