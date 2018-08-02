package com.kingdombiao.hystrix.config;

import java.util.concurrent.ConcurrentHashMap;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

/**
 * HystrixCommand.Setter 工厂，生成的配置会放入缓存，下次直接获取
 * @author kingdom.biao
 *
 */
public class SetterFactory {
	
	public static ConcurrentHashMap<String , HystrixCommand.Setter> setterMap=new ConcurrentHashMap<>();
	
	public static HystrixCommand.Setter createSetter(String projectName,String className,String methodName){
		String key=String.format("%s.%s.%s", projectName,className,methodName);
		String keyThread=String.format("%s.%s", projectName,className);
		if(setterMap.containsKey(key)) {
			return setterMap.get(key);
		}else {
			setterMap.putIfAbsent(key, getSetter(key, keyThread));
			return setterMap.get(key);
		}
		
		
	}
	
	/**
	 * 线程池按class进行划分，一个class为一个领域服务，熔断保护按方法维度提供
	 * @param key
	 * @param keyThread
	 * @return
	 */
	private static HystrixCommand.Setter getSetter(String key,String keyThread){
		return HystrixCommand.Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(keyThread)) //服务分组
				.andCommandKey(HystrixCommandKey.Factory.asKey(key)) //服务标识
				.andCommandPropertiesDefaults(HystrixCommandPropertiesFactory.creatCommandProperties()) //命令属性的配置
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolPropertiesFactory.createThreadPoolProperties()); //线程配置
				
	}

}
