package com.sushi.api.entity.order;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrderControllerAspect {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
//	@Before("execution(* com.monomono.order.OrderController.*(..))")
//	public void before(JoinPoint joinPoint){
//		
//		if(joinPoint.getArgs()!=null && joinPoint.getArgs().length>0) {
//			Object tokenObj = joinPoint.getArgs()[0];
//			
//			if(tokenObj!=null && tokenObj.toString()!=null && tokenObj.toString().length()>0) {
//				//log.debug(" auth token {} ", tokenObj.toString());
//				ApiSessionUtils.setRequestSecurityAuthentication(tokenObj.toString());
//			}
//		}
//		
//		
//	}
}
