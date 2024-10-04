package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerService {
    public String login(String code);

    public CustomerLoginVo getCustomerLoginInfo(String token);

    public CustomerLoginVo getCustomerLoginInfoPlus(Long customerId);
}
