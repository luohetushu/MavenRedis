package com.redis.project.utils;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 使用 Redis 连接池来获取 Jedis 实例
 */
public class RedisUtils {

    //服务器IP地址
    private static String ADDR = "127.0.0.1";  // localhost
    //端口
    private static int PORT = 6379;
    //密码
    private static String AUTH = "123456";
    //连接实例的最大连接数
    private static int MAX_ACTIVE = 20;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 8;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
    private static int MAX_WAIT = 10000;
    //连接超时的时间　　
    private static int TIMEOUT = 10000;
    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    //数据库模式是16个数据库 0~15
    public static final int DEFAULT_DATABASE = 0;

    private static JedisPool jedisPool = null;

    //java.lang.ThreadLocale<T> 类：
    //当有多个线程在进行各自信息处理，很有可能出现线程信息处理交错的情况，此时就需要使用 ThreadLocale 类给各个线程标记
    //创建 ThreadLocal 对象，给 redis.clients.jedis.Jedis 对象标记
    private static ThreadLocal<Jedis> threadLocal = new ThreadLocal<Jedis>();

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            //jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH, DEFAULT_DATABASE);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     */
    public static Jedis getJedis(){
        try {
            Jedis jedis = threadLocal.get();
            if (jedis == null){
                jedis = jedisPool.getResource();
                System.out.println("连接成功");
                //查看服务是否运行
                System.out.println("服务正在运行: " + jedis.ping());
                threadLocal.set(jedis);
            }
            return jedis;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放资源
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
        if (threadLocal.get() == jedis){
            threadLocal.remove();
        }
    }

}
