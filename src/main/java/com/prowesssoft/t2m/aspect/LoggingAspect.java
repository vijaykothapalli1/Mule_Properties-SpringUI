package com.prowesssoft.t2m.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Before("execution(* com.prowesssoft..*.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//		String className = methodSignature.getDeclaringType().getSimpleName();
//		String methodName = methodSignature.getMethod().getName();
//        //logger.info("Method execution started: {}", joinPoint.getSignature());
//        logger.info("Method execution started : {}.{}() with argument[s] = {}", className, methodName,
//				Arrays.toString(joinPoint.getArgs()));
//    }
//
//    @After("execution(* com.prowesssoft..*.*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//        logger.info("Method execution completed: {}", joinPoint.getSignature());
//    }
    
	@Around("execution(* *..controller..*.*(..))")
	public Object logControllerCalls(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getMethod().getName();

		logger.info("START Controller : {}.{}() with argument[s] = {}", className, methodName,
					Arrays.toString(proceedingJoinPoint.getArgs()));
		try {
			Object result = proceedingJoinPoint.proceed();
			logger.info("END Controller : {}.{}() with result = {}", className, methodName, result);
			return result;
		} catch (Exception e) {
			logger.error("END Controller with Error : {} in {}.{}()", e.getMessage(),
					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
					proceedingJoinPoint.getSignature().getName());
			throw e;
		}
	}
	
	@Around("execution(* *..service..*.*(..))")
	public Object logServiceCalls(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getMethod().getName();

		logger.info("START Service : {}.{}() with argument[s] = {}", className, methodName,
					Arrays.toString(proceedingJoinPoint.getArgs()));
		try {
			Object result = proceedingJoinPoint.proceed();
			logger.info("END Service : {}.{}() with result = {}", className, methodName, result);
			return result;
		} catch (Exception e) {
			logger.error("END Service with Error : {} in {}.{}()", e.getMessage(),
					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
					proceedingJoinPoint.getSignature().getName());
			throw e;
		}
	}
	
//	@Around("execution(* *..repository..*.*(..))")
//	public Object logRepositoryCalls(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
//		String className = methodSignature.getDeclaringType().getSimpleName();
//		String methodName = methodSignature.getMethod().getName();
//
//		logger.info("START Repository : {}.{}() with argument[s] = {}", className, methodName,
//					Arrays.toString(proceedingJoinPoint.getArgs()));
//		try {
//			Object result = proceedingJoinPoint.proceed();
//			logger.info("END Repository : {}.{}() with result = {}", className, methodName, result);
//			return result;
//		} catch (Exception e) {
//			logger.error("END Repository with Error : {} in {}.{}()", Arrays.toString(proceedingJoinPoint.getArgs()),
//					proceedingJoinPoint.getSignature().getDeclaringTypeName(),
//					proceedingJoinPoint.getSignature().getName());
//			throw e;
//		}
//	}

}