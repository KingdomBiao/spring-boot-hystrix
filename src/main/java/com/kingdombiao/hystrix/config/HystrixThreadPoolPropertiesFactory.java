package com.kingdombiao.hystrix.config;

import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 线程池属性配置
 * @author kingdom.biao
 *
 */
public class HystrixThreadPoolPropertiesFactory {
	
	public static HystrixThreadPoolProperties.Setter createThreadPoolProperties(){
		return HystrixThreadPoolProperties.Setter()
				.withCoreSize(10) //设置核心线程池的大小
				.withAllowMaximumSizeToDivergeFromCoreSize(true) //允许maximumSize起作用
				.withMaximumSize(12) //设置线程池最大值
				.withMaxQueueSize(-1) //设置BlockingQueue最大的队列值。如果设置为-1，那么使用SynchronousQueue，否则正数将会使用LinkedBlockingQueue。
				.withKeepAliveTimeMinutes(1); //设置存活时间，单位分钟。如果coreSize小于maximumSize，那么该属性控制一个线程从实用完成到被释放的时间。
		
	}

}
