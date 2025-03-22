package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.two.Worker01;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Task2 {

    //队列名称
    public static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = Worker01.RabbitMqUtils.getChannel();

        //声明队列
        boolean durable = true; //需要让Queue进行持久化

        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        channel.confirmSelect();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要发送的消息：");
        while (scanner.hasNext()) {
            String message = scanner.next();
            //设置生产者发送消息为持久化消息（要求保存到磁盘上）
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);
        }
    }
}
