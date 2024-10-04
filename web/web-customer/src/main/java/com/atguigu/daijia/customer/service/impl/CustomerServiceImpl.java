package com.atguigu.daijia.customer.service.impl;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
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
public class CustomerServiceImpl implements CustomerService {


    //注入远程调用接口
    @Autowired
    private CustomerInfoFeignClient client;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {
        //1 拿着code进行远程调用，返回用户id
        Result<Long> loginResult = client.login(code);

        //2 判断如果返回失败了，返回错误提示
        Integer codeResult = loginResult.getCode();
        if(codeResult != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //3 获取远程调用返回用户id
        Long customerId = loginResult.getData();

        //4 判断返回用户id是否为空，如果为空，返回错误提示
        if(customerId == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //5 生成token字符串
        String token = UUID.randomUUID().toString().replaceAll("-","");

        //6 把用户id放到Redis，设置过期时间
        // key:token  value:customerId
        //redisTemplate.opsForValue().set(token,customerId.toString(),30, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token,
                customerId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                TimeUnit.SECONDS);
        //7 返回token
        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(String token) {
        //根据token查询redis判断用户是否登录
        // 获取用户id
        String customerId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        if(!StringUtils.hasText(customerId)) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //根据用户id进行远程调用
        Result<CustomerLoginVo> customerLoginInfo = client.getCustomerLoginInfo(Long.parseLong(customerId));
        Integer code = customerLoginInfo.getCode();
        if(code != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        CustomerLoginVo customerLoginVo = customerLoginInfo.getData();
        if(customerLoginVo == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return customerLoginVo;
    }


    /**
     * 增强版
     * @param customerId
     * @return
     */
    @Override
    public CustomerLoginVo getCustomerLoginInfoPlus(Long customerId) {
        //根据用户id进行远程调用
        Result<CustomerLoginVo> customerLoginInfo = client.getCustomerLoginInfo(customerId);
        Integer code = customerLoginInfo.getCode();
        if(code != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        CustomerLoginVo customerLoginVo = customerLoginInfo.getData();
        if(customerLoginVo == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return customerLoginVo;
    }
}
