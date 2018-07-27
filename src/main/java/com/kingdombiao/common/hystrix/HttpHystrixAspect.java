package com.kingdombiao.common.hystrix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.MethodNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kingdombiao.hystrix.config.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

@Aspect
@Component
public class HttpHystrixAspect {
	
	private Logger logger= LoggerFactory.getLogger(getClass());
	
	@Around(value="execution (public String com.kingdombiao.service.impl.*.*(..)) && @annotation(hystrixCommand)")
	public Object aroundHttpRequest(ProceedingJoinPoint pjp ,HystrixCommand hystrixCommand) {
		Object result=null;
		//执行类名
		String targetName=pjp.getTarget().getClass().getSimpleName();
		
		//方法签名
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		
		//执行的方法名
		String methodName=signature.getMethod().getName();
		
		//初始化熔断器上下文
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		
		 try {
	            result = new HttpHystrix(pjp, targetName, methodName, hystrixCommand).execute();
	        } finally {
	            logger.info("Request => " + HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
	            context.shutdown();
	        }
		
		return result;
	}
	
	public class HttpHystrix extends com.netflix.hystrix.HystrixCommand<Object>{
		
		private final ProceedingJoinPoint pjp;
		
		//类名
		private final String className;
		//方法名
		private final String methodName;
		//注解
		private final HystrixCommand hystrixCommand;
		
		
		/**
         * @param pjp        
         * @param serviceId  类名+方法名
         */
        protected HttpHystrix(ProceedingJoinPoint pjp, String className, String methodName, HystrixCommand hystrixCommand) {
            // Hystrix uses the command group key to group together commands such as for reporting,
            // alerting, dashboards, or team/library ownership.
            // 同一个groupKey共用同一个线程池
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(className))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(methodName))
                    // 超时时间
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(10000)));
            this.pjp = pjp;
            this.className = className;
            this.methodName = methodName;
            this.hystrixCommand = hystrixCommand;
        }


		@Override
		protected Object getFallback() {
			logger.info("[{}] 错误次数达到阈值或者执行超时, 进行熔断措施", className + "_" + methodName);
			//熔断方法名称
			String fallbackMethod = hystrixCommand.fallbackMethod();
			// 未声明了熔断机制，默认熔断方法
			if(StringUtils.isEmpty(fallbackMethod)){
                return "返回失败";
            }
			
			Method[] methods = pjp.getTarget().getClass().getMethods();
			Method method =null;
			for (Method m : methods) {
				if(m.getName().equals(fallbackMethod)) {
					method=m;
					break;
				}
			}
			
			// 未在类中找到申明的fallbackMethod方法
            if(method == null){
                throw new MethodNotFoundException();
            }
            
            //熔断方法传入参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            
            //传入参数为空，直接执行方法
            if(parameterTypes.length == 0) {
            	try {
					return method.invoke(pjp.getTarget());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
            }
            
            //传入参数不为空，则传入AOP拦截方法参数
            try {
				return method.invoke(pjp.getTarget(), pjp.getArgs());
			}  catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}


		@Override
		protected Object run() throws Exception {
			try {
				return pjp.proceed();
			} catch (Throwable e) {
				logger.error("HystrixCommand InterruptedException,May be TimeOut." + e.getMessage());
				throw new Exception(e);
			}
		}
	}

}
