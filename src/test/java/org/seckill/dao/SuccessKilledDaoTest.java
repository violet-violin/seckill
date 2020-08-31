package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author malaka
 * @create 2020-08-18 15:18
 */
//spring的Runwith接口——junit在启动时就会加载spring容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    //接口的实现类
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        long seckillId=1001L;
        long userPhone=13476191877L;

        int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println(insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long seckillId=1001L;
        long userPhone=13476191877L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);

        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());

    }
}