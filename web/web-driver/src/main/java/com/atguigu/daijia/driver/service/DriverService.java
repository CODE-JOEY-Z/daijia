package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.DriverLoginVo;

public interface DriverService {


    String login(String code);
    DriverLoginVo getDriverLoginVo(Long driverId);
}
