package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.two.Worker01;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Work03 {

    //队列名称
    public static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Runnable runnable = () -> {
            Channel channel = null;
            try {
                channel = Worker01.RabbitMqUtils.getChannel();

                System.out.println("线程:" + Thread.currentThread().getId() + "等待接受消息处理时间较短");

                //声明接收消息
                DeliverCallback deliverCallback = getDeliverCallback(channel, 1000);

                //采用手动应答
//                int prefectchCount = 1;
                int prefectchCount = 5;
                channel.basicQos(prefectchCount);

                boolean autoAck = false;
                /**
                 * 启动一个消费者，并返回服务端生成的消费者标识
                 * queue:队列名
                 * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
                 * deliverCallback： 当一个消息发送过来后的回调接口
                 * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
                 * @return 服务端生成的消费者标识
                 */
                channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable runnable2 = () -> {
            Channel channel = null;
            try {
                channel = Worker01.RabbitMqUtils.getChannel();

                System.out.println("线程:" + Thread.currentThread().getId() + "等待接受消息处理时间较长");

                //声明接收消息
                DeliverCallback deliverCallback = getDeliverCallback(channel, 30000);

                //采用手动应答
                boolean autoAck = false;
//                int prefectchCount = 1;
                int prefectchCount = 5;
                channel.basicQos(prefectchCount);
                channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Thread worker01 = new Thread(runnable);
        Thread worker02 = new Thread(runnable2);

        worker01.start();
        worker02.start();
    }

    private static DeliverCallback getDeliverCallback(Channel channel, int millis) {
        Channel finalChannel = channel;
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("线程:" + Thread.currentThread().getId() + "接受到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));

            // 手动应答
            /**
             * 1.消息的标记 tag
             * 2.是否批量应答 false：不批量应答信道中的消息  true：批量
             *
             */
            finalChannel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        return deliverCallback;
    }
}
