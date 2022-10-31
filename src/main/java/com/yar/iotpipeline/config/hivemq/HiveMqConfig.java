package com.yar.iotpipeline.config.hivemq;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

/**
 * HiveMQ Configuration
 */
@Configuration
@Profile({"mqtt-producer", "mqtt-consumer"})
public class HiveMqConfig {

    /**
     * Create client factory bean
     *
     * @param host The HiveMQ host
     * @return {@link MqttPahoClientFactory}
     */
    @Bean
    MqttPahoClientFactory clientFactory(@Value("${hivemq.host}") String host) {
        var factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{host});
        factory.setConnectionOptions(options);
        return factory;
    }

}
