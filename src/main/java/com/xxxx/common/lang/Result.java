package com.xxxx.common.lang;

import lombok.Data;

@Data
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public Result() {}

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<T>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = Result.success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail() {
        return new Result<T>(500, "操作失败", null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>(500, msg, null);
    }

    public static <T> Result<T> fail(int code,String msg) {
        return new Result<T>(code, msg, null);
    }
}
