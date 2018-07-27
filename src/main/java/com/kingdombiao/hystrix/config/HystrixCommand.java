package com.kingdombiao.hystrix.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HystrixCommand {
	
	/**
     * 当错误次数达到阈值或者执行超时，直接执行下面代码
     * 如果不填fallbackMethod则执行默认熔断方法
     * 如果填写fallbackMethod则熔断方法必须在配置注解的同一个类里面，否则抛出MethodNotFoundException
     * [熔断方法传参]
     * 1. 不传参则直接执行fallbackMethod熔断方法
     * 2. 传参则必须和配置注解方法传参类型保持一致, 否则会执行错误
     * 参考：HttpHystrixAspect.java
     * @return
     */
    public String fallbackMethod() default "";

}
