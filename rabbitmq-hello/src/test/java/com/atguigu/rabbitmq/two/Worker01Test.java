package com.atguigu.rabbitmq.two;

import com.rabbitmq.client.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Worker01Test {
    @Mock
    AnnotationConfigApplicationContext applicationContext;

    @Mock
    Consumer consumer;

    private Worker01 worker01UnderTest;

    @BeforeEach
    void setUp() {
        worker01UnderTest = new Worker01();
    }

    @Test
    void testMain() throws Exception {
        // Setup
        // Run the test
        Worker01.main(new String[]{"args"});

        // Verify the results
    }

    @Test
    void testMain_ThrowsException() {
        // Setup
        // Run the test
        assertThatThrownBy(() -> Worker01.main(new String[]{"args"})).isInstanceOf(Exception.class);
    }

    @Test
    void testUseRabbitMq() throws Exception {
        // Setup
        // Run the test
        worker01UnderTest.useRabbitMq();

        // Verify the results
    }

    @Test
    void testUseRabbitMq_ThrowsException() {
        // Setup
        // Run the test
        assertThatThrownBy(() -> worker01UnderTest.useRabbitMq()).isInstanceOf(Exception.class);
    }

    @Test
    void testStaticInnerGetConsummer() throws Exception {
        when(new AnnotationConfigApplicationContext()).thenReturn(applicationContext);
        when(applicationContext.getBean(Consumer.class)).thenReturn(consumer);
        Worker01.RabbitMqUtils.getConsumer();

        verify(applicationContext).getBean(Consumer.class);
    }
}
