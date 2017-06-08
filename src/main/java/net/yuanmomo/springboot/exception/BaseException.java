package net.yuanmomo.springboot.exception;

/**
 * Created by Hongbin.Yuan on 2015-10-13 00:18.
 */

public abstract class BaseException extends Exception {
    private static final long serialVersionUID = 8923954512584864632L;
    private int code;

    private String key;

    public BaseException(int code, String key){
        this.code = code;
        this.key = key;
    }


    public int getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }
}
