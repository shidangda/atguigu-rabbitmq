package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 生产者
 */
@Component
public class Consumer {

    //队列名称
    public static final String QUEUE_NAME = "hello";

    //接收消息
    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.20.129");
        factory.setUsername("admin");
        factory.setPassword("admin");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();

        //声明接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            System.out.println(new String(delivery.getBody()));
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断" + consumerTag);
        };

        /**
         * 消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答  false代表手动应答
         * 3.消费者成功消费的回调函数
         * 4.消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }

}
