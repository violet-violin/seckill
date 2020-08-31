package org.seckill.exception;

/**
 * @author malaka
 * @create 2020-08-19 16:01
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
