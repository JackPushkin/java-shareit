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
public class ItemLoggingAspect {

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.addItem(..))")
    private void addItemMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.getItemById(..))")
    private void getItemMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.getUserItems(..))")
    private void getUserItemsMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.updateItem(..))")
    private void updateItemMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.deleteItem(..))")
    private void deleteItemMethod() {
    }

    @Pointcut("execution(* ru.practicum.shareit.item.controller.ItemController.searchItems(..))")
    private void searchItemsMethod() {
    }

    @Around(value = "addItemMethod()")
    public Object aroundAddItemAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Пользователь id={}. Попытка добавить вещь {}", joinPoint.getArgs()[1], joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Вещь добавлена {}.", result);
        return result;
    }

    @Around(value = "getItemMethod()")
    public Object aroundGetItemAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка получить вещь id={}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Вещь получена {}", result);
        return result;
    }

    @Around(value = "getUserItemsMethod()")
    public Object aroundGetUserItemsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка получить вещи пользователя id={}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Вещи получены {}", result);
        return result;
    }

    @Around(value = "updateItemMethod()")
    public Object aroundUpdateItemAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Пользователь id={}. Попытка обновить вещь id={}", joinPoint.getArgs()[2], joinPoint.getArgs()[1]);
        Object result = joinPoint.proceed();
        log.info("Данные успешно обновлены {}.", result);
        return result;
    }

    @Around(value = "deleteItemMethod()")
    public Object aroundDeleteItemAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка удалить вещь id={}", joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        log.info("Вещь успешно удалена {}.", result);
        return result;
    }

    @Around(value = "searchItemsMethod()")
    public Object aroundSearchItemsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Попытка получить список вещей соответствующих параметру text={}", joinPoint.getArgs()[1]);
        Object result = joinPoint.proceed();
        log.info("Список вещей успешно получен {}.", result);
        return result;
    }

    @AfterThrowing(
            value = "addItemMethod() || getItemMethod() || getUserItemsMethod() || " +
                    "updateItemMethod() || deleteItemMethod()",
            throwing = "e")
    public void afterThrowingItemAdvice(Exception e) {
        log.warn(e.getMessage());
    }
}
