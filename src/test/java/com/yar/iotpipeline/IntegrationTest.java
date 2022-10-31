package com.yar.iotpipeline;

import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    @Autowired
    private WebTestClient web;

    @Autowired
    private RouterFunction<ServerResponse> http;

    @BeforeEach
    public void setUp() {
        web = WebTestClient.bindToRouterFunction(http).build();
    }

    @Test
    @DisplayName("Should Get Success on a valid request")
    void validRequestIntegrationTest() {
        Random random = new Random();
        web.get().uri("/send/{clientId}", UUID.randomUUID().toString())
                .attribute("thermostat", random.nextInt(100))
                .attribute("heartRateMeter", random.nextInt(50))
                .attribute("carFuel", random.nextInt(100))
                .exchange()
                .expectStatus().isOk();
    }

}
