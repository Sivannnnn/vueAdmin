package com.sivan.common.lang;

import lombok.Data;

import java.io.Serializable;

/***
 *  统一结果数据封装类
 */

// @Data注解的主要作用是提高代码的简洁，使用这个注解可以省去实体类中大量的get()、 set()、 toString()等方法。
@Data
// Serializable使序列化
public class Result implements Serializable {
    // 验证码
    private int code; // 200是正常，非200表示异常
    // 消息信息
    private String msg;
    // 数据信息
    private Object data;

    public static Result succ(int code,String msg,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 成功返回状态码200、成功信息和数据信息
    public static Result succ(Object data) {
        return succ(200,"操作成功",data);
    }

    public static Result fail(int code,String msg,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    // 失败返回状态码400、失败信息，没信息可返回，data设置未'null'
    public static Result fail(String msg) {
        return fail(400,msg,null);
    }
}
