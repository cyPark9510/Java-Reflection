package com.pcy.reflect.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})  // 동작 위치 지정 
@Retention(RetentionPolicy.RUNTIME)  // 런타임시에 어노테이션 동작 
public @interface RequestMapping {

	String value();
}
