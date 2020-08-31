package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.springframework.stereotype.Repository;

/**
 * @author malaka
 * @create 2020-08-13 11:04
 */
@Repository
public interface SuccessKilledDao {


    /**
     * 插入购买明细,可过滤重复     //??啥
     * @param seckillId
     * @param userPhone
     * @return   插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);


    /**
     * 根据秒杀商品的id、用户手机号查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

}
