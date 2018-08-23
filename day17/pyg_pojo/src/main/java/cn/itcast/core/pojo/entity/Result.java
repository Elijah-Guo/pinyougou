package cn.itcast.core.pojo.entity;

import java.io.Serializable;

public class Result implements Serializable{
    //是否成功, 成功true, 失败false
    private boolean success;
    //提示信息
    private String message;
    public Result(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
