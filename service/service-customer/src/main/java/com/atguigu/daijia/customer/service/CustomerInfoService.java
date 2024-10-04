package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CustomerInfoService extends IService<CustomerInfo> {
    public Long login(String code);

    public CustomerLoginVo getCustomerLoginVo(Long customerId);
}
