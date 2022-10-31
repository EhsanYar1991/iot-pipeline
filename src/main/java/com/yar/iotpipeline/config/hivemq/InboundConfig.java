package com.yar.iotpipeline.config.hivemq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHandler;

/**
 * Inbound Configuration
 */
@Configuration
@RequiredArgsConstructor
@Profile({"mqtt-consumer", "kafka-producer"})
@Slf4j
public class InboundConfig {


    /**
     * Create inbound adaptor bean
     *
     * @param topic         The hiveMQ topic
     * @param clientFactory The {@link MqttPahoClientFactory} bean
     * @return {@link MqttPahoMessageDrivenChannelAdapter}
     */
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inboundAdaptor(@Value("${hivemq.topic}") String topic, MqttPahoClientFactory clientFactory) {
        return new MqttPahoMessageDrivenChannelAdapter("consumer", clientFactory, topic);
    }

    /**
     * Create inbound flow bean
     *
     * @param inboundAdaptor The {@link MqttPahoMessageDrivenChannelAdapter} bean
     * @return {@link IntegrationFlow}
     */
    @Bean
    public IntegrationFlow inboundFlow(final MqttPahoMessageDrivenChannelAdapter inboundAdaptor,
            final KafkaTemplate<String, String> kafkaTemplate,
            final MessageHandler kafkaProducerMessageHandler,
            @Value("${kafka.topic}") String topic) {
        return IntegrationFlows
                .from(inboundAdaptor)
                .handle(kafkaProducerMessageHandler)
                .get();
    }
}
