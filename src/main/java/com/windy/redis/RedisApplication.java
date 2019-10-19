package com.windy.redis;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sun.misc.ObjectInputFilter;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }


    @Bean
    public Redisson redisson(){
        //此为单机模式
        Config config=new Config();
        config.useSingleServer().setAddress("redis://120.78.169.196:6379").setDatabase(0);
        return (Redisson)Redisson.create(config);
    }
}
