package com.kingdombiao.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.kingdombiao.entities.User;
import com.kingdombiao.hystrix.config.HystrixCommand;
import com.kingdombiao.mapper.UserMapper;
import com.kingdombiao.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserMapper userMapper;

	@Override
	@HystrixCommand(fallbackMethod = "fallBack")
	public int addUser(User user) {
		return userMapper.insertSelective(user);
	}

	@Override
	@HystrixCommand(fallbackMethod = "fallBack")
	public List<User> findAllUser(int pageNum, int pageSize) throws Exception {
		// 模拟数据操作耗时
		/*try {
			Thread.sleep((int) (Math.random() * 1000) + 2000);
		} catch (InterruptedException e) {
			// do nothing
		}*/
		
		/* 执行失败比率 */
        /*if (Math.random() > (double)(Math.random() * 1000) / 100) {
            throw new RuntimeException("运行异常");
        }*/
		
		
			Thread.sleep(10000);
	
		
		PageHelper.startPage(pageNum, pageSize);
		return userMapper.selectAllUser();
	}

	public List<User> fallBack(int n, int m) {
		logger.info("熔断措施：hi,sorry,error!");
		return new ArrayList<>();
	}

}
