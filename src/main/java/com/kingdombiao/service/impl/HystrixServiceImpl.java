package com.kingdombiao.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kingdombiao.hystrix.config.HystrixCommand;
import com.kingdombiao.service.HystrixService;

@Service
public class HystrixServiceImpl implements HystrixService {
	
	private Logger logger= LoggerFactory.getLogger(getClass());

	@Override
	@HystrixCommand(fallbackMethod ="hiError")
	public String testHystrix(int outTimeRate, int runTimeRate) {
		
		//模拟数据操作耗时
		try {
			Thread.sleep((int)(Math.random()*10)+2);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		/* 执行失败比率 */
        if (Math.random() > (double) runTimeRate / 100) {
            throw new RuntimeException("运行异常");
        }
        
        /* 执行超时比率 */
        if (Math.random() > (double) runTimeRate / 100) {
            try {
                Thread.sleep(Integer.parseInt("1") + 5);
            } catch (Exception e) {
                // do nothing
            }
        }
        return "{'status': 'SUCCESS'}";
	}
	
	public String hiError(int n, int m) {
        logger.info("熔断措施：hi,sorry,error!");
        return "hi,sorry,error!";
    }

}
