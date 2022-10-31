package com.yar.iotpipeline.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yar.iotpipeline.repository.RecordRepository;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * Kafka Consumer Channel Configuration
 * */
@Configuration
@Profile({"kafka-producer", "kafka-consumer"})
public class ConsumerChannelConfig {

    @Value("${kafka.server}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String springIntegrationKafkaTopic;

    /**
     * Create consumer channel bean
     *
     * @return {@link DirectChannel} bean
     * */
    @Bean
    public DirectChannel consumerChannel() {
        return new DirectChannel();
    }

    /**
     * Create Kafka Message Driven Channel Adapter Bean
     *
     * @return {@link KafkaMessageDrivenChannelAdapter} bean
     * */
    @Bean
    public KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter() {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(kafkaListenerContainer());
        kafkaMessageDrivenChannelAdapter.setOutputChannel(consumerChannel());
        return kafkaMessageDrivenChannelAdapter;
    }

    /**
     * Create Kafka Message Handler Bean
     *
     * @param recordRepository The record repository
     * @return {@link KafkaMessageHandler} bean
     * */
    @Bean
    @ServiceActivator(inputChannel = "consumerChannel")
    public KafkaMessageHandler kafkaConsumerMessageHandler(final RecordRepository recordRepository,
            final ObjectMapper objectMapper) {
        return new KafkaMessageHandler(recordRepository, objectMapper);
    }

    /**
     * Create Kafka Listener Container Bean
     *
     * @return {@link ConcurrentMessageListenerContainer} bean
     * */
    @Bean
    public ConcurrentMessageListenerContainer<String, String> kafkaListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(springIntegrationKafkaTopic);
        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }

    /**
     * Create Consumer Factory Bean
     *
     * @return {@link ConsumerFactory} bean
     * */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     * Create Consumer Configs Bean
     *
     * @return {@link Map} bean
     * */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "spring-integration");
        // automatically reset the offset to the earliest offset
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }
}
