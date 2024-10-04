package com.atguigu.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.daijia.customer.mapper.CustomerInfoMapper;
import com.atguigu.daijia.customer.mapper.CustomerLoginLogMapper;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.vo.customer.CustomerInfoVo;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {
    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private CustomerInfoMapper customerInfoMapper;
    @Autowired
    private CustomerLoginLogMapper customerLoginLogMapper;
    @Override
    public Long login(String code) {
        // 1. 获取code值，使用微信工具包对象，获取微信唯一标识openid
        String openId =  null;
        try {
            WxMaJscode2SessionResult sessionInfo =
                    wxMaService.getUserService().getSessionInfo(code);
            openId = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        // 2. 根据openid查询数据表
        // 2.1 如果不存在则返回null,如果存在则返回一条记录
        LambdaQueryWrapper<CustomerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerInfo::getWxOpenId,openId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);
        // 2.2 select * from customer_info ci where ci.wx_open_id=''
        // 3. 如果第一次登录，添加信息到用户表
        if (customerInfo == null) {
            customerInfo= new CustomerInfo();
            customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));
            customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            customerInfo.setWxOpenId(openId);
            customerInfoMapper.insert(customerInfo);
        }

        // 4. 记录登录日志信息
        //4 记录登录日志信息
        CustomerLoginLog customerLoginLog = new CustomerLoginLog();
        customerLoginLog.setCustomerId(customerInfo.getId());
        customerLoginLog.setMsg("小程序登录");
        customerLoginLogMapper.insert(customerLoginLog);
        //5 返回用户id
        return customerInfo.getId();

    }

    @Override
    public CustomerLoginVo getCustomerLoginVo(Long customerId) {
        // 根据customerId查询乘客信息
        CustomerInfo customerInfo = customerInfoMapper.selectById(customerId);
        // 封装到customerInfoVo
        CustomerLoginVo customerLoginVo = new CustomerLoginVo();
        BeanUtils.copyProperties(customerInfo, customerLoginVo);

        //@Schema(description = "是否绑定手机号码")
        //    private Boolean isBindPhone;
        String phone = customerInfo.getPhone();
        boolean isBindPhone = StringUtils.hasText(phone);
        customerLoginVo.setIsBindPhone(isBindPhone);
        //3 CustomerLoginVo返回
        return customerLoginVo;
    }
}
