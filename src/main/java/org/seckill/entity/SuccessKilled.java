package org.seckill.entity;

import java.util.Date;

/**
 * @author malaka
 * @create 2020-08-13 11:01
 */
public class SuccessKilled {

    private long seckillId;
    private long userPhone;
    private short state;     //0、1、2代表什么？？
    private Date createTime;

    //变通
    //多对一的复合属性，因为一件商品在库存中有很多数量，对应的购买明细也有很多。
    //DAO时用？
    private Seckill seckill;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }

}
