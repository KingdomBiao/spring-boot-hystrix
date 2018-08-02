package com.kingdombiao.hystrix.config;

import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 命令属性的配置
 * @author kingdom.biao
 *
 */
public class HystrixCommandPropertiesFactory {
	
	public static HystrixCommandProperties.Setter creatCommandProperties (){
		return HystrixCommandProperties.Setter()
				//执行属性
				.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD) //配置执行隔离策略，默认是使用线程隔离
				.withExecutionTimeoutInMilliseconds(20000) //设置调用者等待命令执行的超时限制，超过此时间，HystrixCommand被标记为TIMEOUT，并执行回退逻辑。
				.withExecutionIsolationThreadInterruptOnTimeout(true) //使用线程隔离时,是否对命令执行超时的线程调用中断操作.默认：true
				.withExecutionIsolationSemaphoreMaxConcurrentRequests(10) //最大并发请求数
				
				//回退属性
				.withFallbackIsolationSemaphoreMaxConcurrentRequests(50) // 设置调用线程产生的HystrixCommand.getFallback()方法的允许最大请求数目
				.withFallbackEnabled(true) //该属性决定当前的调用故障或者拒绝发生时，是否调用HystrixCommand.getFallback()。
				
				//断路器（Circuit Breaker）属性配置
				.withCircuitBreakerErrorThresholdPercentage(25) //失败率配置，默认为50%，这里配置的为25%，即失败率到达25%触发熔断
				.withCircuitBreakerSleepWindowInMilliseconds(5000) //设置在断路器被打开，拒绝请求到再次尝试请求的时间间隔。 默认值：5000（毫秒）
				.withCircuitBreakerRequestVolumeThreshold(20); //熔断请求阈值
	}
	

}
