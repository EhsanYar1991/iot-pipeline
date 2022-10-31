package com.yar.iotpipeline.config.simulation;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Simulation Scheduler.
 * In this simulation the endpoint of sending data will be called constantly based on the scheduler time( 1 second).
 */
@Slf4j
public class SimulationScheduler {

    @Value("${mqtt.application.host}")
    private String serverAddress;

    /**
     * Calling and sending the simulated mock data
     */
    @Scheduled(fixedDelay = 1000)
    public void callSendEndpoint() {
        final String url = requestUrlBuilder();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            log.error("Calling {} failed. error: {}", url, e.getMessage());
        }
    }

    private String requestUrlBuilder() {
        Random random = new Random();
        return new StringBuilder(this.serverAddress) // Server Address
                .append("/send/") // Endpoint
                .append(UUID.randomUUID()) // Random UUID as a client ID
                .append("?") // Query param separator
                .append("thermostat=").append(random.nextInt(100)).append("&") // thermostat
                .append("heartRateMeter=").append(random.nextInt(50)).append("&") //  heart rate meter
                .append("carFuel=").append(random.nextInt(50)).append("&") //  heart rate meter
                .append("latitude=").append(random.nextDouble()).append("&") //  latitude
                .append("longitude=").append(random.nextDouble()) // longitude
                .toString();
    }


}
