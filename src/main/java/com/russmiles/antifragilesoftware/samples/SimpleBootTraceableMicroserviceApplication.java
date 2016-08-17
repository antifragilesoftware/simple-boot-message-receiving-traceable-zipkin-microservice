package com.russmiles.antifragilesoftware.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableBinding(Sink.class)
@IntegrationComponentScan
public class SimpleBootTraceableMicroserviceApplication {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }

    @Bean
    Sampler sampler() {
        return span -> true;
    }

    int invocationTimes = 0;

    @RequestMapping("/")
    public String home() {
        log.info("Root URL invoked");

        return this.toString() + " instance saying: Invoked " + invocationTimes + "\n";
    }

//    @Bean
//    public MessageChannel amqpInputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public AmqpInboundGateway inbound(SimpleMessageListenerContainer listenerContainer,
//                                      @Qualifier("amqpInputChannel") MessageChannel channel) {
//        AmqpInboundGateway gateway = new AmqpInboundGateway(listenerContainer);
//        gateway.setRequestChannel(channel);
//        gateway.setDefaultReplyTo("bar");
//        return gateway;
//    }

//    @Bean
//    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer container =
//                new SimpleMessageListenerContainer(connectionFactory);
//        container.setQueueNames("amqpMessageChannel");
//        container.setConcurrentConsumers(2);
//        // ...
//        return container;
//    }

//    @Bean
//    @ServiceActivator(inputChannel = "amqpInputChannel")
//    public MessageHandler handler() {
//        return new AbstractReplyProducingMessageHandler() {
//
//            @Override
//            protected Object handleRequestMessage(Message<?> requestMessage) {
//                log.info("Received message " + requestMessage.getPayload());
//                invocationTimes ++;
//                return "reply to " + requestMessage.getPayload();
//            }
//
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleBootTraceableMicroserviceApplication.class, args);
    }
}

@MessageEndpoint
class MessageProcessor {

    @Autowired
    SimpleBootTraceableMicroserviceApplication app;

    private Logger log = LoggerFactory.getLogger(getClass());

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void onMessage(String msg) {
        this.log.info("received message: '" + msg + "'.");
        app.invocationTimes ++;
    }
}
