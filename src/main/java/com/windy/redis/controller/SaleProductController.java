package com.windy.redis.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: windy
 * @create: 2019-10-14 20:54
 */
@Controller
@RequestMapping("/product")
public class SaleProductController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Redisson redisson;

    /**
     * 销售商品，扣减库存
     */
//    @RequestMapping("/sale")
//    @ResponseBody
//    public String sale(){
//        int store=Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
//        if(store>0){
//            int resultStock=store-1;
//            stringRedisTemplate.opsForValue().set("stock",resultStock+"");
//            System.out.println("扣减成功，剩余库存"+resultStock);
//        }else{
//            System.out.println("扣减失败，库存不足");
//        }
//        return "success";
//    }

    /**
     * 销售商品，扣减库存（优化版本1：加锁）
     */
//    @RequestMapping("/saleSyn")
//    @ResponseBody
//    public String saleSyn(){
//        synchronized (this){
//            int store=Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
//            if(store>0){
//                int resultStock=store-1;
//                stringRedisTemplate.opsForValue().set("stock",resultStock+"");
//                System.out.println("扣减成功，剩余库存"+resultStock);
//            }else{
//                System.out.println("扣减失败，库存不足");
//            }
//        }
//        return "success";
//    }


    /**
     * 销售商品，扣减库存（优化版本2：加redis分布式锁）
     */
    @RequestMapping("/sale-RedisLock")
    @ResponseBody
    public String saleRedisLock(){
        String lockKey="product_1";
        RLock redissonLock = redisson.getLock(lockKey);
        try {
            redissonLock.lock(30,TimeUnit.SECONDS);
            int store=Integer.parseInt(stringRedisTemplate.opsForValue().get("stock").toString());
            if(store>0) {
                int resultStock = store - 1;
                stringRedisTemplate.opsForValue().set("stock", resultStock + "");
                System.out.println("扣减成功，剩余库存" + resultStock);
            }else{
                System.out.println("扣减失败，库存不足");
            }
        }finally {
            //如果为false就说明该线程的锁已经自动释放，无需解锁
            if (redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
        return "success";
    }

}
