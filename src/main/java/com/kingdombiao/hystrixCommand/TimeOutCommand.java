package com.kingdombiao.hystrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * 超时降级
 * @author kingdom.biao
 *
 */
public class TimeOutCommand extends HystrixCommand<String> {


	private final String name;
	
	public TimeOutCommand(String name) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("timeOutGroup"))
				//指定超时时间为500ms
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500))
				//common key
				.andCommandKey(HystrixCommandKey.Factory.asKey("timeOut")));
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
		Thread.sleep(1000);
		return "timeOut:"+name+",current thread:"+Thread.currentThread().getName();
		
	}
	
	@Override
	protected String getFallback() {
		
		return "<<<<<-----------超时执行fallback!--------------->>>>>";
	}

	public static void main(String[] args) {
		TimeOutCommand timeOutCommand = new TimeOutCommand("kingdombiao timeout");
		//超时执行getFallback;
		System.out.println(timeOutCommand.execute());

	}

}
