package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {


    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    //登录
    @Override
    public String login(String code) {
        //远程调用，得到司机id
        Result<Long> longResult = driverInfoFeignClient.login(code);
        //TODO 判断
        Long driverId = longResult.getData();
        if(driverId == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //token字符串
        String token = UUID.randomUUID().toString().replaceAll("-","");
        //放到redis，设置过期时间
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                driverId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                TimeUnit.SECONDS);
        return token;
    }

    @Override
    public DriverLoginVo getDriverLoginVo(Long driverId) {
        //根据id进行远程调用
        Result<DriverLoginVo> driverLogin = driverInfoFeignClient.getDriverLogin(driverId);
        Integer code = driverLogin.getCode();
        if (code != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        DriverLoginVo driverLoginVo = driverLogin.getData();
        if (driverLoginVo == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return driverLoginVo;
    }
}
