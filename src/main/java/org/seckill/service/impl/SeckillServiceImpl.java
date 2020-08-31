package org.seckill.service.impl;


import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.seckill.enums.SeckillStatEnum.SUCCESS;

/**
 * service接口的实现类
 *
 * @author malaka
 * @create 2020-08-19 16:02
 */
@Service//采用注解的方式将对应Service的实现类加入到Spring IOC容器中:
public class SeckillServiceImpl implements SeckillService {

    //首先引入一些要用的对象
    //日志对象；用的slf4j
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖；所有的Dao实现类都在spring容器中
    @Autowired    //@Resource  区别？？
    private SeckillDao seckillDao;

    @Autowired  //@Resource   ??
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //加入一个混淆字符串(秒杀接口)的salt，为了避免用户猜出我们的md5值，
    // 值任意给，越复杂越好；md5盐值字符串，用于混淆md5
    private final String salt = "fahsiodhfkasjf.jafoiu983258&&%&uwqkjro";


    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 在秒杀开启时输出秒杀接口的地址，否则输出系统时间和秒杀时间
     *
     * @param seckillId
     * @return
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化:超时的基础上维护一致性？
        /**
         * 所有的秒杀单都要调用地址暴露接口，即下面的seckillDao.queryById(seckillId);
         * 故用redis把它缓存起来，可以优化
         * 伪码————
         * get from cache
         * if null
         *  get db
         *  else
         *      put cache
         *  locgoin
         *
         *  缓存不能直接放在这里，放在Dao层
         */
        //1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {  //说明查不到这个秒杀产品的记录
                return new Exposer(false, seckillId);
            } else {
                //3.放入redis
                redisDao.putSeckill(seckill);
            }
        }


        //用从redis/数据库拿到的seckill判断秒杀是否开始
        //若是秒杀未开启
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间；  java.util
        Date nowTime = new Date();

        //秒杀未开启，或已经结束
        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(),
                    startTime.getTime(), endTime.getTime());
        }

        //md5——根据某字符串生成标准的MD5字符串；还不可逆
        //秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5 = getMD5(seckillId);


        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        //DigestUtils——spring工具类，生成MD5
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    //完成执行秒杀方法的实现
    //秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
    @Override
    //秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
    @Transactional
    /**
     * 使用注解控制事务方法的优点:
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部（这些耗时很长，几毫秒）
     * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制；
     * 例如如果没有该方法内部的seckillDao.reduceNumber() + successKilledDao.insertSuccessKilled()
     * 这种事务，就不用加@Transactionnal
     *      //如果用aop那种一次配置，所有运行的容易搞忘谁是事务方法
     */

    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");//秒杀数据被重写了
        }

        //以下部分——执行秒杀逻辑:减库存+增加购买明细
        Date nowTime = new Date();

        try {

            //否则更新了库存，秒杀成功,增加明细，记录购买行为，此处执行了插入
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //看是否该明细被重复插入，即用户是否重复秒杀
            if (insertCount <= 0) {
                //代表重复秒杀了
                throw new RepeatKillException("seckill repeated");
            } else {

                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新库存记录（其实很多原因导致未更新,都可以当作秒杀结束处理），说明秒杀结束；rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);  //啥？
            //把所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error :" + e.getMessage());//spring声明式事务会帮我们执行rollback？
        }

    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStatEnum.DATE_REWRITE);
        }

        Date killTime = new Date();
        HashMap<String, Object> map = new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被复制
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }
}
