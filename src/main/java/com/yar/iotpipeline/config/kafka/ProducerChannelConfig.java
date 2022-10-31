package com.yar.iotpipeline.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageHandler;

/**
 * The Kafka Producer Channel Configuration
 * */
@Configuration
@Profile({"mqtt-consumer","kafka-producer"})
public class ProducerChannelConfig {

    @Value("${kafka.server}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String topic;

    /**
     * Create the producer channel bean
     *
     * @return {@link DirectChannel} bean
     * */
    @Bean
    public DirectChannel producerChannel() {
        return new DirectChannel();
    }

    /**
     * Create the kafka message handler bean
     *
     * @return {@link MessageHandler} bean
     * */
    @Bean
    @ServiceActivator(inputChannel = "producerChannel")
    public MessageHandler kafkaProducerMessageHandler() {
        KafkaProducerMessageHandler<String, String> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate());
        handler.setTopicExpression(new LiteralExpression(topic));
        handler.setMessageKeyExpression(new LiteralExpression("kafka-integration"));
        return handler;
    }

    /**
     * Create the kafka template bean
     *
     * @return {@link KafkaTemplate} bean
     * */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Create the producer factory bean
     *
     * @return {@link ProducerFactory} bean
     * */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // introduce a delay on send to allow more messages to accumulate
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        return properties;
    }
}
