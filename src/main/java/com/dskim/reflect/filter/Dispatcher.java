package com.dskim.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dskim.reflect.anno.RequestMapping;
import com.dskim.reflect.controller.UserController;

public class Dispatcher implements Filter{
	
	private boolean isMatch = false;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
//		System.out.println("ContextPath: " + request.getContextPath());
//		System.out.println("getRequestURI: " + request.getRequestURI());
//		System.out.println("getRequestURL: " + request.getRequestURL());

		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("EndPoint: " + endPoint);
		
		UserController userController = new UserController();
//		if (endPoint.equals("/join")) {
//			userController.join();
//		} else if(endPoint.equals("/login")) {
//			userController.login();
//		} else if(endPoint.equals("/user")) {
//			userController.user();
//		}

		Method[] methods = userController.getClass().getDeclaredMethods();
//		for (Method method : methods) {
////			System.out.println(method.getName());
//			if (endPoint.equals("/" + method.getName())) {
//				try {
//					method.invoke(userController);
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		}
		for (Method method : methods) {
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping) annotation;
//			System.out.println(requestMapping.value());
			
			if (requestMapping.value().equals(endPoint)) {
				isMatch = true;
				try {
					Parameter[] params = method.getParameters();
					String path = null;
					
					if (params.length != 0) {
//						System.out.println("params[0] " + params[0]);
//						System.out.println("params[0].getName() " + params[0].getName());
//						System.out.println("params[0].getType() " + params[0].getType());
						
						// 해당 dtoInstance를 리플렉션해서 set함수 호출(username, password)
						Object dtoInstance = params[0].getType().newInstance(); // /user/login => LoginDto, /user/join => JoinDto
//						String username = request.getParameter("username");
//						String password = request.getParameter("password");
						setData(dtoInstance, request);
						
						path = (String)method.invoke(userController, dtoInstance);
					} else {
						path = (String)method.invoke(userController);
					}
					
					RequestDispatcher dis = request.getRequestDispatcher(path); // 내부적으로 보내서 필터를 안탐!
					dis.forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if (isMatch == false) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("Invalid request url. 404 error.");
			out.flush();
		}
	}
	
	private <T> void setData(T instance, HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames(); // username, password
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String methodKey = keyToMethodKey(key);
			
			Method[] methods = instance.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodKey)) {
					try {
						method.invoke(instance, request.getParameter(key));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String keyToMethodKey(String key) {
		String firstKey = "set";
		String upperKey = key.substring(0, 1).toUpperCase();
		String remainKey = key.substring(1);
		
		return firstKey + upperKey + remainKey;
	}
}
