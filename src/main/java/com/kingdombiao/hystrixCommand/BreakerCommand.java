package com.kingdombiao.hystrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 触发熔断器熔断
 * @author kingdom.biao
 *
 */
public class BreakerCommand extends HystrixCommand<String> {
	
	private final String name;

	public BreakerCommand(String name) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("breakerGroup"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(500))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withCircuitBreakerEnabled(true)
						.withCircuitBreakerErrorThresholdPercentage(50)
						.withCircuitBreakerRequestVolumeThreshold(3)
						.withExecutionTimeoutInMilliseconds(1000))
				.andCommandKey(HystrixCommandKey.Factory.asKey("breaker")));
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
		 System.out.println("RUNNABLE --> " + name);
	        Integer num = Integer.valueOf(name);
	        if (num % 2 == 0 && num < 10) {
	            return "breaker: " + name + ",current thread:" + Thread.currentThread().getName();
	        } else {
	            Thread.sleep(1500);
	            return name;
	        }
	}
	
	@Override
	protected String getFallback() {
		return "<-----fallback----->";
	}

	public static void main(String[] args) {
		 for (int i = 0; i < 50; i++) {
	            try {
	                System.out.println(new BreakerCommand(String.valueOf(i)).execute());
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }

	        try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
