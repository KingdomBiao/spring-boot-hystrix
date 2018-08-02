package com.kingdombiao.hystrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * 请求结果缓存 hystrix支持将一个请求结果缓存起来，下一个具有相同key的请求将直接从缓存中取出结果，减少请求开销。 要使用hystrix
 * cache功能， 第一个要求是重写getCacheKey()，用来构造cache key；
 * 第二个要求是构建context，如果请求B要用到请求A的结果缓存，A和B必须同处一个context。
 * 通过HystrixRequestContext.initializeContext()和context.shutdown()可以构建一个context，这两条语句间的所有请求都处于同一个context，
 * 同一个context中可以从缓存中直接获取cache key相同的响应结果。
 * 
 * @author kingdom.biao
 *
 */
public class RequestCacheCommand extends HystrixCommand<Boolean> {

	private final Integer value;
	private final String name;

	public RequestCacheCommand(Integer value, String name) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("requestCacheGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("requestCache")));
		this.value = value;
		this.name = name;
	}

	@Override
	protected Boolean run() throws Exception {
		System.out.println("<======执行了此代码======>");
		return value == 0 || value % 2 == 0;
	}

	@Override
	protected String getCacheKey() {
		return name + ":" + value;
	}

	public static void main(String[] args) {
		HystrixRequestContext context = HystrixRequestContext.initializeContext();

		try {
			RequestCacheCommand command1 = new RequestCacheCommand(1, "kingdombiao");
			RequestCacheCommand command2 = new RequestCacheCommand(1, "kingdombiao");
			RequestCacheCommand command3 = new RequestCacheCommand(1, "kingdombiao-ex");

			System.out.println("command1 result --> " + command1.execute());
			System.out.println("command1 isResponseFromCache --> " + command1.isResponseFromCache());

			System.out.println("command2 result --> " + command2.execute());
			System.out.println("command2 isResponseFromCache --> " + command2.isResponseFromCache());

			System.out.println("command3 result --> " + command3.execute());
			System.out.println("command3 isResponseFromCache --> " + command3.isResponseFromCache());
		} finally {
			context.shutdown();
		}
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
