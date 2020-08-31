package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author malaka
 * @create 2020-08-20 13:20
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                         "classpath:spring/spring-service.xml"})
//spring-service.xml 依赖于 spring-dao.xml
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    //查询商品列表
    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
        System.out.println();
        System.out.println(list);
        //Closing non transactional SqlSession；不是在事务控制下
    }

    @Test
    public void getById() {
        long seckillId=1000;
        Seckill seckill=seckillService.getById(seckillId);
        logger.info("seckill={}", seckill);
        System.out.println();
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer={}", exposer);
        System.out.println();
        System.out.println(exposer);  //记得重写Exposer的toStrig

        //exposer=Exposer{exposed=true,
        // md5='b76281abc462a7153d6d310f20fce523',
        // seckillId=1000,
        // now=0, start=0, end=0}

    }

    @Test
    public void executeSeckill() {
        long seckillId=1000;
        long userPhone=13476191879L;
        String md5="b76281abc462a7153d6d310f20fce523";

        try {
            SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);
            logger.info("result={}", seckillExecution);
            System.out.println();
            System.out.println(seckillExecution);
        } catch (RepeatKillException e)
        {
            e.printStackTrace();
        }catch (SeckillCloseException e1)
        {
            e1.printStackTrace();
        }


        //result=SeckillExecution{
        // seckillId=1000,
        // state=1,
        // stateInfo='秒杀成功',
        // successKilled=SuccessKilled{
        //                  seckillId=1000,
        //                  userPhone=13476191879,
        //                  state=0,
        //                  createTime=Thu Aug 20 15:42:30 CST 2020}}

    }


    @Test//完整逻辑代码测试，注意可重复执行 ；  上面那两个test应该注释掉，并放在一起执行
    public void testSeckillLogic() throws Exception {
        long seckillId=1001;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed())
        {

            System.out.println(exposer);

            long userPhone=13476191876L;
            String md5=exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            }catch (RepeatKillException e)
            {
                e.printStackTrace();
            }catch (SeckillCloseException e1)
            {
                e1.printStackTrace();
            }
        }else {
            //秒杀未开启
            System.out.println(exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1001;
        long phone = 1368011101;

        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }

    }


}