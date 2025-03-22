package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Worker01 {

    //队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {


        Runnable runnable = () -> {
            //消息的接收
            try {
                Channel channel = RabbitMqUtils.getChannel();

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    System.out.println(new String(delivery.getBody()));
                };

                CancelCallback cancelCallback = (consumerTag) -> {
                    System.out.println("消息消费被中断" + consumerTag);
                };

                System.out.println("线程:" + Thread.currentThread().getId() + "正在等待消费消息....");
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
                System.out.println("线程:" + Thread.currentThread().getId() + "消费消息成功！");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Thread worker01 = new Thread(runnable);
        Thread worker02 = new Thread(runnable);

        worker01.start();
        worker02.start();
    }

    public void useRabbitMq() throws Exception {
        com.atguigu.rabbitmq.utils.RabbitMqUtils.getChannel();
        System.out.println("静态方法测试！");
    }


    public static class RabbitMqUtils {
        private Consumer consumer;

        public static Consumer getConsumer() {
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            return applicationContext.getBean(Consumer.class);
        }

        public static Channel getChannel() throws Exception {
            //创建一个连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("47.121.29.230");
            factory.setUsername("admin");
            factory.setPassword("admin");

            //创建连接
            Connection connection = factory.newConnection();
            //获取信道
            Channel channel = connection.createChannel();
            return channel;
        }
    }
}
