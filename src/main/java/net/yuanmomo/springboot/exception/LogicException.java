package net.yuanmomo.springboot.exception;

/**
 *
 * 业务异常。
 *
 * Created by Hongbin.Yuan on 2015-10-13 00:21.
 */

public class LogicException extends BaseException {

    private static final long serialVersionUID = -7071959784876014656L;

    public LogicException(int code, String message) {
        super(code, message);
    }
}
