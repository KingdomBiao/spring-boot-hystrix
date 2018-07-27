package com.kingdombiao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingdombiao.service.HystrixService;

@RestController
public class HystrixController {
	
	@Autowired
	private HystrixService  hystrixService;
	
	@RequestMapping("/api/testHystrix")
	public String testHystrix(int outTimeRate, int runTimeRate) {
		String result = hystrixService.testHystrix(outTimeRate, runTimeRate);
		return result;
	}

}
