package com.sivan.common.exception;

import com.sivan.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */

// @Slf4j是用作日志输出的
@Slf4j
// @RestControllerAdvice都是对Controller进行增强的，可以全局捕获spring mvc抛的异常。
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 实体类校验异常
    // @ResponseStatus的作用就是为了改变HTTP响应的状态码，则返回失败状态码
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        // 获取异常转成流
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();
        // 日志打印异常信息
        log.error("实体校验异常：----------------{}", objectError.getDefaultMessage());
        // 返回异常信息
        return Result.fail(objectError.getDefaultMessage());
    }

    // 非法数据
    // @ResponseStatus的作用就是为了改变HTTP响应的状态码，则返回失败状态码
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        // 日志打印异常信息
        log.error("Assert异常：----------------{}", e.getMessage());
        // 返回异常信息
        return Result.fail(e.getMessage());
    }

    // @ResponseStatus的作用就是为了改变HTTP响应的状态码，则返回失败状态码
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // @ExceptionHandler的作用是用来捕获指定的异常。
    // 先捕获RuntimeException.class，因为大部分Exception都是RuntimeException.class的子类
    @ExceptionHandler(value = RuntimeException.class)
    public Result handller(RuntimeException e){
        // 日志打印异常信息
        log.error("运行时异常：----------------{}", e.getMessage());
        // 返回异常信息
        return Result.fail(e.getMessage());
    }
}