package com.lagou.minicat.exceptions;

/**
 * MinicatException
 *
 * @author wlz
 * @date 2020/5/12
 */
public class MinicatException extends RuntimeException {
    public MinicatException(String message) {
        super(message);
    }

    public MinicatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinicatException(Throwable cause) {
        super(cause);
    }
}
