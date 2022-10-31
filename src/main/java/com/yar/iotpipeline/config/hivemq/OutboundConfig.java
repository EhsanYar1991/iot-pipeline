package com.yar.iotpipeline.config.hivemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;


/**
 * Outbound Configuration
 */
@Configuration
@Profile("mqtt-producer")
@RequiredArgsConstructor
@Slf4j
public class OutboundConfig {

    private static final String CLIENT_ID_KEY = "clientId";

    private final ObjectMapper objectMapper;

    /**
     * Create http route function bean
     *
     * @param outboundMessageChannel The {@link MessageChannel} bean
     * @return {@link RouterFunction<ServerResponse>}
     */
    @Bean
    @SneakyThrows
    public RouterFunction<ServerResponse> http(final MessageChannel outboundMessageChannel) {
        return route()
                .GET("/send/{clientId}", request -> {
                    final String clientId = request.pathVariable(CLIENT_ID_KEY);
                    MessageHeaders headers = new MessageHeaders(Map.of(CLIENT_ID_KEY, clientId));
                    Map<String, Object> payload = new HashMap<>(request.queryParams().toSingleValueMap().
                            entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, p -> Double.valueOf(p.getValue()))));
                    payload.put(CLIENT_ID_KEY, clientId);
                    String payloadJson;
                    try {
                        payloadJson = objectMapper.writeValueAsString(payload);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                    Message<String> message = MessageBuilder.createMessage(payloadJson, headers);
                    outboundMessageChannel.send(message);
                    return ServerResponse.ok().build();
                })
                .build();
    }

    /**
     * Create outbound message channel bean
     *
     * @return {@link MessageChannel} bean
     */
    @Bean
    public MessageChannel outboundMessageChannel() {
        return MessageChannels.direct().get();
    }

    /**
     * Create outbound adaptor bean
     *
     * @param topic The HiveMQ topic
     * @return {@link MqttPahoMessageHandler} bean
     */
    @Bean
    public MqttPahoMessageHandler outboundAdaptor(MqttPahoClientFactory factory, @Value("${hivemq.topic}") final String topic) {
        var mqttPahoMessageHandler = new MqttPahoMessageHandler("producer", factory);
        mqttPahoMessageHandler.setDefaultTopic(topic);
        return mqttPahoMessageHandler;
    }

    /**
     * Create outbound adaptor bean
     *
     * @param outboundMessageChannel The {@link MessageChannel} bean
     * @param outboundAdaptor        The {@link MqttPahoMessageHandler} bean
     * @return {@link MqttPahoMessageHandler} bean
     */
    @Bean
    public IntegrationFlow outboundFlow(final MessageChannel outboundMessageChannel, final MqttPahoMessageHandler outboundAdaptor) {
        return IntegrationFlows
                .from(outboundMessageChannel)
                .handle(outboundAdaptor)
                .get();
    }
}
