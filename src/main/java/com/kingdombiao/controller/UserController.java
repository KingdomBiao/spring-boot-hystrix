package com.kingdombiao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingdombiao.entities.User;
import com.kingdombiao.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/add")
	public int addUser(User user) {
		return userService.addUser(user);
	}
	
	@RequestMapping("/getAllUser")
	public List<User> findAllUser(int pageNum,int pageSize) throws Exception{
		return userService.findAllUser(pageNum, pageSize);
	}

}
