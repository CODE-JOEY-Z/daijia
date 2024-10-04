package com.atguigu.daijia.common.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Joey Zhuo
 * @Date: 2024/10/03/11:07
 */
//登录判断
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoeyLogin {
}
