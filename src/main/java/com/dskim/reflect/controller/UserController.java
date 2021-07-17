package com.dskim.reflect.controller;

import com.dskim.reflect.anno.RequestMapping;
import com.dskim.reflect.controller.dto.JoinDto;
import com.dskim.reflect.controller.dto.LoginDto;

public class UserController {

	@RequestMapping("/user/join")
	public String join(JoinDto dto) { // username, password, email
		System.out.println("join() called.");
		System.out.println(dto);
		return "/";
	}
	
	@RequestMapping("/user/login")
	public String login(LoginDto dto) { // username, password
		System.out.println("login() called.");
		System.out.println(dto);
		return "/";
	}
	
	@RequestMapping("/user")
	public String user() {
		System.out.println("user() called.");
		return "/";
	}

	@RequestMapping("/hello")
	public String hello() {
		System.out.println("hello() called.");
		return "/";
	}

}
