package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author malaka
 * @create 2020-08-28 10:53
 */
//spring的Runwith接口——junit在启动时就会加载spring容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest{

    private long id = 1001;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception{
        //全局测试  get and put
        Seckill seckill = redisDao.getSeckill(id);
        System.out.println(seckill);
        if(seckill == null){//缓存没有，就去数据库取
            seckill = seckillDao.queryById(id);
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);//把数据库中取出的放入缓存
                System.out.println(result);
                seckill = redisDao.getSeckill(id);//再从缓存里拿出来一次
                System.out.println(seckill);

            }
        }


    }


}