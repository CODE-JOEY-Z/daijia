package com.atguigu.daijia.customer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * @Author: Joey Zhuo
 * @Date: 2024/10/01/18:11
 */
@Component
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxConfigProperties {
    private String appId;
    private String secret;
}
