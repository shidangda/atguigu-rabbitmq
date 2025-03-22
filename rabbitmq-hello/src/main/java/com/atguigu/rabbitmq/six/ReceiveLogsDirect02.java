package com.atguigu.rabbitmq.six;

import com.atguigu.rabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 直接交换机
 */
public class ReceiveLogsDirect02 {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列 临时队列
        channel.queueDeclare("disk", false, false, false, null);

        /**
         * 绑定交换机与队列
         */
        channel.queueBind("disk", EXCHANGE_NAME, "error");
        System.out.println("等待接收消息，把接收到消息打印在屏幕上...");

        //接受消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            System.out.println("ReceiveLogsDirect02控制台打印接收到的消息：" + new String(delivery.getBody()));
        };

        channel.basicConsume("disk", true, deliverCallback, consumerTag -> {});
    }
}
