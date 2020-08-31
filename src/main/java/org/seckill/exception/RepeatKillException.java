package org.seckill.exception;

/**
 * 重复秒杀异常，是一个运行期异常，不需要我们手动try catch
 * Spring的声明式事务——只支持运行期异常的回滚策略，非运行时异常不会回滚
 * @author malaka
 * @create 2020-08-19 15:56
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
