package com.kingdombiao.service;

import java.util.List;

import com.kingdombiao.entities.User;

/**
 * @author 11572
 *
 */
public interface UserService {
	
	int addUser(User user);
	
	List<User> findAllUser(int pageNum,int pageSize) throws Exception;

}
