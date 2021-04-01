# Java Reflection

 - Reflection 개념 이해 및 필터 적용

## Java Reflection이란?

 - 구체적인 클래스 타입을 알지 못해도, 그 클래스의 메소드, 타입, 변수들에 접근 할 수 있도록 해주는 자바 API

## Why?

 - 자바는 정적인 언어라 부족한 부분이 많은데 이 동적인 문제를 해결하기위해 Reflection을 사용

 	정적 언어 : 컴파일 시점에 타입을 결정
 		ex) Java, C, C++ 등
 	동적 언어 : 런타임 시점에 타입을 결정
 		ex) Javascript, Python, Ruby 등

 - Reflection은 애플리케이션 개발에서 보다는 프레임워크, 라이브러리에서 많이 사용
   * 프레임워크, 라이브러리는 사용하는 사람이 어떤 클래스를 만들지 모른다. 이럴 때 동적으로 해결해주기 위해 리플렉션을 사용!
   	대표적인 사용 예로 스프링의 DI(Dependency Injection), Proxy, ModelMapper 등이 있다.

```java
public class UserController {    

	@PostMapping("/user/join")
	public String join(JoinDto dto) {
		...
	}

    	@PostMapping("/user/login")
	public String login(LoginDto dto) {
		...
	}

	...
}
```

 스프링에서는 @Controller 어노테이션을 사용하면 인스턴스를 생성하지 않아도 스프링에서 알아서 생성해서 Bean으로 관리
  * @Controller 어노테이션을 가지고있는 클래스 스캔
  * 해당 클래스의 인스턴스 생성 및 필드 DI

## How?

```java
public class UserController {    

	@PostMapping("/user/join")
	public String join(JoinDto dto) {
		...
	}

    	@PostMapping("/user/login")
	public String login(LoginDto dto) {
		...
	}

	...
}
```

UserController 클래스의 매소드 정보를 Reflection을 이용한다면

```java
Method[] methods = userController.getClass().getDeclaredMethods();  // userController 클래스의 매소드만 가져온다.

for (Method method : methods) {
	Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
	RequestMapping requestMapping = (RequestMapping)annotation;
	System.out.println(requestMapping.value());
}
```

출력 결과

```
/user/join
/user/login
...
```

UserController 클래스의 login parameters 정보를 Reflection을 이용한다면

```java
Parameter[] params = method.getParameters();

for(Parameter param : params){
	System.out.println(param.getType());
}
```

```
username
password
```

## 주의 사항

 - 이미 인스턴스를 만들었음에도 불구하고 굳이 필드와 리플렉션을 이용해서 접근하고나 사용할 경우 성능 이슈를 야기할 수 있다.
 - 컴파일 타임에 확인되지 않고 런타임 시에만 발생하는 문제를 만들 가능성이 있다.
 - 접근 지시자를 무시할 수 있다.
