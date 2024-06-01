package ru.practicum.shareit.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class UserLoggingAspect {

    @Pointcut("execution(* ru.practicum.shareit.user.controller.UserController.addUser(..))")
    private void addUserMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.user.controller.UserController.getUserById(..))")
    private void getUserMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.user.controller.UserController.updateUser(..))")
    private void updateUserMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.user.controller.UserController.deleteUser(..))")
    private void deleteUserMethod() {
    }

    @Around(value = "addUserMethod()")
    public Object aroundAddUserAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка добавить пользователя {}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Пользователь добавлен {}.", result);
        return result;
    }

    @Around(value = "getUserMethod()")
    public Object aroundGetUserAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка получить пользователя с id={}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Пользователь получен {}", result);
        return result;
    }

    @Around(value = "updateUserMethod()")
    public Object aroundUpdateUserAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка обновить пользователя {}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Данные пользователя успешно обновлены {}.", result);
        return result;
    }

    @Around(value = "deleteUserMethod()")
    public Object aroundDeleteUserAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка удалить пользователя с id={}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Пользователь успешно удален {}.", result);
        return result;
    }

    @AfterThrowing(
            value = "addUserMethod() || getUserMethod() || updateUserMethod() || deleteUserMethod()",
            throwing = "e")
    public void afterThrowingUserAdvice(Exception e) {
        log.warn(e.getMessage());
    }
}
