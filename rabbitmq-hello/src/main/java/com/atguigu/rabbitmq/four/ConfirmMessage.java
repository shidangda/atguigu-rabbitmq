package com.atguigu.rabbitmq.four;

import com.atguigu.rabbitmq.two.Worker01;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1、单个确认
 * 2、批量确认
 * 3、异步批量确认
 *
 */
public class ConfirmMessage {

    // 批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;


    public static void main(String[] args) throws Exception {
        //1、单个确认
//        publishMessageIndividually();
        //2、批量确认
//        publishMessageBatch();

        //3、异步批量确认
        publishMessageAsync();
    }

    //单个确认
    public static void publishMessageIndividually() throws Exception {
        Channel channel = Worker01.RabbitMqUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时" + (end - begin) +"ms");

    }

    //批量确认
    public static void publishMessageBatch() throws Exception {
        Channel channel = Worker01.RabbitMqUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认大小
        int batchSize = 100;

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());

            //判断达到100条消息的时候，批量确认一次
            if (i%batchSize == 0) {
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");
                }
            }
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息，耗时" + (end - begin) +"ms");
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = Worker01.RabbitMqUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();

        /**
         *
         * 1.将序号和消息进行关联
         * 2.轻松批量删除条目
         * 3.支持高并发（多线程）
         *
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        //消息确认成功 回调函数
        /**
         * 1.消息的编号
         * 2.是否为批量确认
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //2.删除已经确认的消息 剩下的就是未确认的消息
            if (multiple) {
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(deliveryTag);
            }

            System.out.println("确认的消息：" + deliveryTag +
                    " 当前线程为：" + Thread.currentThread().getName() + " 线程ID为：" +Thread.currentThread().getId());
        };

        //消息确认失败 回调函数
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //3、打印一下未确认的消息都有哪些
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息是：" + message + "  未确认的消息tag：" + deliveryTag);

        };
        //准备消息的监听器 监听哪些消息成功了 哪些消息失败了
        /**
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallback, nackCallback);

        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            channel.basicPublish("", queueName, null, message.getBytes());
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息，耗时" + (end - begin) +"ms");
        System.out.println("发送线程为：" + Thread.currentThread().getName() + " 线程ID为：" +Thread.currentThread().getId());
    }


}
