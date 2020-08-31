package org.seckill.exception;

/**
 * 秒杀相关的所有业务异常
 * @author malaka
 * @create 2020-08-19 15:57
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message){
        super(message);
    }

    public SeckillException(String message, Throwable cause){
        super(message, cause);
    }

}
