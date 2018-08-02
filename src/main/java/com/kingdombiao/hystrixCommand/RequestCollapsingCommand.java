package com.kingdombiao.hystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * hystrix支持N个请求自动合并为一个请求，这个功能在有网络交互的场景下尤其有用，比如每个请求都要网络访问远程资源，如果把请求合并为一个，将使多次网络交互变成一次，极大节省开销。
 * 重要一点，两个请求能自动合并的前提是两者足够“近”，即两者启动执行的间隔（timerDelayInMilliseconds）时长要足够小，默认为10ms，即超过10ms将不自动合并。
 * 请求合并需要继承HystrixCollapser<BatchReturnType, ResponseType,
 * RequestArgumentType>，三个泛型参数的含义分别是： -
 * BatchReturnType：createCommand()方法创建批量命令的返回值的类型。 - ResponseType：单个请求返回的类型。 -
 * RequestArgumentType：getRequestArgument()方法请求参数的类型。
 * 
 * 继承HystrixCollapser后需要覆写三个方法：getRequestArgument()、createCommand()、mapResponseToRequests()。
 * 
 * 
 * @author kingdom.biao
 *
 */
public class RequestCollapsingCommand extends HystrixCollapser<List<Boolean>, Boolean, Integer> {

	private final Integer value;

	public RequestCollapsingCommand(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getRequestArgument() {
		return value;
	}

	@Override
	protected HystrixCommand<List<Boolean>> createCommand(Collection<CollapsedRequest<Boolean, Integer>> requests) {
		return new BatchCommand(requests);
	}

	private static final class BatchCommand extends HystrixCommand<List<Boolean>> {
		private final Collection<CollapsedRequest<Boolean, Integer>> requests;

		public BatchCommand(Collection<CollapsedRequest<Boolean, Integer>> requests) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("requestCollapsingGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("requestCollapsing")));
			this.requests = requests;
		}

		@Override
		protected List<Boolean> run() throws Exception {
			List<Boolean> response = new ArrayList<>();
			for (CollapsedRequest<Boolean, Integer> request : requests) {
				Integer argument = request.getArgument();
				response.add(0 == argument || argument % 2 == 0); // 这里就是执行单元的逻辑
			}
			return response;
		}

	}

	@Override
	protected void mapResponseToRequests(List<Boolean> batchResponse,
			Collection<CollapsedRequest<Boolean, Integer>> requests) {
		int count = 0;
        for (CollapsedRequest<Boolean, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }

	}

	public static void main(String[] args) throws Exception {
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Future<Boolean> command1 = new RequestCollapsingCommand(1).queue();
            Future<Boolean> command2 = new RequestCollapsingCommand(2).queue();
            Future<Boolean> command3 = new RequestCollapsingCommand(3).queue();
            Future<Boolean> command4 = new RequestCollapsingCommand(4).queue();
            Future<Boolean> command5 = new RequestCollapsingCommand(5).queue();
            //故意sleep超过10ms,第六个命令不会合并到本次批量请求
            TimeUnit.MILLISECONDS.sleep(12);
            Future<Boolean> command6 = new RequestCollapsingCommand(6).queue();

            System.out.println(command1.get());
            System.out.println(command2.get());
            System.out.println(command3.get());
            System.out.println(command4.get());
            System.out.println(command5.get());
            System.out.println(command6.get());
            // note：numExecuted表示共有几个命令执行，1个批量多命令请求算一个
            // 因为due to non-determinism of scheduler since this example uses the real timer
            int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();
            System.out.println("num executed: " + numExecuted);
            int numLogs = 0;
            for (HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                numLogs++;
                System.out.println(command.getCommandKey().name() + " => command.getExecutionEvents(): " + command.getExecutionEvents());
            }
            System.out.println("num logs:" + numLogs);
        } finally {
            context.shutdown();
        }

        Thread.sleep(60000);

	}

}
