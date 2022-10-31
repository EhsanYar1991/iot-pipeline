/*
 * Copyright (c) Worldline 2021 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.yar.iotpipeline.tests;

import com.yar.iotpipeline.domain.RecordDocument;
import com.yar.iotpipeline.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebFluxTest
public abstract class AbstractIntegrationTest {

    @MockBean
    private RecordRepository recordRepository;

    @BeforeEach
    void mockCouch() {
        when(recordRepository.save(any())).thenReturn(new RecordDocument());
    }

}
