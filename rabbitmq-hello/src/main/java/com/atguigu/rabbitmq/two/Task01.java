package com.atguigu.rabbitmq.two;

import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 生产者
 */
public class Task01 {

    //队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = Worker01.RabbitMqUtils.getChannel();

        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化（磁盘） 默认情况消息存储在内存中
         * 3.该队列是否只供一个消费者消费 是否进行消息共享，true可以供多个消费者消费 false：只能一个消费者消费
         * 4.是否自动删除 最后一个消费者断开连接以后 该队列是否自动删除 true自动删除 false不自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME,null, message.getBytes());
        }


    }
}
