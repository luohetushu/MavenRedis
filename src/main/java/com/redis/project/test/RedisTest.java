package com.redis.project.test;

import com.redis.project.utils.RedisUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisTest {
    //连接本地 Redis 服务
    // todo 需要事先本地安装好 Redis 服务，并启动 redis-server
    @Test
    public void connectRedis(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());
    }

    // 存储并读取 String(字符串)
    @Test
    public void setGetString(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //设置 redis 字符串数据
        jedis.set("riderKey", "Hesin Kamen Rider");
        // 获取存储的数据并输出
        System.out.println("redis 存储的字符串为: "+ jedis.get("riderKey"));
    }

    // 存储并读取 Hash（哈希）
    @Test
    public void setGetHash(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //设置 Hash（哈希）
        Map<String, String> maps = new HashMap<>();
        maps.put("field1", "Drive");
        maps.put("field2", "Ghost");
        maps.put("field3", "Wizard");
        jedis.hmset("riders", maps);
        // 获取存储的数据并输出
        jedis.hget("riders", "field1");  // Drive
        Map<String, String> getMaps = jedis.hgetAll("riders");
    }

    // 存储并读取 List 集合
    @Test
    public void setGetList(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //存储数据到列表中
        // todo 错误：JedisDataException: WRONGTYPE Operation against a key holding the wrong kind of value
        //      原因：估摸着是前面的测试，set 存储使用了相同的 key
        // jedis.del("riderKey");  // 删除存在的键
        jedis.lpush("ridersKey", "Decade");
        jedis.lpush("ridersKey", "Wizard");
        jedis.lpush("ridersKey", "Drive");
        // 获取存储的数据并输出
        List<String> lists = jedis.lrange("ridersKey", 0, 2);
        for (String str : lists){
            System.out.print(str + "、");
        }
    }

    //存储并读取 set 集合
    @Test
    public void setGetSet(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //存储数据到 set 集合中
        jedis.sadd("riderSet", "OOO");
        jedis.sadd("riderSet", "Wizard");
        jedis.sadd("riderSet", "Ex-aid");
        //读取 set 集合中所有元素
        Set<String> riders = jedis.smembers("riderSet");
    }

    //存储并读取有序 sorted set 集合
    @Test
    public void setGetSortedSet(){
        //连接本地 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //存储数据到有序 sorted set 集合中
        // redis 依据分数来为集合中的成员进行从小到大的排序
        jedis.zadd("riderZSet", 10, "Decade");
        jedis.zadd("riderZSet", 12, "OOO");
        jedis.zadd("riderZSet", 16, "Drive");
        jedis.zadd("riderZSet", 14, "Wizard");
        //读取有序 sorted set 集合中指定分数间的所有元素
        Set<String> riders = jedis.zrangeByScore("riderZSet", 10, 14);
    }

    //获取前面测试存储的 key
    @Test
    public void getKeys(){
        //连接本地 Redis 服务
        //Jedis jedis = new Jedis("localhost");
        //System.out.println("连接成功");
        Jedis jedis = RedisUtils.getJedis();
        if (jedis != null) {
            //设置的密码
            //jedis.auth("123456");
            //获取前面测试存储的 key
            Set<String> keys = jedis.keys("*");  //
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                System.out.print(key + "、");  // riderKey、ridersKey、
            }
            //释放资源
            RedisUtils.close(jedis);
        }
    }
}
