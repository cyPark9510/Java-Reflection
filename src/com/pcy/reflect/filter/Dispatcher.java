package com.pcy.reflect.filter;

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

import com.pcy.reflect.anno.RequestMapping;
import com.pcy.reflect.controller.UserController;

// 분기 시키기!!(라우터 역할)
public class Dispatcher implements Filter {
	
	boolean isMatching = false;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain arg2)
			throws IOException, ServletException {
		// Dispatcher 진입!!

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		System.out.println("컨텍스트 패스 : " + request.getContextPath()); // 프로젝트 시작 주소
		System.out.println("식별자 주소 : " + request.getRequestURI()); // 끝 주소
		System.out.println("전체 주소 : " + request.getRequestURL()); // 전체 주소

		// /user 파싱
		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("엔드포인트 : " + endPoint);

		UserController userController = new UserController();

//		if(endPoint.equals("/join")) {
//			userController.join();
//		}else if(endPoint.equals("/login")) {
//			userController.login();
//		}else if(endPoint.equals("/user")) {
//			userController.user();
//		}

		// Reflection => 메서드를 런타임(실행) 시점에서 찾아내서 실행
		Method[] methods = userController.getClass().getDeclaredMethods(); // 그 파일의 메서드만!! 상속된거(X)
//		for(Method method : methods) {
//			System.out.println(method.getName());
//			if(endPoint.equals("/" + method.getName())) {
//				try {
//					method.invoke(userController, null);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}

		for (Method method : methods) { // 4바퀴(join, login, user, hello
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping)annotation;
			System.out.println(requestMapping.value());
			
			if (requestMapping.value().equals(endPoint)) {
				isMatching = true;
				try {
					Parameter[] params = method.getParameters();
					String path = null;
					if (params.length != 0) {
						System.out.println("params[0].getType() : "+params[0].getType());
						// 해당 dtoInstance를 리플렉션 해서 set함수 호출(username, password)
						Object dtoInstance = params[0].getType().newInstance();  // /user/login => LoginDto, /user/join => JoinDto
						
						setData(dtoInstance, request);
						
						path = (String) method.invoke(userController, dtoInstance);
					} else {
						path = (String) method.invoke(userController);
					}

					// RequestDispatcher는 내부에서 동작하기 때문에 filter를 다시 타지 않음!
					// Dispatcher 설정 추가적인 등록 필요!!
					RequestDispatcher dis = request.getRequestDispatcher(path);
					dis.forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
		if(isMatching == false) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("잘못된 주소 요청입니다. 404 error");
			out.flush();
		}
	}
	
	private <T> void setData(T instance, HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames();  // login => username, password
		
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			// keys 값 변형 username => setUsername
			// keys 값 변형 password => setPassword
			String methodKey = keyToMethodKey(key);
			
			Method[] methods = instance.getClass().getDeclaredMethods();
			
			for(Method method : methods) {
				if(method.getName().equals(methodKey)) {
					try {
						if(key.equals("id")) {
							method.invoke(instance, Integer.parseInt(request.getParameter(key)));
						}else {
							method.invoke(instance, request.getParameter(key));
						}
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
