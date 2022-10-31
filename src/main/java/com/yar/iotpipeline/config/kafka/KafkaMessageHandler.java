package com.yar.iotpipeline.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yar.iotpipeline.domain.RecordDocument;
import com.yar.iotpipeline.repository.RecordRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

/**
 * The Kafka message handler
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageHandler implements MessageHandler {

    private final RecordRepository recordRepository;
    private final ObjectMapper objectMapper;

    /**
     * Custom implementation of message handling
     */
    @SneakyThrows
    @Override
    public void handleMessage(Message<?> message) {
        log.info("received message='{}'", message);
        Object payload = message.getPayload();
        Map<String, Object> payloadMap = objectMapper.readValue(payload.toString(), Map.class);
        RecordDocument recordDocument = new RecordDocument(payloadMap);
        Optional.ofNullable(message.getHeaders().get("kafka_receivedTimestamp")).ifPresent(kafka_receivedTimestamp -> {
            recordDocument.put("timeStamp", Instant.ofEpochMilli((Long) kafka_receivedTimestamp));
        });
        recordRepository.save(recordDocument);
        log.info("Record has persisted. Record: {}", recordDocument);
    }
}
