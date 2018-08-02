package com.kingdombiao.hystrixCommand;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import rx.Observable;
import rx.Observer;

/**
 * 四种命令执行方法的结果获取
 * @author kingdom.biao
 *
 */
public class FourCommand extends HystrixCommand<String> {

	private final String name;
	
	

	public FourCommand(String name) {
		//最小配置，指定groupKey
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("fourCommandGroup"))
				////commonKey代表一个依赖抽象,相同的依赖要用相同的commonKey,依赖隔离的根本就是依据commonKey进行隔离
				.andCommandKey(HystrixCommandKey.Factory.asKey("fourCommand")));
		this.name = name;
	}
	
	@Override
	protected String run() throws Exception {
		
		return "fourCommand=>:"+name+",current thread=>:"+Thread.currentThread().getName();
	}
	



	public static void main(String[] args) throws Exception{
		
		FourCommand command=new FourCommand("kingdombiao");
		
		//1、同步调用
		String result = command.execute();
		System.out.println("<<<<----Sync calll result--->>>>"+result);

		//2、异步调用
		command=new FourCommand("kingdombiao async");
		Future<String> future = command.queue();
		result = future.get();
		System.out.println("Async call result --->>>"+result);
		
		//3.1 注册观察者事件订阅 -- 事件注册前执行
		Observable<String> observable = new FourCommand("kingdombiao observable").observe();
		observable.subscribe(result1 -> System.out.println("Observable call result --> " + result1));
		
		//3.2 注册完整执行生命周期事件 -- 事件注册前执行
		observable.subscribe(new Observer<String>() {

			@Override
			public void onCompleted() {
				//onNext/onError完成之后最后回调
                System.out.println("observe Execute onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				// 当产生异常时回调
                System.out.println("observe Execute error");
                e.printStackTrace();
				
			}

			@Override
			public void onNext(String t) {
				 // 获取结果后回调
                System.out.println("observe Execute onNext --> " + t);
			}
		});
		
		//4、注册观察者事件订阅 -- 事件注册后执行
        command = new FourCommand("kingdombiao toObservable");
        Observable<String> toObservable = command.toObservable();
        toObservable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                //onNext/onError完成之后最后回调
                System.out.println("toObservable Execute onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                // 当产生异常时回调
                System.out.println("toObservable Execute error");
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                // 获取结果后回调
                System.out.println("toObservable Execute onNext --> " + s);
            }
        });

        //异步执行需要时间，先阻塞主线程
        Thread.sleep(5000);
    }
}
