package com.jwh.dobotochatserver.util;

import com.alibaba.fastjson.JSON;

//Result Message For JSON
//目的：通过工具类将数据与传输状态封装为统一的JSON数据格式发送给前端，方便前端解析
//结构内容：1.响应状态信息state（200/请求成功/301/资源转移URL/404/资源未找到/500/服务器内部错误，通常可以用作处理异常返回信息）简单的处理为SUCCESS和ERROR
//        2.响应数据data，如果状态是200，则说明内部有响应数据，如果是其他，则data中是“”""空字符串
//        3。错误信息message，如果是成功，则message中显示相应的信息，如果是错误，message中也会有相关信息，这些信息可有前端的Feedback展现出来
//整个的Result的toJsonString，是通过fast json来实现。
public class DResultMsg {

    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    private int state;
    private Object data;
    private String message;
    private int pageCount;
    private int pageSize;
    private int total;


    public DResultMsg() {
        super();
    }

    public DResultMsg(int state) {
        if (state == DResultMsg.SUCCESS) {
            this.state = state;
            this.data = "null";
            this.message = "success";
            this.pageCount=0;
            this.pageSize=0;
            this.total=0;
        } else {
            this.state = state;
            this.data = "null";
            this.message = "error";
            this.pageCount=0;
            this.pageSize=0;
            this.total=0;
        }
    }

    public DResultMsg(int state, String message) {
        this.state = state;
        this.data = null;
        this.message = message;
        this.pageCount=0;
        this.pageSize=0;
        this.total=0;
    }


    public DResultMsg(int state, Object data, String message) {
        this.state = state;
        this.data = data;
        this.message = message;
        this.pageCount=0;
        this.pageSize=0;
        this.total=0;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public Object toJsonObject() {
        return JSON.toJSON(this);
    }
    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
